package org.nbgradle.netbeans.project.model;

import org.nbgradle.netbeans.project.NbGradleProject;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;

@XmlRootElement(name = "gradleBuild", namespace = "org.nbgradle.netbeans.project")
public class NbGradleBuildJAXB {
    private NbGradleProjectJAXB rootProject;
    private DistributionSpec distribution;

    public DistributionSpec getDistribution() {
        return distribution;
    }

    public void setDistribution(DistributionSpec distribution) {
        this.distribution = distribution;
    }

    public NbGradleProjectJAXB getRootProject() {
        return rootProject;
    }

    public void setRootProject(NbGradleProjectJAXB rootProject) {
        this.rootProject = rootProject;
    }
}
