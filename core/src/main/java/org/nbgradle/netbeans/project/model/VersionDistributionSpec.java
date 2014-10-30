package org.nbgradle.netbeans.project.model;

import com.gradleware.tooling.eclipse.core.models.DistributionSpec;
import com.gradleware.tooling.eclipse.core.models.DistributionSpecs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "versionDistribution")
public class VersionDistributionSpec extends DistributionSettings {
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public DistributionSpec toSpec() {
        return DistributionSpecs.versionDistribution(version);
    }
}
