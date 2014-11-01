package org.nbgradle.netbeans.project.model;

import com.gradleware.tooling.eclipse.core.models.DistributionSpec;
import com.gradleware.tooling.eclipse.core.models.DistributionSpecs;

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
