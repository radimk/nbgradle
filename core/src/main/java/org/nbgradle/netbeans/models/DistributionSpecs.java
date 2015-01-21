package org.nbgradle.netbeans.models;

import java.io.File;
import java.net.URI;

import org.gradle.tooling.GradleConnector;

public class DistributionSpecs {

    private DistributionSpecs() {
    }

    public static DistributionSpec defaultDistribution() {
        return new DistributionSpec() {
            @Override
            public void process(GradleConnector connector) {
                // no-op
            }
        };
    }

    public static DistributionSpec installedDistribution(final File installDir) {
        return new DistributionSpec() {
            @Override
            public void process(GradleConnector connector) {
                connector.useInstallation(installDir);
            }
        };
    }

    public static DistributionSpec uriDistribution(final URI distroUri) {
        return new DistributionSpec() {
            @Override
            public void process(GradleConnector connector) {
                connector.useDistribution(distroUri);
            }
        };
    }

    public static DistributionSpec versionDistribution(final String version) {
        return new DistributionSpec() {
            @Override
            public void process(GradleConnector connector) {
                connector.useGradleVersion(version);
            }
        };
    }
}
