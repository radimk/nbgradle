package org.nbgradle.netbeans.models;

import java.io.File;

import org.gradle.api.Nullable;

/**
 * Settings related to Gradle project connection.
 */
public interface GradleBuildSettings {

    /**
     * Specifies what Gradle distribution should be used when building the project.
     */
    DistributionSpec getDistributionSpec();
    /**
     * Returns Gradle user home directory if specified.
     */
    @Nullable
    File getGradleUserHomeDir();

    @Nullable
    Integer getMaxDaemonIdleTimeoutMillis();

    @Nullable
    String getJvmOptions();
}
