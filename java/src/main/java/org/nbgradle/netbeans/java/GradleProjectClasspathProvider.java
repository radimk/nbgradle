package org.nbgradle.netbeans.java;

import org.nbgradle.netbeans.project.ModelProcessor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.idea.IdeaContentRoot;
import org.gradle.tooling.model.idea.IdeaDependency;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaModuleDependency;
import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency;
import org.gradle.tooling.model.idea.IdeaSourceDirectory;
import org.nbgradle.netbeans.models.adapters.IdeaModelBridge;
import org.nbgradle.netbeans.project.AbstractModelProducer;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.PathResourceImplementation;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.java.classpath.support.CompositePathResourceBase;
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

    private final SourcePathResources sourceMain, sourceTest, classesMain, classesTest, 
            compileMain, compileTest, exported;
    private final MergedPathResources imported;
    private final ClassPath boot, compileMainCp, compileTestCp;

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
        exported  = new SourcePathResources("exported");
        imported = new MergedPathResources();
        compileMainCp = ClassPathSupport.createProxyClassPath(
                imported.classpath, compileMain.classpath);
        compileTestCp = ClassPathSupport.createProxyClassPath(
                compileTest.classpath, classesMain.classpath, compileMainCp);
    }

    @Override
    protected void updateFromModel(IdeaProject ideaModel) {
        if (ideaModel == null) {
            // maybe it is better to hold previous state.
            return;
        }

        IdeaModule module = new IdeaModelBridge(ideaModel).moduleForProject(project);
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
        List<File> exportedLibs = new ArrayList<>();
        List<PathResourceImplementation> importedPathResources = new ArrayList<>();
        for (IdeaDependency dep : module.getDependencies()) {
            if (dep instanceof IdeaSingleEntryLibraryDependency) {
                IdeaSingleEntryLibraryDependency lib = (IdeaSingleEntryLibraryDependency) dep;
                if (dep.getExported()) {
                    exportedLibs.add(lib.getFile());
                }
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
            } else if (dep instanceof IdeaModuleDependency) {
                IdeaModuleDependency moduleDep = (IdeaModuleDependency) dep;
                Project depProject = findNbProject(moduleDep.getDependencyModule().getGradleProject());
                if (depProject != null) {
                    PathResourceImplementation exportedRoots =
                            depProject.getLookup().lookup(RegisteredClassPathProvider.class).getExportedRoots();
                    if (exportedRoots != null) {
                        importedPathResources.add(exportedRoots);
                        LOG.log(Level.FINE, "Project {0} depends on {1} that exports {2}",
                                new Object[] {project, depProject, Arrays.toString(exportedRoots.getRoots())});
                    }
                }
            }
        }
        sourceMain.setRootDirs(srcMainDirs);
        sourceTest.setRootDirs(srcTestDirs);
        classesMain.setRootDirs(classesMainDirs);
        classesTest.setRootDirs(classesTestDirs);
        compileMain.setRootDirs(srcMainLibs);
        compileTest.setRootDirs(srcTestLibs);
        exportedLibs.addAll(classesMainDirs);
        exported.setRootDirs(exportedLibs);
        LOG.log(Level.FINE, "Project {0} exports {1}", new Object[] {project, exported.srcDirs});
        imported.setImportedDependencies(importedPathResources);
        LOG.log(Level.FINE, "Project {0} compile main full classpath {1}", new Object[] {project, Arrays.toString(compileMainCp.getRoots())});
    }

    @Override
    public ClassPath findClassPath(FileObject fo, String type) {
        if (inSources(fo, sourceMain.classpath)) {
            return mainClassPath(type);
        } else if (inSources(fo, sourceTest.classpath)) {
            return testClassPath(type);
        }
        return null;
    }

    @Override
    public PathResourceImplementation getExportedRoots() {
        return exported;
    }

    private ClassPath testClassPath(String type) {
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

    private ClassPath mainClassPath(String type) {
        switch (type) {
        case ClassPath.SOURCE:
            return sourceMain.classpath;
        case ClassPath.COMPILE:
        case ClassPath.EXECUTE:
            return compileMainCp;
        case ClassPath.BOOT:
            return boot;
        default:
            return null;
        }
    }

    @Override
    public ClassPath findClassPath(String sourceType, String type) {
        if (null != sourceType) {
            switch (sourceType) {
            case JavaProjectConstants.SOURCES_TYPE_JAVA:
                return mainClassPath(type);
            case NbGradleConstants.SOURCES_TYPE_TEST_JAVA:
                return testClassPath(type);
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
        pathRegistry.register(ClassPath.COMPILE, new ClassPath[] {compileMainCp, compileTestCp});
        pathRegistry.register(ClassPath.EXECUTE, new ClassPath[] {compileMainCp, compileTestCp});
    }

    @Override
    public void unregister() {
        GlobalPathRegistry pathRegistry = GlobalPathRegistry.getDefault();
        pathRegistry.unregister(ClassPath.SOURCE, new ClassPath[] {sourceMain.classpath, sourceTest.classpath});
        pathRegistry.unregister(ClassPath.COMPILE, new ClassPath[] {compileMainCp, compileTestCp});
        pathRegistry.unregister(ClassPath.EXECUTE, new ClassPath[] {compileMainCp, compileTestCp});
        pathRegistry.unregister(ClassPath.BOOT, new ClassPath[] {boot});
    }

    private void tryAddClassesDir(List<File> classesDirs, File srcDir) {
        if (srcDir.getParentFile() == null
                || srcDir.getParentFile().getParentFile() == null
                || !"src".equals(srcDir.getParentFile().getParentFile().getName())) {
            return;
        }
        String sourceSetName = srcDir.getParentFile().getName();
        String sourceTypeName = "resources".equals(srcDir.getName()) ? "resources" : "classes";
        File classesDir = new File(
                FileUtil.toFile(project.getProjectDirectory()),
                "build" + File.separator + sourceTypeName + File.separator + sourceSetName);
        classesDirs.add(classesDir);
    }

    private static Project findNbProject(GradleProject gradleProject) {
        try {
            // TODO again this should use BacicGradleProject
            Project project = ProjectManager.getDefault().findProject(
                    FileUtil.toFileObject(gradleProject.getBuildDirectory().getParentFile()));
            return project;
        } catch (IOException | IllegalArgumentException ex) {
            LOG.log(Level.INFO, "cannot find a project for " + gradleProject, ex);
            return null;
        }
    }

    private class MergedPathResources extends CompositePathResourceBase {
        final ClassPath classpath;
        private Iterable<PathResourceImplementation> importedDependencies = Collections.emptyList();

        public MergedPathResources() {
            classpath = ClassPathSupport.createClassPath(Collections.singletonList(this));
        }

        @Override
        protected ClassPathImplementation createContent() {
            LOG.log(Level.INFO, "imported dependencies in {0}: {1}", new Object[] {project, importedDependencies});
            return ClassPathSupport.createClassPathImplementation(Lists.newArrayList(importedDependencies));
        }

        public void setImportedDependencies(Iterable<PathResourceImplementation> importedDependencies) {
            this.importedDependencies = importedDependencies;
            firePropertyChange(PROP_ROOTS, null, null);
        }
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
