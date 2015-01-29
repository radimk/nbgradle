package org.nbgradle.netbeans.java;

import java.io.File;
import java.io.IOException;
import org.nbgradle.netbeans.project.GradleProjectImporter;
import org.nbgradle.netbeans.project.model.DefaultGradleBuildSettings;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import static org.junit.Assert.assertNotNull;

/**
 *
 * @author radim
 */
public class GradleProjectFixture {
    private final File dir;

    public GradleProjectFixture(File dir) {
        this.dir = dir;
    }

    public Project importAndFindProject() throws IOException {
        FileObject prjDirFo = FileUtil.toFileObject(FileUtil.normalizeFile(dir));
        DefaultGradleBuildSettings buildSettings = new DefaultGradleBuildSettings();
        GradleProjectImporter importer = new GradleProjectImporter();
        importer.importProject(buildSettings, dir);
        prjDirFo.refresh();
        ProjectManager.getDefault().clearNonProjectCache();
        Project project = ProjectManager.getDefault().findProject(prjDirFo);
        assertNotNull("Project in " + dir, project);
        return project;
    }
}
