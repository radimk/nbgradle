package org.nbgradle.netbeans.project.model;

import java.io.File;

public class DefaultGradleBuildSettings implements GradleBuildSettings {
    private DistributionSpec distributionSpec;
    private File gradleUserHomeDir;

    public DefaultGradleBuildSettings() {
        distributionSpec = new DefaultDistributionSpec();
    }

    public void setDistributionSpec(DistributionSpec distributionSpec) {
        this.distributionSpec = distributionSpec;
    }

    @Override
    public DistributionSpec getDistributionSpec() {
        return distributionSpec;
    }

    public void setGradleUserHomeDir(File gradleUserHomeDir) {
        this.gradleUserHomeDir = gradleUserHomeDir;
    }

    @Override
    public File getGradleUserHomeDir() {
        return gradleUserHomeDir;
    }
}
