package org.nbgradle.netbeans.java;

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
import org.netbeans.spi.java.classpath.ClassPathProvider;

import static org.junit.Assert.*;

public class GradleProjectClasspathProviderTest {
    @Rule public final TestNameTestDirectoryProvider temporaryFolder = new TestNameTestDirectoryProvider();
    @Rule public Sample sample = new Sample(temporaryFolder);

    @Test
    @UsesSample("java/quickstart")
    public void quickstart() throws IOException {
        FileObject prjDir = FileUtil.toFileObject(FileUtil.normalizeFile(sample.getDir().toFile()));
        assertNotNull(prjDir);
        Project project = ProjectManager.getDefault().findProject(prjDir);
        assertNull("Not yet a project in " + prjDir, project);

        DefaultGradleBuildSettings buildSettings = new DefaultGradleBuildSettings();
        GradleProjectImporter importer = new GradleProjectImporter();
        importer.importProject(buildSettings, sample.getDir().toFile());
        prjDir.refresh();
        ProjectManager.getDefault().clearNonProjectCache();
        project = ProjectManager.getDefault().findProject(prjDir);
        assertNotNull("Project in " + prjDir, project);

        assertNotNull(project.getLookup().lookup(ClassPathProvider.class));
    }
}
