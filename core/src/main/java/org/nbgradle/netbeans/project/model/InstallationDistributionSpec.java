package org.nbgradle.netbeans.project.model;

import java.io.File;
import org.nbgradle.netbeans.models.DistributionSpec;
import org.nbgradle.netbeans.models.DistributionSpecs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "installationDistribution")
public class InstallationDistributionSpec extends DistributionSettings {
    public InstallationDistributionSpec() {
        super(Type.FILE);
    }

    @Override
    public DistributionSpec toSpec() {
        return DistributionSpecs.installedDistribution(new File(getValue()));
    }
}
