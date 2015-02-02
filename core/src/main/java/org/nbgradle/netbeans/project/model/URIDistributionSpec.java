package org.nbgradle.netbeans.project.model;

import java.net.URI;
import java.net.URISyntaxException;
import org.nbgradle.netbeans.models.DistributionSpec;
import org.nbgradle.netbeans.models.DistributionSpecs;

import javax.xml.bind.annotation.XmlRootElement;
import org.openide.util.Exceptions;

@XmlRootElement(name = "uriDistribution")
public class URIDistributionSpec extends DistributionSettings {
    public URIDistributionSpec() {
        super(Type.URI);
    }

    @Override
    public DistributionSpec toSpec() {
        try {
            return DistributionSpecs.uriDistribution(new URI(getValue()));
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
            return DistributionSpecs.defaultDistribution();
        }
    }
}
