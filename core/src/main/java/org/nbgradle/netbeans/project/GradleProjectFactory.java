package org.nbgradle.netbeans.project;

import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectFactory2;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@org.openide.util.lookup.ServiceProvider(service = ProjectFactory.class)
public class GradleProjectFactory implements ProjectFactory2 {
    private static final Logger LOG = Logger.getLogger(GradleProjectFactory.class.getName());

    @StaticResource
    public static final String PROJECT_ICON_PATH = "org/nbgradle/netbeans/project/gradle.png";

    @Override
    public ProjectManager.Result isProject2(FileObject projectDirectory) {
        LOG.log(Level.FINE, "check " + projectDirectory);
        if (projectDirectory.getFileObject(NbGradleConstants.NBGRADLE_BUILD_XML) != null) {
            return new ProjectManager.Result(ImageUtilities.loadImageIcon(PROJECT_ICON_PATH, true));
        }
        return null;
    }

    @Override
    public boolean isProject(FileObject projectDirectory) {
        return isProject2(projectDirectory) != null;
    }

    @Override
    public Project loadProject(FileObject projectDirectory, ProjectState state) throws IOException {
        if (!isProject(projectDirectory)) {
            return null;
        }
        return new NbGradleProject(projectDirectory, FileUtil.toFile(projectDirectory));
    }

    @Override
    public void saveProject(Project project) throws IOException, ClassCastException {

    }
}
