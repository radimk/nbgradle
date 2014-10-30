package org.nbgradle.netbeans.project.model;

import com.gradleware.tooling.eclipse.core.models.DistributionSpec;
import com.gradleware.tooling.eclipse.core.models.DistributionSpecs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "defaultDistribution")
public class DefaultDistributionSpec extends DistributionSettings {
    @Override
    public DistributionSpec toSpec() {
        return DistributionSpecs.defaultDistribution();
    }
}
