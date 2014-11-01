package org.nbgradle.netbeans.project;

// TODO candidate to be moved to models
public interface GradleLaunchSpec {
    BuildProgressMonitor createProgressMonitor();
    StandardStreams getStandardStreams();
    Iterable<String> getTaskNames();
}
