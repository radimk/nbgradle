package org.nbgradle.netbeans.java;

import org.nbgradle.netbeans.project.ModelProcessor;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gradle.api.Nullable;
import org.gradle.tooling.model.idea.IdeaContentRoot;
import org.gradle.tooling.model.idea.IdeaDependency;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency;
import org.gradle.tooling.model.idea.IdeaSourceDirectory;
import org.nbgradle.netbeans.models.ModelProvider;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.java.classpath.support.PathResourceBase;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author radim
 */
@ProjectServiceProvider(service={ClassPathProvider.class, ModelProcessor.class}, projectType=NbGradleConstants.PROJECT_TYPE)
public final class GradleProjectClasspathProvider implements ClassPathProvider, ModelProcessor {
    private static final Logger LOG = Logger.getLogger(GradleProjectClasspathProvider.class.getName());

    private final @NonNull Project project;
    private final @NonNull ModelProvider modelProvider;

    private final SourcePathResources sourceMain, sourceTest, compileMain, compileTest;
    private final ClassPath boot, compileTestCp, executeMain, executeTest;

    public GradleProjectClasspathProvider(Project project, Lookup baseLookup) {
        this.project = Preconditions.checkNotNull(project);
        modelProvider = baseLookup.lookup(ModelProvider.class);
        boot = createBoot();
        sourceMain = new SourcePathResources("main sources");
        sourceTest = new SourcePathResources("test libs");
        compileMain = new SourcePathResources("main libs");
        compileTest  = new SourcePathResources("test libs");
        compileTestCp = ClassPathSupport.createProxyClassPath(compileTest.classpath, sourceMain.classpath, compileMain.classpath);
        executeMain = executeTest = null;
    }

    @Override
    public void loadFromGradle(final Phaser phaser) {
        phaser.register();
        ListenableFuture<IdeaProject> ideaModel = modelProvider.getModel(IdeaProject.class);
        Futures.addCallback(ideaModel, new FutureCallback<IdeaProject>() {

            @Override
            public void onSuccess(IdeaProject model) {
                try {
                    LOG.log(Level.INFO, "Processing classpath from IDEA");
                    updateClasspath(model);
                } finally {
                    phaser.arriveAndDeregister();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                try {
                    LOG.log(Level.INFO, "Cannot get classpath using idea model", t);
                    updateClasspath(null);
                } finally {
                    phaser.arriveAndDeregister();
                }
            }
        });
    }

    private void updateClasspath(@Nullable IdeaProject ideaModel) {
        if (ideaModel == null) {
            // maybe it is better to hold previous state.
            return;
        }
        IdeaModule module = Iterables.find(
                ideaModel.getModules(),
                new Predicate<IdeaModule>() {
                    @Override
                    public boolean apply(IdeaModule input) {
                        return ":".equals(input.getGradleProject().getPath());
                    }
                },
                null);
        if (module == null) {
            LOG.log(Level.INFO, "Cannot get classpath for this subproject");
            return;
        }

        List<File> srcMainDirs = new ArrayList<>();
        List<File> srcTestDirs = new ArrayList<>();
        for (IdeaContentRoot contentRoot : module.getContentRoots()) {
            for (IdeaSourceDirectory ideaSrcDir : contentRoot.getSourceDirectories()) {
                srcMainDirs.add(ideaSrcDir.getDirectory());
            }
            for (IdeaSourceDirectory ideaSrcDir : contentRoot.getTestDirectories()) {
                srcTestDirs.add(ideaSrcDir.getDirectory());
            }
        }
        List<File> srcMainLibs = new ArrayList<>();
        List<File> srcTestLibs = new ArrayList<>();
        for (IdeaDependency dep : module.getDependencies()) {
            if (dep instanceof IdeaSingleEntryLibraryDependency) {
                IdeaSingleEntryLibraryDependency lib = (IdeaSingleEntryLibraryDependency) dep;
                if ("COMPILE".equals(lib.getScope().getScope())) {
                    srcMainLibs.add(lib.getFile());
                } else if ("TEST".equals(lib.getScope().getScope())) {
                    srcTestLibs.add(lib.getFile());
                } else {
                    LOG.log(Level.INFO, "Library {0} not processed yet", lib);
                }
            }
        }
        sourceMain.setRootDirs(srcMainDirs);
        sourceTest.setRootDirs(srcTestDirs);
        compileMain.setRootDirs(srcMainLibs);
        compileTest.setRootDirs(srcTestLibs);
    }

    @Override
    public ClassPath findClassPath(FileObject fo, String type) {
        if (inSources(fo, sourceMain.classpath)) {
            switch (type) {
            case ClassPath.SOURCE:
                return sourceMain.classpath;
            case ClassPath.COMPILE:
                return compileMain.classpath;
            case ClassPath.BOOT:
                return boot;
            default:
                return null;
            }
        } else if (inSources(fo, sourceTest.classpath)) {
            switch (type) {
            case ClassPath.SOURCE:
                return sourceTest.classpath;
            case ClassPath.COMPILE:
                return compileTestCp;
            case ClassPath.BOOT:
                return boot;
            default:
                return null;
            }
        }
        return null;
    }

    private ClassPath createBoot() {
        return JavaPlatformManager.getDefault().getDefaultPlatform().getBootstrapLibraries();
    }

    private boolean inSources(FileObject fo, ClassPath cp) {
        return cp.contains(fo);
    }

    private class SourcePathResources extends PathResourceBase {
        final ClassPath classpath;
        Iterable<File> srcDirs = Collections.emptyList();
        private final String group;

        public SourcePathResources(String group) {
            this.group = group;
            classpath = ClassPathSupport.createClassPath(Collections.singletonList(this));
        }

        @Override
        public URL[] getRoots() {
            List<URL> roots = Lists.newArrayList();
            try {
                for (File srcDir : srcDirs) {
                    roots.add(FileUtil.urlForArchiveOrDir(srcDir));
                }
            } catch (Exception ex) {
                LOG.log(Level.FINE, "stub claspath cannot be created", ex);
            }
            LOG.log(Level.INFO, "source roots in {0}/{1}: {2}", new Object[] {project, group, roots});
            return roots.toArray(new URL[0]);
        }

        @Override
        public ClassPathImplementation getContent() {
            // semi-deprecated: should not be called
            return null;
        }

        private void setRootDirs(List<File> srcDirs) {
            this.srcDirs = srcDirs;
            firePropertyChange(PROP_ROOTS, null, null);
        }
    }
}
