package org.nbgradle.netbeans.models;

import org.gradle.tooling.GradleConnector;

/**
 * Gradle distribution specification.
 * It can be applied to {@link GradleConnector} to make it use specified version.
 */
public abstract class DistributionSpec {
    public abstract void process(GradleConnector connector);
}
