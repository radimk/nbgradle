package org.nbgradle.netbeans.project.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "gradleBuild", namespace = "org.nbgradle.netbeans.project")
public class NbGradleBuildJAXB {
    private NbGradleProjectJAXB rootProject;
    private DistributionSettings distribution;

    public DistributionSettings getDistribution() {
        return distribution;
    }

    public void setDistribution(DistributionSettings distribution) {
        this.distribution = distribution;
    }

    public NbGradleProjectJAXB getRootProject() {
        return rootProject;
    }

    public void setRootProject(NbGradleProjectJAXB rootProject) {
        this.rootProject = rootProject;
    }
}
