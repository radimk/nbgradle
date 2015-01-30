package org.nbgradle.netbeans.java;

import org.nbgradle.netbeans.project.ModelProcessor;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.gradle.tooling.model.GradleProject;
import org.nbgradle.netbeans.models.adapters.GradleProjectBridge;
import org.nbgradle.netbeans.project.AbstractModelProducer;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation2;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;

/**
 *
 * @author radim
 */
@ProjectServiceProvider(service={SourceForBinaryQueryImplementation2.class, ModelProcessor.class},
        projectType=NbGradleConstants.PROJECT_TYPE)
public final class GradleSourceForCompiledClasses extends AbstractModelProducer<GradleProject>
        implements SourceForBinaryQueryImplementation2 {
    private static final Logger LOG = Logger.getLogger(GradleSourceForCompiledClasses.class.getName());

    private final @NonNull Project project;
    private File buildDirectory;
    private File prjDirectory;
    private final LoadingCache<File, CompiledSourceResult> resultsCache = CacheBuilder.newBuilder().
            weakValues().build(new CacheLoader<File, CompiledSourceResult>() {

        @Override
        public CompiledSourceResult load(File key) throws Exception {
            return new CompiledSourceResult(prjDirectory, key.getName());
        }
    });

    public GradleSourceForCompiledClasses(Project project, Lookup baseLookup) {
        super(baseLookup, GradleProject.class);
        this.project = Preconditions.checkNotNull(project);
    }

    @Override
    protected void updateFromModel(GradleProject model) {
        LOG.log(Level.FINE, "update from model {0}", project);
        if (model == null) {
            // maybe it is better to hold previous state.
            return;
        }
        GradleProject currentGradleProject = new GradleProjectBridge(model).moduleForProject(project);
        if (currentGradleProject == null) {
            return;
        }
        buildDirectory = currentGradleProject.getBuildDirectory();
        // TODO this needs to be read from GradleBuild/BasicGradleProject
        prjDirectory = currentGradleProject.getBuildDirectory().getParentFile();
        LOG.log(Level.FINE, "update from model: build dir {0}, prj dir {1}", new Object[]{buildDirectory, prjDirectory});
    }

    @Override
    public Result findSourceRoots2(URL binaryRoot) {
        LOG.log(Level.FINE, "source for binary for {0}", binaryRoot);
        File binaryRootFile = FileUtil.archiveOrDirForURL(binaryRoot);
        if (binaryRootFile == null) {
            return null;
        }

        if (!isParentOrSame(buildDirectory, binaryRootFile) ||
                !new File(buildDirectory, "classes").equals(binaryRootFile.getParentFile())) {
            return null;
        }
        try {
            // assume have build/classes/<sourceSetName> and will translate to /src/<sourceSetName>/java
            return resultsCache.get(binaryRootFile);
        } catch (ExecutionException ex) {
            LOG.log(Level.FINE, "cannot compute source location for binaries in " + binaryRoot, ex);
            return null;
        }
    }

    @Override
    public SourceForBinaryQuery.Result findSourceRoots(URL binaryRoot) {
        return findSourceRoots2(binaryRoot);
    }

    private static boolean isParentOrSame(File parent, File child) {
        for (File current = child; current != null; current = current.getParentFile()) {
            if (current.equals(parent)) {
                return true;
            }
        }
        return false;
    }

    private static class CompiledSourceResult implements SourceForBinaryQueryImplementation2.Result {
        private final ChangeSupport cs = new ChangeSupport(this);
        private final FileObject root;

        private CompiledSourceResult(File prjDirectory, String name) {
            root = FileUtil.toFileObject(new File(prjDirectory, "src" + File.separatorChar + name + File.separatorChar + "java"));
            LOG.log(Level.FINE, "Created source for binary for {0} pointing to {1}", new Object[] {name, root});
        }

        @Override
        public boolean preferSources() {
            return true;
        }

        @Override
        public FileObject[] getRoots() {
            return new FileObject[] {root};
        }

        @Override
        public void addChangeListener(ChangeListener l) {
            cs.addChangeListener(l);
        }

        @Override
        public void removeChangeListener(ChangeListener l) {
            cs.removeChangeListener(l);
        }
    }
}
