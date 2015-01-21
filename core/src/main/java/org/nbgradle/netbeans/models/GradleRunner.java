package org.nbgradle.netbeans.models;

import org.gradle.tooling.BuildLauncher;

/**
 * A facade over ProjectConnection to add IDE handling to common actions.
 */
public interface GradleRunner {
    <M> M getModel(Class<M> clz);
    BuildLauncher newBuild();
}
