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
import org.netbeans.spi.java.classpath.ClassPathProvider;

import static org.junit.Assert.*;

public class GradleProjectClasspathProviderTest {
    @Rule public final TestNameTestDirectoryProvider temporaryFolder = new TestNameTestDirectoryProvider();
    @Rule public Sample sample = new Sample(temporaryFolder);

    private static Project importAndOpenProject(File prjDir) throws IOException {
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
    public void quickstart() throws IOException {
        Project project = importAndOpenProject(sample.getDir().toFile());
        assertNotNull(project.getLookup().lookup(ClassPathProvider.class));
        project.getLookup().lookup(ProjectLoadingHook.class).phaser.arriveAndAwaitAdvance();
    }
}
