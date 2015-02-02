package org.nbgradle.netbeans.project.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "gradleBuild", namespace = "org.nbgradle.netbeans.project")
public class NbGradleBuildJAXB {
    private NbGradleProjectJAXB rootProject;
    private DistributionSettings distribution;
    private String jvmOptions;
    private String gradleUserDir;

    public DistributionSettings getDistribution() {
        return distribution;
    }

    public void setDistribution(DistributionSettings distribution) {
        this.distribution = distribution;
    }

    public String getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    public String getGradleUserDir() {
        return gradleUserDir;
    }

    public void setGradleUserDir(String gradleUserDir) {
        this.gradleUserDir = gradleUserDir;
    }

    public NbGradleProjectJAXB getRootProject() {
        return rootProject;
    }

    public void setRootProject(NbGradleProjectJAXB rootProject) {
        this.rootProject = rootProject;
    }
}
