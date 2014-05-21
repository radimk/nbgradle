package org.nbgradle.netbeans.project;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.gradle.BasicGradleProject;
import org.gradle.tooling.model.gradle.GradleBuild;
import org.nbgradle.netbeans.project.model.DefaultDistributionSpec;
import org.nbgradle.netbeans.project.model.DistributionSpec;
import org.nbgradle.netbeans.project.model.GradleBuildSettings;
import org.nbgradle.netbeans.project.model.NbGradleBuildJAXB;
import org.nbgradle.netbeans.project.model.NbGradleProjectJAXB;
import org.nbgradle.netbeans.project.model.VersionDistributionSpec;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;

public class GradleProjectImporter {

    public void importProject(GradleBuildSettings gradleBuildSettings, File projectDir) {
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
        NbGradleBuildJAXB buildJaxb = new NbGradleBuildJAXB();
        buildJaxb.setRootProject(createProjectJAXB(gradleBuild.getRootProject()));
        buildJaxb.setDistribution(gradleBuildSettings.getDistributionSpec());
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(NbGradleBuildJAXB.class, DefaultDistributionSpec.class, VersionDistributionSpec.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // Write to File
            m.marshal(buildJaxb, new File(projectDir, NbGradleConstants.NBGRADLE_BUILD_XML));
        } catch (JAXBException e) {
            throw new ProjectImportException("Cannot store project metadata.", e);
        }
    }

    private NbGradleProjectJAXB createProjectJAXB(BasicGradleProject project) {
        NbGradleProjectJAXB projectJaxb = new NbGradleProjectJAXB();
        projectJaxb.setPath(project.getPath());
        projectJaxb.setName(project.getName());
        projectJaxb.setProjectDirectory(project.getProjectDirectory());
        // todo children
        return projectJaxb;
    }
}
