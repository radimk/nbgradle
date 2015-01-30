package org.nbgradle.netbeans.java;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import org.nbgradle.netbeans.models.GradleRunner;
import org.nbgradle.netbeans.project.GradleProjectImporter;
import org.nbgradle.netbeans.project.lookup.ProjectLoadingHook;
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
    private Project project;

    public GradleProjectFixture(File dir) {
        this.dir = dir;
    }

    public Project importAndFindRootProject() throws IOException {
        FileObject prjDirFo = FileUtil.toFileObject(FileUtil.normalizeFile(dir));
        DefaultGradleBuildSettings buildSettings = new DefaultGradleBuildSettings();
        GradleProjectImporter importer = new GradleProjectImporter();
        importer.importProject(buildSettings, dir);
        prjDirFo.refresh();
        ProjectManager.getDefault().clearNonProjectCache();
        project = ProjectManager.getDefault().findProject(prjDirFo);
        assertNotNull("Project in " + dir, project);

        project.getLookup().lookup(ProjectLoadingHook.class).projectOpened();
        project.getLookup().lookup(ProjectLoadingHook.class).phaser.arriveAndAwaitAdvance();
        return project;
    }

    /**
     * Finds a sub-project in a Gradle build assuming that it can be imported.
     */
    public Project findSubProject(String relativePath) throws IOException {
        FileObject projectDir = FileUtil.toFileObject(new File(dir, relativePath));
        Project subProject = ProjectManager.getDefault().findProject(projectDir);
        if (subProject != null) {
            subProject.getLookup().lookup(ProjectLoadingHook.class).projectOpened();
            subProject.getLookup().lookup(ProjectLoadingHook.class).phaser.arriveAndAwaitAdvance();
        }
        return subProject;
    }

    public GradleProjectFixture build(String... tasks) {
        Preconditions.checkNotNull(project);
        GradleRunner runner = project.getLookup().lookup(GradleRunner.class);
        runner.newBuild().forTasks(tasks).run();
        return this;
    }
}
