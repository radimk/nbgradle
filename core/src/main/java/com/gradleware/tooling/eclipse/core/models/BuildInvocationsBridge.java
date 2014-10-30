package com.gradleware.tooling.eclipse.core.models;

import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.Launchable;
import org.gradle.tooling.model.Task;
import org.gradle.tooling.model.TaskSelector;

/**
 * Access to information about project launchables used by task view and similar code.
 *
 * @author radim
 */
public interface BuildInvocationsBridge {

    // TODO should return thread safe values/snapshots
    // void setProject(IProject project);
    Iterable<? extends TaskSelector> getSelectors();
    Iterable<? extends Task> getTasks();
    Iterable<? extends GradleProject> getSubprojects();
    Launchable findLaunchableByName(String name);
    void dispose();
}
