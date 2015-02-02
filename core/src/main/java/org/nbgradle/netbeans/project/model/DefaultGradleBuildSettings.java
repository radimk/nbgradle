package org.nbgradle.netbeans.project.model;

import org.nbgradle.netbeans.models.DistributionSpec;
import org.nbgradle.netbeans.models.GradleBuildSettings;

import java.io.File;

public class DefaultGradleBuildSettings implements NbGradleBuildSettings {
    private DistributionSettings distributionSettings;
    private File gradleUserHomeDir;
    private String jvmOptions;

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
    public String getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    @Override
    public Integer getMaxDaemonIdleTimeoutMillis() {
        return 5 * 60 * 1000; // 5 minutes
    }
}
