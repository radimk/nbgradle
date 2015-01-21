package org.nbgradle.netbeans.project.model;

import org.nbgradle.netbeans.models.DistributionSpec;
import org.nbgradle.netbeans.models.DistributionSpecs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "defaultDistribution")
public class DefaultDistributionSpec extends DistributionSettings {
    public DefaultDistributionSpec() {
        super(Type.DEFAULT);
    }

    @Override
    public DistributionSpec toSpec() {
        return DistributionSpecs.defaultDistribution();
    }
}
