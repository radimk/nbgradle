package org.nbgradle.netbeans.project.model;

import com.gradleware.tooling.eclipse.core.models.DistributionSpec;
import com.gradleware.tooling.eclipse.core.models.GradleBuildSettings;

import java.io.File;

public class DefaultGradleBuildSettings implements NbGradleBuildSettings {
    private DistributionSettings distributionSettings;
    private File gradleUserHomeDir;

    public DefaultGradleBuildSettings() {
        distributionSettings = new DefaultDistributionSpec();
    }

    public void setDistributionSettings(DistributionSettings distributionSpec) {
        this.distributionSettings = distributionSpec;
    }

    @Override
    public DistributionSettings getDistributionSettings() {
        return distributionSettings;
    }

    @Override
    public DistributionSpec getDistributionSpec() {
        return distributionSettings.toSpec();
    }

    public void setGradleUserHomeDir(File gradleUserHomeDir) {
        this.gradleUserHomeDir = gradleUserHomeDir;
    }

    @Override
    public File getGradleUserHomeDir() {
        return gradleUserHomeDir;
    }

    @Override
    public Integer getMaxDaemonIdleTimeoutMillis() {
        return 5 * 60 * 1000; // 5 minutes
    }
}
