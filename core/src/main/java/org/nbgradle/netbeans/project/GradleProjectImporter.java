package org.nbgradle.netbeans.project;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import org.nbgradle.netbeans.models.GradleBuildSettings;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.gradle.BasicGradleProject;
import org.gradle.tooling.model.gradle.GradleBuild;
import org.nbgradle.netbeans.project.lookup.ProjectInfoNode;
import org.nbgradle.netbeans.project.model.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nbgradle.netbeans.models.GradleIdeConnector;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

public class GradleProjectImporter {
    private static final Logger LOG = Logger.getLogger(GradleProjectImporter.class.getName());

    public static class ImportedData {
        public final ProjectInfoNode projectTree;
        public final GradleBuildSettings buildSettings;

        public ImportedData(ProjectInfoNode projectTree, GradleBuildSettings buildSettings) {
            this.projectTree = projectTree;
            this.buildSettings = buildSettings;
        }
    }

    public void importProject(NbGradleBuildSettings gradleBuildSettings, File projectDir) {
        GradleIdeConnector ideConnector = new GradleIdeConnector(gradleBuildSettings, projectDir);
        ProjectConnection connection = ideConnector.getConnection();
        GradleBuild gradleBuild = connection.getModel(GradleBuild.class);
        try {
            if (!gradleBuild.getRootProject().getProjectDirectory().equals(projectDir.getCanonicalFile())) {
                throw new ProjectImportException("Directory " + projectDir + " does not contain Gradle root project.");
            }
        } catch (IOException e) {
            throw new ProjectImportException("Problem when importing directory " + projectDir + ".", e);
        }
        writeProjectSettings(
                gradleBuild.getRootProject(),
                gradleBuildSettings,
                Files.asByteSink(new File(projectDir, NbGradleConstants.NBGRADLE_BUILD_XML)));
        markProjectDirectories(
                gradleBuild.getRootProject(), gradleBuild.getRootProject().getProjectDirectory().getAbsolutePath());
    }

    private void markProjectDirectories(BasicGradleProject gProject, String rootProjectPath) {
        File projectDir = gProject.getProjectDirectory();
        if (!new File(projectDir, NbGradleConstants.BUILD_GRADLE_FILENAME).exists() &
                !new File(projectDir, NbGradleConstants.SETTINGS_GRADLE_FILENAME).exists() &&
                !new File(projectDir, NbGradleConstants.NBGRADLE_BUILD_XML).exists()) {
            FileObject projectDirFo = FileUtil.toFileObject(projectDir);
            try {
                projectDirFo.setAttribute(NbGradleConstants.NBGRADLE_PROJECT_DIR_ATTR, rootProjectPath);
            } catch (IOException ex) {
                LOG.log(Level.INFO, "Cannot mark " + projectDir.getAbsolutePath() + " as project directory", ex);
            }
        }
        for (BasicGradleProject subProject : gProject.getChildren()) {
            markProjectDirectories(subProject, rootProjectPath);
        }
    }

    void writeProjectSettings(
            BasicGradleProject project, NbGradleBuildSettings buildSettings, ByteSink byteSink) {
        NbGradleBuildJAXB buildJaxb = new NbGradleBuildJAXB();
        buildJaxb.setRootProject(createProjectJAXB(project));
        buildJaxb.setDistribution(buildSettings.getDistributionSettings());
        buildJaxb.setJvmOptions(buildSettings.getJvmOptions());
        buildJaxb.setGradleUserDir(buildSettings.getGradleUserHomeDir() != null ?
                buildSettings.getGradleUserHomeDir().getAbsolutePath(): null);
        try (OutputStream outputStream = byteSink.openStream()) {
            JAXBContext context = JAXBContext.newInstance(
                    NbGradleBuildJAXB.class, DefaultDistributionSpec.class, VersionDistributionSpec.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(buildJaxb, outputStream);
        } catch (JAXBException | IOException e) {
            throw new ProjectImportException("Cannot store project metadata.", e);
        }
    }

    private NbGradleProjectJAXB createProjectJAXB(BasicGradleProject project) {
        NbGradleProjectJAXB projectJaxb = new NbGradleProjectJAXB();
        projectJaxb.setPath(project.getPath());
        projectJaxb.setName(project.getName());
        projectJaxb.setProjectDirectory(project.getProjectDirectory());
        List<NbGradleProjectJAXB> children = Lists.newArrayList();
        for (BasicGradleProject child : project.getChildren().getAll()) {
            children.add(createProjectJAXB(child));
        }
        projectJaxb.setChildProjects(children);
        return projectJaxb;
    }

    public ImportedData readBuildSettings(ByteSource byteSource) {
        try (InputStream is = byteSource.openStream()) {
            JAXBContext context = JAXBContext.newInstance(
                    NbGradleBuildJAXB.class, DefaultDistributionSpec.class, VersionDistributionSpec.class);
            Unmarshaller um = context.createUnmarshaller();
            NbGradleBuildJAXB build = (NbGradleBuildJAXB) um.unmarshal(is);
            DefaultGradleBuildSettings settings = new DefaultGradleBuildSettings();
            if (build.getDistribution() != null) {
                settings.setDistributionSettings(build.getDistribution());
            }
            settings.setJvmOptions(build.getJvmOptions());
            String userHomeDir = build.getGradleUserDir();
            if (userHomeDir != null) {
                settings.setGradleUserHomeDir(new File(userHomeDir));
            }

            return new ImportedData(build.getRootProject(), settings);
        } catch (JAXBException | IOException e) {
            throw new ProjectImportException("Cannot read project metadata.", e);
        }
    }
}
