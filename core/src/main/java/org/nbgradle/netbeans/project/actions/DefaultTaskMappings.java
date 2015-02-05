package org.nbgradle.netbeans.project.actions;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.spi.project.ActionProvider;

/**
 *
 * @author radim
 */
public class DefaultTaskMappings {

    public static Map<String, TaskMappingForAction> forProject(String projectPath) {
        Map<String, TaskMappingForAction> actions = new HashMap<>();
        actions.put(ActionProvider.COMMAND_BUILD, new DefaultTaskMappingForAction(taskName(projectPath, "assemble")));
        actions.put(ActionProvider.COMMAND_CLEAN, new DefaultTaskMappingForAction(taskName(projectPath, "clean")));
        actions.put(ActionProvider.COMMAND_TEST, new DefaultTaskMappingForAction(taskName(projectPath, "test")));
        return actions;
    }

    private static String taskName(String projectPath, String taskName) {
        return ":".equals(projectPath) ? ":" + taskName : projectPath + ":" + taskName;
    }
}
