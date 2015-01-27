package org.nbgradle.netbeans.java;

import com.google.common.base.Function;
import org.nbgradle.netbeans.project.ModelProcessor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gradle.tooling.model.idea.IdeaProject;
import org.nbgradle.netbeans.models.ModelProvider;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.classpath.ClassPathFactory;
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

    private final ClassPath boot, sourceMain, sourceTest, compileMain, compileTest, executeMain, executeTest;

    public GradleProjectClasspathProvider(Project project, Lookup baseLookup) {
        this.project = Preconditions.checkNotNull(project);
        modelProvider = baseLookup.lookup(ModelProvider.class);
        boot = createBoot();
        sourceMain = createSourcePath();
        sourceTest = createSourcePath();
        compileMain = compileTest = executeMain = executeTest = null;
    }

    @Override
    public void loadFromGradle(final Phaser phaser) {
        phaser.register();
        modelProvider.getModel(IdeaProject.class).addListener(new Runnable() {

            @Override
            public void run() {
                LOG.log(Level.INFO, "Processing classpath from IDEA");
                // TODO compute classpath here
                phaser.arriveAndDeregister();
            }
        }, MoreExecutors.sameThreadExecutor());
    }

    @Override
    public ClassPath findClassPath(FileObject fo, String type) {
        if (inSources(fo, sourceMain)) {
            switch (type) {
            case ClassPath.SOURCE:
                return sourceMain;
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

    private ClassPath createSourcePath() {
      return ClassPathSupport.createClassPath(
            Collections.singletonList(new SourcePathResources()));
    }

    private boolean inSources(FileObject fo, ClassPath cp) {
        return cp.contains(fo);
    }

    private class SourcePathResources extends PathResourceBase {

        public SourcePathResources() {
        }

        @Override
        public URL[] getRoots() {
            List<URL> roots = Lists.newArrayList();
            try {
                roots.add(FileUtil.urlForArchiveOrDir(FileUtil.toFile(project.getProjectDirectory().getFileObject("src/main/java"))));
            } catch (Exception ex) {
                LOG.log(Level.FINE, "stub claspath cannot be created", ex);
            }
            LOG.log(Level.FINE, "source roots: {0}", roots);
            return roots.toArray(new URL[0]);
        }

        @Override
        public ClassPathImplementation getContent() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
