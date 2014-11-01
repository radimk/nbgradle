package org.nbgradle.netbeans.project;

import org.gradle.api.Nullable;
import org.gradle.tooling.CancellationToken;

// TODO candidate to be moved to models
public interface GradleLaunchSpec {
    BuildProgressMonitor createProgressMonitor();
    StandardStreams getStandardStreams();
    Iterable<String> getTaskNames();
    @Nullable CancellationToken getCancellationToken();
}
