/*
 */
package org.nbgradle.netbeans.project.actions;

import java.util.Collections;

/**
 *
 * @author radim
 */
public class DefaultTaskMappingForAction implements TaskMappingForAction {

    private final String taskName;

    public DefaultTaskMappingForAction(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }

    @Override
    public Iterable<String> getParameters() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "DefaultTaskMappingForAction{" +
                "taskName='" + taskName + '\'' +
                '}';
    }
}
