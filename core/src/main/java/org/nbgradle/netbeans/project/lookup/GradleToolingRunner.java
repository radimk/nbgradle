package org.nbgradle.netbeans.project.lookup;

import org.gradle.tooling.BuildLauncher;

public interface GradleToolingRunner {
    <M> M getModel(Class<M> clz);
    BuildLauncher newBuild();
}
