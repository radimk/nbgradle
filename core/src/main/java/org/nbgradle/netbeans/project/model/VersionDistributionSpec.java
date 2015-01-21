package org.nbgradle.netbeans.project.model;

import org.nbgradle.netbeans.models.DistributionSpec;
import org.nbgradle.netbeans.models.DistributionSpecs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "versionDistribution")
public class VersionDistributionSpec extends DistributionSettings {
    public VersionDistributionSpec() {
        super(Type.VERSION);
    }

    @Override
    public DistributionSpec toSpec() {
        return DistributionSpecs.versionDistribution(getValue());
    }
}
