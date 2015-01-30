package org.nbgradle.netbeans.project;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import org.nbgradle.netbeans.models.GradleBuildSettings;
import org.junit.Rule;
import org.junit.Test;
import org.nbgradle.netbeans.project.model.DefaultGradleBuildSettings;
import org.nbgradle.test.fixtures.Sample;
import org.nbgradle.test.fixtures.TestNameTestDirectoryProvider;
import org.nbgradle.test.fixtures.UsesSample;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ProjectFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

import java.io.IOException;
import java.nio.file.Files;
import org.nbgradle.netbeans.project.lookup.GradleProjectInformation;

import static org.junit.Assert.*;

public class GradleProjectFactoryTest {
    @Rule public final TestNameTestDirectoryProvider temporaryFolder = new TestNameTestDirectoryProvider();
    @Rule public Sample sample = new Sample(temporaryFolder);

    @Test
    public void isRegistred() {
        assertTrue(Iterables.any(
                Lookup.getDefault().lookupAll(ProjectFactory.class),
                Predicates.instanceOf(GradleProjectFactory.class)));
    }

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
    }

    @Test
    @UsesSample("java/multiproject")
    public void multiproject() throws IOException {
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
        assertEquals(":", project.getLookup().lookup(GradleProjectInformation.class).getProjectPath());

        FileObject apiDir = prjDir.getFileObject("api");
        project = ProjectManager.getDefault().findProject(apiDir);
        assertNotNull(":api in " + apiDir, project);
        assertEquals(":api", project.getLookup().lookup(GradleProjectInformation.class).getProjectPath());

        // TODO enable when we recognize project dir without build.gradle
        FileObject sharedDir = prjDir.getFileObject("shared");
        project = ProjectManager.getDefault().findProject(sharedDir);
        assertNotNull(":shared in " + sharedDir, project);
        assertEquals(":shared", project.getLookup().lookup(GradleProjectInformation.class).getProjectPath());
    }
}
