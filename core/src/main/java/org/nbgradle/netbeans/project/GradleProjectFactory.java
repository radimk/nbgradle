package org.nbgradle.netbeans.project;

import java.io.File;
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
import org.nbgradle.netbeans.models.DefaultGradleContextProvider;
import org.nbgradle.netbeans.models.GradleContext;
import org.nbgradle.netbeans.models.GradleContextProvider;
import org.nbgradle.netbeans.project.lookup.GradleProjectInformation;
import org.nbgradle.netbeans.project.lookup.ProjectInfoNode;

@org.openide.util.lookup.ServiceProvider(service = ProjectFactory.class)
public class GradleProjectFactory implements ProjectFactory2 {
    private static final Logger LOG = Logger.getLogger(GradleProjectFactory.class.getName());

    @StaticResource
    public static final String PROJECT_ICON_PATH = "org/nbgradle/netbeans/project/gradle.png";

    private final GradleContextProvider contextProvider = new DefaultGradleContextProvider();

    @Override
    public ProjectManager.Result isProject2(FileObject projectDirectory) {
        LOG.log(Level.FINEST, "check {0}", projectDirectory);
        if (findRootProjectDirectory(projectDirectory) != null) {
            return new ProjectManager.Result(ImageUtilities.loadImageIcon(PROJECT_ICON_PATH, true));
        }
        return null;
    }

    private FileObject findRootProjectDirectory(FileObject projectDirectory) {
        LOG.log(Level.FINEST, "check {0}", projectDirectory);
        if (projectDirectory == null) {
            return null;
        }
        if (projectDirectory.getFileObject(NbGradleConstants.NBGRADLE_BUILD_XML) != null) {
            return projectDirectory;
        }
        if (projectDirectory.getFileObject(NbGradleConstants.BUILD_GRADLE_FILENAME) != null) {
            return findRootProjectDirectory(projectDirectory.getParent());
        }
        Object markerAttr = projectDirectory.getAttribute(NbGradleConstants.NBGRADLE_PROJECT_DIR_ATTR);
        if (markerAttr instanceof String) {
            File rootProjectDir = new File((String) markerAttr);
            if (rootProjectDir.exists()) {
                return FileUtil.toFileObject(rootProjectDir);
            }
        }
        return null;
    }

    @Override
    public boolean isProject(FileObject projectDirectory) {
        return isProject2(projectDirectory) != null;
    }

    @Override
    public Project loadProject(FileObject projectDirectory, ProjectState state) throws IOException {
        FileObject rootProjectDirectory = findRootProjectDirectory(projectDirectory);
        if (rootProjectDirectory == null) {
            return null;
        }
        try {
            File rootProjectDir = FileUtil.toFile(rootProjectDirectory);
            File projectDir = FileUtil.toFile(projectDirectory);
            GradleContext gradleContext = contextProvider.forProject(rootProjectDir);
            ProjectInfoNode currentProject = findProjectInfo(gradleContext.getProjectTreeInformation(), projectDir);
            if (currentProject == null) {
                LOG.log(Level.FINE, "Cannot find project metadata.");
                return null;
            }
            return new NbGradleProject(gradleContext, projectDirectory, currentProject);
        } catch (ProjectImportException pie) {
            LOG.log(Level.FINE, "Cannot load project.", pie);
            return null;
        }
    }

    private ProjectInfoNode findProjectInfo(ProjectInfoNode projectNode, File projectDirectory) {
        if (projectNode == null) {
            return null;
        }
        if (projectNode.getProjectDirectory().equals(projectDirectory)) {
            return projectNode;
        }
        if (projectNode.getChildProjects() != null) {
            for (ProjectInfoNode subprojectNode : projectNode.getChildProjects()) {
                ProjectInfoNode find = findProjectInfo(subprojectNode, projectDirectory);
                if (find != null) {
                    return find;
                }
            }
        }
        return null;
    }

    @Override
    public void saveProject(Project project) throws IOException, ClassCastException {

    }
}
