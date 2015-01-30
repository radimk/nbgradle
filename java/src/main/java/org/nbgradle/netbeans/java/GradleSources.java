package org.nbgradle.netbeans.java;

import com.google.common.base.Preconditions;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.event.ChangeListener;
import org.gradle.tooling.model.idea.IdeaContentRoot;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.idea.IdeaSourceDirectory;
import org.nbgradle.netbeans.models.adapters.IdeaModelBridge;
import org.nbgradle.netbeans.project.AbstractModelProducer;
import org.nbgradle.netbeans.project.ModelProcessor;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.netbeans.spi.project.support.GenericSources;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;

/**
 *
 * @author radim
 */
@ProjectServiceProvider(service={Sources.class, ModelProcessor.class}, projectType=NbGradleConstants.PROJECT_TYPE)
public class GradleSources extends AbstractModelProducer<IdeaProject> implements Sources {
    private static final Logger LOG = Logger.getLogger(GradleSources.class.getName());

    private final Project project;
    private final ChangeSupport cs = new ChangeSupport(this);
    private Map<String, SourceGroup[]> sourcesByType = new HashMap<>();

    public GradleSources(Project project, Lookup baseLookup) {
        super(baseLookup, IdeaProject.class);
        this.project = Preconditions.checkNotNull(project);
    }

    @Override
    public SourceGroup[] getSourceGroups(String type) {
        if (type.equals(Sources.TYPE_GENERIC)) {
            ProjectInformation info = ProjectUtils.getInformation(project);
            return new SourceGroup[]{GenericSources.group(
                    project, project.getProjectDirectory(), info.getName(), info.getDisplayName(), null, null)};
        } else {
            SourceGroup[] sgs = sourcesByType.get(type);
            return sgs != null ? sgs : new SourceGroup[0];
        }
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        cs.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        cs.removeChangeListener(listener);
    }

    @Override
    protected void updateFromModel(IdeaProject model) {
        if (model == null) {
            sourcesByType.clear();
        } else {
            IdeaModule module = new IdeaModelBridge(model).moduleForProject(project);
            if (module == null) {
                LOG.log(Level.INFO, "No source group for {0}", project);
                sourcesByType.clear();
            } else {
                sourcesByType = createSources(module);
            }
        }
        cs.fireChange();
    }

    private Map<String, SourceGroup[]> createSources(IdeaModule module) {
        Map<String, SourceGroup[]> newSources = new HashMap<>();
        List<SourceGroup> groupsJava = new ArrayList<>();
        List<SourceGroup> groupsResources = new ArrayList<>();
        List<SourceGroup> groupsTestJava = new ArrayList<>();
        List<SourceGroup> groupsTestResources = new ArrayList<>();
        for (IdeaContentRoot contentRoot : module.getContentRoots()) {
            for (IdeaSourceDirectory ideaSrcDir : contentRoot.getSourceDirectories()) {
                final File srcDir = ideaSrcDir.getDirectory();
                FileObject srcDirFo = FileUtil.toFileObject(srcDir);
                if (srcDirFo != null) {
                    final String srcName = srcDir.getName();
                    if (!"resources".equals(srcName)) {
                        groupsJava.add(GenericSources.group(
                                project, srcDirFo, srcName, "Source Packages " + srcName, null, null));
                    } else {
                        groupsResources.add(GenericSources.group(
                                project, srcDirFo, srcName, "Resources", null, null));
                    }
                }
            }
            for (IdeaSourceDirectory ideaSrcDir : contentRoot.getTestDirectories()) {
                final File srcDir = ideaSrcDir.getDirectory();
                FileObject srcDirFo = FileUtil.toFileObject(srcDir);
                if (srcDirFo != null) {
                    final String srcName = srcDir.getName();
                    if (!"resources".equals(srcName)) {
                        groupsTestJava.add(GenericSources.group(
                                project, srcDirFo, srcName, "Test Packages " + srcName, null, null));
                    } else {
                        groupsTestResources.add(GenericSources.group(
                                project, srcDirFo, srcName, "Test Resources", null, null));
                    }
                }
            }
        }
        LOG.log(Level.FINE, "source groups {0}: {1}", new Object[] {JavaProjectConstants.SOURCES_TYPE_JAVA, groupsJava});
        newSources.put(JavaProjectConstants.SOURCES_TYPE_JAVA, groupsJava.toArray(new SourceGroup[0]));
        newSources.put(JavaProjectConstants.SOURCES_TYPE_RESOURCES, groupsResources.toArray(new SourceGroup[0]));
        newSources.put(NbGradleConstants.SOURCES_TYPE_TEST_JAVA, groupsTestJava.toArray(new SourceGroup[0]));
        newSources.put(NbGradleConstants.SOURCES_TYPE_TEST_RESOURCES, groupsTestResources.toArray(new SourceGroup[0]));
        return newSources;
    }

    /**
     * SourceGroup that accept all sources including generated (NOT_SHARABLE).
     */
    private static final class AnySourceGroup implements SourceGroup {

        private final Project p;
        private final FileObject rootFolder;
        private final String name;
        private final String displayName;
        private final Icon icon;
        private final Icon openedIcon;

        AnySourceGroup(Project p, FileObject rootFolder, String name, String displayName, Icon icon, Icon openedIcon) {
            this.p = p;
            this.rootFolder = rootFolder;
            this.name = name;
            this.displayName = displayName;
            this.icon = icon;
            this.openedIcon = openedIcon;
        }

        @Override
        public FileObject getRootFolder() {
            return rootFolder;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public Icon getIcon(boolean opened) {
            return opened ? icon : openedIcon;
        }

        @Override
        public boolean contains(FileObject file) {
            if (file != rootFolder && !FileUtil.isParentOf(rootFolder, file)) {
                return false;
            }
            if (file.isFolder() && file != p.getProjectDirectory() && ProjectManager.getDefault().isProject(file)) {
                // #67450: avoid actually loading the nested project.
                return false;
            }
            if (FileOwnerQuery.getOwner(file) != p) {
                return false;
            }
            // MIXED, UNKNOWN, and SHARABLE -> include it
            return true; // SharabilityQuery.getSharability(file) != SharabilityQuery.Sharability.NOT_SHARABLE;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener l) {
            // XXX should react to ProjectInformation changes
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener l) {
            // XXX
        }

        @Override
        public String toString() {
            return "AnySourceGroup[name=" + name + ",rootFolder=" + rootFolder + "]";
        }
    }
}
