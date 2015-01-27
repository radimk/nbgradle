package org.nbgradle.netbeans.java;

import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.nbgradle.netbeans.project.model.DefaultGradleBuildSettings;
import org.nbgradle.test.fixtures.Sample;
import org.nbgradle.test.fixtures.TestNameTestDirectoryProvider;
import org.nbgradle.test.fixtures.UsesSample;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import java.io.IOException;
import org.nbgradle.netbeans.project.GradleProjectImporter;
import org.nbgradle.netbeans.project.lookup.ProjectLoadingHook;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.java.classpath.ClassPathProvider;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

public class GradleProjectClasspathProviderTest {
    @Rule public final TestNameTestDirectoryProvider temporaryFolder = new TestNameTestDirectoryProvider();
    @Rule public Sample sample = new Sample(temporaryFolder);

    private static Project importAndFindProject(File prjDir) throws IOException {
        FileObject prjDirFo = FileUtil.toFileObject(FileUtil.normalizeFile(prjDir));
        DefaultGradleBuildSettings buildSettings = new DefaultGradleBuildSettings();
        GradleProjectImporter importer = new GradleProjectImporter();
        importer.importProject(buildSettings, prjDir);
        prjDirFo.refresh();
        ProjectManager.getDefault().clearNonProjectCache();
        Project project = ProjectManager.getDefault().findProject(prjDirFo);
        assertNotNull("Project in " + prjDir, project);
        return project;
    }

    @Test
    @UsesSample("java/quickstart")
    public void quickstart() throws Exception {
        Project project = importAndFindProject(sample.getDir().toFile());

        assertNotNull(project.getLookup().lookup(ClassPathProvider.class));
        project.getLookup().lookup(ProjectLoadingHook.class).projectOpened();
        project.getLookup().lookup(ProjectLoadingHook.class).phaser.arriveAndAwaitAdvance();

        FileObject foSrcMainJava = project.getProjectDirectory().getFileObject("src/main/java");
        FileObject foSrcTestJava = project.getProjectDirectory().getFileObject("src/test/java");
        ClassPath mainCp = ClassPath.getClassPath(foSrcMainJava, ClassPath.SOURCE);
        assertThat(mainCp.getRoots()).containsOnlyOnce(foSrcMainJava);
        ClassPath mainBootCp = ClassPath.getClassPath(foSrcMainJava, ClassPath.BOOT);
        // TODO boot CP according to selected platform
        assertThat(mainBootCp).isNotNull();

        ClassPath testCp = ClassPath.getClassPath(foSrcTestJava, ClassPath.SOURCE);
        assertThat(testCp.getRoots()).containsOnlyOnce(foSrcTestJava);
    }
}
