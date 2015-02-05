package org.nbgradle.netbeans.project.actions;

/**
 *
 * @author radim
 */
public interface TaskMappingForAction {

    String getTaskName();
    Iterable<String> getParameters();
    // Iterable<String> getJvmArguments();
}
