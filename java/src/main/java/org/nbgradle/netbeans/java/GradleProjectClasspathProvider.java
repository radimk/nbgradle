package org.nbgradle.netbeans.java;

import org.nbgradle.netbeans.project.ModelProcessor;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gradle.tooling.model.idea.IdeaContentRoot;
import org.gradle.tooling.model.idea.IdeaDependency;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency;
import org.gradle.tooling.model.idea.IdeaSourceDirectory;
import org.nbgradle.netbeans.models.idea.IdeaModelHelper;
import org.nbgradle.netbeans.project.AbstractModelProducer;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
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
@ProjectServiceProvider(service={ClassPathProvider.class, RegisteredClassPathProvider.class, ModelProcessor.class},
        projectType=NbGradleConstants.PROJECT_TYPE)
public final class GradleProjectClasspathProvider extends AbstractModelProducer<IdeaProject>
        implements ClassPathProvider, RegisteredClassPathProvider {
    private static final Logger LOG = Logger.getLogger(GradleProjectClasspathProvider.class.getName());

    private final @NonNull Project project;

    private final SourcePathResources sourceMain, sourceTest, classesMain, classesTest, compileMain, compileTest;
    private final ClassPath boot, compileTestCp, executeMain, executeTest;

    public GradleProjectClasspathProvider(Project project, Lookup baseLookup) {
        super(baseLookup, IdeaProject.class);
        this.project = Preconditions.checkNotNull(project);
        boot = createBoot();
        sourceMain = new SourcePathResources("main sources");
        sourceTest = new SourcePathResources("test libs");
        classesMain = new SourcePathResources("main classes");
        classesTest = new SourcePathResources("test classes");
        compileMain = new SourcePathResources("main libs");
        compileTest  = new SourcePathResources("test libs");
        compileTestCp = ClassPathSupport.createProxyClassPath(
                compileTest.classpath, classesMain.classpath, compileMain.classpath);
        executeMain = executeTest = null;
    }

    @Override
    protected void updateFromModel(IdeaProject ideaModel) {
        if (ideaModel == null) {
            // maybe it is better to hold previous state.
            return;
        }

        IdeaModule module = new IdeaModelHelper(ideaModel).moduleForProject(":");
        if (module == null) {
            LOG.log(Level.INFO, "Cannot get classpath for this subproject");
            return;
        }

        List<File> srcMainDirs = new ArrayList<>();
        List<File> classesMainDirs = new ArrayList<>();
        List<File> srcTestDirs = new ArrayList<>();
        List<File> classesTestDirs = new ArrayList<>();
        for (IdeaContentRoot contentRoot : module.getContentRoots()) {
            for (IdeaSourceDirectory ideaSrcDir : contentRoot.getSourceDirectories()) {
                srcMainDirs.add(ideaSrcDir.getDirectory());
                tryAddClassesDir(classesMainDirs, ideaSrcDir.getDirectory());
            }
            for (IdeaSourceDirectory ideaSrcDir : contentRoot.getTestDirectories()) {
                srcTestDirs.add(ideaSrcDir.getDirectory());
                tryAddClassesDir(classesTestDirs, ideaSrcDir.getDirectory());
            }
        }
        List<File> srcMainLibs = new ArrayList<>();
        List<File> srcTestLibs = new ArrayList<>();
        for (IdeaDependency dep : module.getDependencies()) {
            if (dep instanceof IdeaSingleEntryLibraryDependency) {
                IdeaSingleEntryLibraryDependency lib = (IdeaSingleEntryLibraryDependency) dep;
                switch (lib.getScope().getScope()) {
                case "COMPILE":
                    srcMainLibs.add(lib.getFile());
                    break;
                case "TEST":
                    srcTestLibs.add(lib.getFile());
                    break;
                default:
                    LOG.log(Level.INFO, "Library {0} not processed yet", lib);
                    break;
                }
            }
        }
        sourceMain.setRootDirs(srcMainDirs);
        sourceTest.setRootDirs(srcTestDirs);
        classesMain.setRootDirs(classesMainDirs);
        classesTest.setRootDirs(classesTestDirs);
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
            case ClassPath.EXECUTE:
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
            case ClassPath.EXECUTE:
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
        ClassPath bootstrapLibraries = JavaPlatformManager.getDefault().getDefaultPlatform().getBootstrapLibraries();
        LOG.log(Level.FINE, "Boot classpath for {0} is {1}", new Object[] {project, bootstrapLibraries});
        return bootstrapLibraries;
    }

    private boolean inSources(FileObject fo, ClassPath cp) {
        return cp.contains(fo);
    }

    @Override
    public void register() {
        LOG.log(Level.FINE, "Registering classpaths for {0}", project);
        GlobalPathRegistry pathRegistry = GlobalPathRegistry.getDefault();
        pathRegistry.register(ClassPath.BOOT, new ClassPath[] {boot});
        pathRegistry.register(ClassPath.SOURCE, new ClassPath[] {sourceMain.classpath, sourceTest.classpath});
        pathRegistry.register(ClassPath.COMPILE, new ClassPath[] {compileMain.classpath, compileTestCp});
        pathRegistry.register(ClassPath.EXECUTE, new ClassPath[] {compileMain.classpath, compileTestCp});
    }

    @Override
    public void unregister() {
        GlobalPathRegistry pathRegistry = GlobalPathRegistry.getDefault();
        pathRegistry.unregister(ClassPath.SOURCE, new ClassPath[] {sourceMain.classpath, sourceTest.classpath});
        pathRegistry.unregister(ClassPath.COMPILE, new ClassPath[] {compileMain.classpath, compileTestCp});
        pathRegistry.unregister(ClassPath.EXECUTE, new ClassPath[] {compileMain.classpath, compileTestCp});
        pathRegistry.unregister(ClassPath.BOOT, new ClassPath[] {boot});
    }

    private void tryAddClassesDir(List<File> classesDirs, File srcDir) {
        if (!"java".equals(srcDir.getName()) || srcDir.getParentFile() == null
                || srcDir.getParentFile().getParentFile() == null
                || !"src".equals(srcDir.getParentFile().getParentFile().getName())) {
            return;
        }
        String sourceSetName = srcDir.getParentFile().getName();
        File classesDir = new File(
                FileUtil.toFile(project.getProjectDirectory()),
                "build" + File.separator + "classes" + File.separator + sourceSetName);
        classesDirs.add(classesDir);
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
            for (File srcDir : srcDirs) {
                try {
                    roots.add(FileUtil.urlForArchiveOrDir(srcDir));
                } catch (Exception ex) {
                    LOG.log(Level.FINE, "claspath entry " + srcDir + " cannot be added", ex);
                }
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
