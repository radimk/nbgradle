/*
 */
package org.nbgradle.netbeans.java.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;

/**
 *
 * @author radim
 */
@NodeFactory.Registration(projectType=NbGradleConstants.PROJECT_TYPE,position=100)
public class JavaNodesFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project p) {
        return new SourcesNodeList(p);
    }

    private static class SourcesNodeList implements NodeList<SourceGroupKey>, ChangeListener {

        private final Project project;

        private final ChangeSupport changeSupport = new ChangeSupport(this);

        public SourcesNodeList(Project proj) {
            project = proj;
        }

        @Override
        public List<SourceGroupKey> keys() {
            if (this.project.getProjectDirectory() == null || !this.project.getProjectDirectory().isValid()) {
                return Collections.emptyList();
            }
            Sources sources = getSources();
            List<SourceGroupKey> result = new ArrayList<>();
            addSourceGroup(result, sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA));
            addSourceGroup(result, sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_RESOURCES));
            addSourceGroup(result, sources.getSourceGroups(NbGradleConstants.SOURCES_TYPE_TEST_JAVA));
            addSourceGroup(result, sources.getSourceGroups(NbGradleConstants.SOURCES_TYPE_TEST_RESOURCES));
            return result;
        }

        private void addSourceGroup(List<SourceGroupKey> keys, SourceGroup[] groups) {
            for (SourceGroup sg : groups) {
                keys.add(new SourceGroupKey(sg));
            }
        }

        @Override
        public void addChangeListener(ChangeListener l) {
            changeSupport.addChangeListener(l);
        }

        @Override
        public void removeChangeListener(ChangeListener l) {
            changeSupport.removeChangeListener(l);
        }

        @Override
        public Node node(SourceGroupKey key) {
            return PackageView.createPackageView(key.group);
            // TODO or copy this to get properties action
            // return new AndroidPackagesNode(key.group, project);
        }

        @Override
        public void addNotify() {
            getSources().addChangeListener(this);
        }

        @Override
        public void removeNotify() {
            getSources().removeChangeListener(this);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            // setKeys(getKeys());
            // The caller holds ProjectManager.mutex() read lock
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    changeSupport.fireChange();
                }
            });
        }

        private Sources getSources() {
            return ProjectUtils.getSources(project);
        }

    }

    private static class SourceGroupKey {

        public final SourceGroup group;
        public final FileObject fileObject;

        SourceGroupKey(SourceGroup group) {
            this.group = group;
            this.fileObject = group.getRootFolder();
        }

        @Override
        public int hashCode() {
            int hash = 5;
            String disp = this.group.getDisplayName();
            hash = 79 * hash + (fileObject != null ? fileObject.hashCode() : 0);
            hash = 79 * hash + (disp != null ? disp.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SourceGroupKey)) {
                return false;
            } else {
                SourceGroupKey otherKey = (SourceGroupKey) obj;

                if (fileObject != otherKey.fileObject && (fileObject == null || !fileObject.equals(otherKey.fileObject))) {
                    return false;
                }
                String thisDisplayName = this.group.getDisplayName();
                String otherDisplayName = otherKey.group.getDisplayName();
                boolean oneNull = thisDisplayName == null;
                boolean twoNull = otherDisplayName == null;
                if (oneNull != twoNull || !thisDisplayName.equals(otherDisplayName)) {
                    return false;
                }
                return true;
            }
        }
    }
}
