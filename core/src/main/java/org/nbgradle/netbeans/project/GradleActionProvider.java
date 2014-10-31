package org.nbgradle.netbeans.project;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.gradleware.tooling.eclipse.core.models.GradleRunner;
import com.gradleware.tooling.eclipse.core.models.ModelProvider;
import org.gradle.api.Nullable;
import org.gradle.tooling.model.Task;
import org.gradle.tooling.model.gradle.BuildInvocations;
import org.nbgradle.netbeans.project.lookup.GradleProjectInformation;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.LookupProvider;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.util.Lookup;
import org.openide.util.Parameters;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GradleActionProvider implements ActionProvider {
    private static final Logger LOGGER = Logger.getLogger(GradleActionProvider.class.getName());

    @ProjectServiceProvider(
            service=ActionProvider.class,
            projectTypes={@LookupProvider.Registration.ProjectType(id=NbGradleConstants.PROJECT_TYPE, position=1000)})
    public static ActionProvider create(@NonNull final Lookup lkp) {
        Parameters.notNull("lkp", lkp);
        GradleProjectInformation information = lkp.lookup(GradleProjectInformation.class);
        return new GradleActionProvider(
                information,
                lkp.lookup(ModelProvider.class),
                lkp.lookup(GradleRunner.class));
    }

    private final GradleProjectInformation projectInfo;
    private final ModelProvider modelSupplier;
    private final GradleRunner toolingRunner;

    public GradleActionProvider(GradleProjectInformation projectInfo, ModelProvider modelSupplier, GradleRunner toolingRunner) {
        this.projectInfo = Preconditions.checkNotNull(projectInfo);
        this.modelSupplier = Preconditions.checkNotNull(modelSupplier);
        this.toolingRunner = Preconditions.checkNotNull(toolingRunner);
    }

    @Override
    public String[] getSupportedActions() {
        return new String[] {
                COMMAND_BUILD,
                COMMAND_CLEAN
//                COMMAND_REBUILD,
//                COMMAND_RUN,
//                COMMAND_DEBUG,
//        JavaProjectConstants.COMMAND_JAVADOC,
//      COMMAND_TEST,
//                COMMAND_DELETE,
//                COMMAND_COPY,
//                COMMAND_MOVE,
//                COMMAND_RENAME,
        };
    }

    @Override
    public void invokeAction(String command, Lookup context) throws IllegalArgumentException {
        final Iterable<String> taskNames = taskNames(command, context);
        if (taskNames == null) {
            LOGGER.log(Level.FINE, "invokeAction with no task to run {0}", command);
            return;
        }
        new NbGradleBuildRunner(toolingRunner).execute(new GradleLaunchSpec() {
            @Override
            public String getDescription() {
                return "Project build";
            }

            @Override
            public Iterable<String> getTaskNames() {
                return taskNames;
            }
        });
    }

    @Override
    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        LOGGER.log(Level.FINE, "isActionEnabled {0}", command);
        return taskNames(command, context) != null;
    }

    @Nullable
    Iterable<String> taskNames(String command, Lookup context) {
        BuildInvocations tasks = null;
        try {
            tasks = modelSupplier.getModel(BuildInvocations.class).get();
        } catch (InterruptedException e) {
            LOGGER.log(Level.FINE, "cannot load tasks", e);
        } catch (ExecutionException e) {
            LOGGER.log(Level.FINE, "cannot load tasks", e);
        }
        if (tasks == null) {
            return null;
        }
        // TODO utility to find project
        if (COMMAND_BUILD.equals(command) && Iterables.any(tasks.getTasks(), matchesTaskName(projectInfo.getProjectPath(), "build"))) {
            return Collections.singletonList("build");
        }
        return null;
    }

    private static Predicate<Task> matchesTaskName(final String projectPath, final String taskName) {
        return new Predicate<Task>() {
            @Override
            public boolean apply(Task input) {
                return input.getPath().equals((":".equals(projectPath) ? ":" : projectPath + ":") + taskName);
            }
        };
    }
}
