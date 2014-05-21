package org.nbgradle.netbeans.project.model;

import org.gradle.tooling.GradleConnector;

public abstract class DistributionSpec {
    public abstract void process(GradleConnector connector);
}
