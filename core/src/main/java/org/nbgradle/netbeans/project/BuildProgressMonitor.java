package org.nbgradle.netbeans.project;

import org.gradle.tooling.ProgressEvent;

public interface BuildProgressMonitor {

    void start();
    void statusChanged(ProgressEvent event);
    void finish();
}
