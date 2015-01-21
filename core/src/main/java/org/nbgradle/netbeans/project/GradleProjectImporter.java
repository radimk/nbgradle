package org.nbgradle.netbeans.project;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import org.nbgradle.netbeans.models.GradleBuildSettings;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.gradle.BasicGradleProject;
import org.gradle.tooling.model.gradle.GradleBuild;
import org.nbgradle.netbeans.project.lookup.ProjectTreeInformation;
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

public class GradleProjectImporter {

    public static class ImportedData {
        public final ProjectTreeInformation projectTree;
        public final GradleBuildSettings buildSettings;

        public ImportedData(ProjectTreeInformation projectTree, GradleBuildSettings buildSettings) {
            this.projectTree = projectTree;
            this.buildSettings = buildSettings;
        }
    }

    public void importProject(NbGradleBuildSettings gradleBuildSettings, File projectDir) {
        GradleConnector connector = GradleConnector.newConnector()
                .forProjectDirectory(projectDir);
        gradleBuildSettings.getDistributionSpec().process(connector);
        if (gradleBuildSettings.getGradleUserHomeDir() != null) {
            connector.useGradleUserHomeDir(gradleBuildSettings.getGradleUserHomeDir());
        }
        ProjectConnection connection = connector.connect();
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
                gradleBuildSettings.getDistributionSettings(),
                Files.asByteSink(new File(projectDir, NbGradleConstants.NBGRADLE_BUILD_XML)));
    }

    void writeProjectSettings(BasicGradleProject project, DistributionSettings distributionSettings, ByteSink byteSink) {
        NbGradleBuildJAXB buildJaxb = new NbGradleBuildJAXB();
        buildJaxb.setRootProject(createProjectJAXB(project));
        buildJaxb.setDistribution(distributionSettings);
        try (OutputStream outputStream = byteSink.openStream()) {
            JAXBContext context = JAXBContext.newInstance(NbGradleBuildJAXB.class, DefaultDistributionSpec.class, VersionDistributionSpec.class);
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
            JAXBContext context = JAXBContext.newInstance(NbGradleBuildJAXB.class, DefaultDistributionSpec.class, VersionDistributionSpec.class);
            Unmarshaller um = context.createUnmarshaller();
            NbGradleBuildJAXB build = (NbGradleBuildJAXB) um.unmarshal(is);
            DefaultGradleBuildSettings settings = new DefaultGradleBuildSettings();
            settings.setDistributionSettings(build.getDistribution());

            return new ImportedData(build.getRootProject(), settings);
        } catch (JAXBException | IOException e) {
            throw new ProjectImportException("Cannot read project metadata.", e);
        }
    }
}
