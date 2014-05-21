package org.nbgradle.netbeans.project.model;

import org.gradle.tooling.GradleConnector;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "versionDistribution")
public class VersionDistributionSpec extends DistributionSpec {
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public void process(GradleConnector connector) {
        connector.useGradleVersion(version);
    }
}
