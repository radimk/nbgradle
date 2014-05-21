package org.nbgradle.netbeans.project.model;

import org.gradle.tooling.GradleConnector;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "defaultDistribution")
public class DefaultDistributionSpec extends DistributionSpec {
    @Override
    public void process(GradleConnector connector) {
        // no-op
    }
}
