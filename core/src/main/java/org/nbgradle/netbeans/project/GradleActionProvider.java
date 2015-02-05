package org.nbgradle.netbeans.project;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.io.Closeables;
import org.nbgradle.netbeans.models.GradleRunner;
import org.nbgradle.netbeans.models.ModelProvider;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;
import org.gradle.api.Nullable;
import org.gradle.tooling.CancellationToken;
import org.gradle.tooling.CancellationTokenSource;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProgressEvent;
import org.gradle.tooling.model.Task;
import org.gradle.tooling.model.gradle.BuildInvocations;
import org.nbgradle.netbeans.project.lookup.GradleProjectInformation;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.LookupProvider;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.util.Cancellable;
import org.openide.util.Lookup;
import org.openide.util.Parameters;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nbgradle.netbeans.project.actions.DefaultTaskMappings;
import org.nbgradle.netbeans.project.actions.TaskMappingForAction;

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
    private final Map<String, TaskMappingForAction> actionMappings;

    public GradleActionProvider(GradleProjectInformation projectInfo, ModelProvider modelSupplier, GradleRunner toolingRunner) {
        this.projectInfo = Preconditions.checkNotNull(projectInfo);
        this.modelSupplier = Preconditions.checkNotNull(modelSupplier);
        this.toolingRunner = Preconditions.checkNotNull(toolingRunner);
        // TODO needs to be customizable
        actionMappings = DefaultTaskMappings.forProject(projectInfo.getProjectPath());
    }

    @Override
    public String[] getSupportedActions() {
        return new String[] {
                COMMAND_BUILD,
                COMMAND_CLEAN,
                COMMAND_TEST
//                COMMAND_REBUILD,
//                COMMAND_RUN,
//                COMMAND_DEBUG,
//        JavaProjectConstants.COMMAND_JAVADOC,
//                COMMAND_DELETE,
//                COMMAND_COPY,
//                COMMAND_MOVE,
//                COMMAND_RENAME,
        };
    }

    @Override
    public void invokeAction(String command, Lookup context) throws IllegalArgumentException {
        TaskMappingForAction taskMapping = taskMapping(command, context);
        if (taskMapping == null) {
            LOGGER.log(Level.FINE, "invokeAction with no task to run {0}", command);
            return;
        }
        new NbGradleBuildRunner(toolingRunner).execute(new NbGradleLaunchSpec(taskMapping));
    }

    @Override
    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        return taskMapping(command, context) != null;
    }

    @Nullable
    TaskMappingForAction taskMapping(String command, Lookup context) {
        BuildInvocations tasks = null;
        try {
            tasks = modelSupplier.getModel(BuildInvocations.class).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.log(Level.FINE, "cannot load tasks", e);
        }
        if (tasks == null) {
            return null;
        }
        // TODO utility to find project
        // TODO vm args and options
        TaskMappingForAction action = actionMappings.get(command);
        if (action != null && Iterables.any(tasks.getTasks(), matchesTaskName(action.getTaskName()))) {
            return action;
        }
        return null;
    }

    private static Predicate<Task> matchesTaskName(final String taskName) {
        return new Predicate<Task>() {
            @Override
            public boolean apply(Task input) {
                return input.getPath().equals(taskName);
            }
        };
    }

    private class NbGradleLaunchSpec implements GradleLaunchSpec {
        private final TaskMappingForAction taskMapping;
        private final String title;
        private final InputOutput io;
        private final IOToStreamsAdapter streams;
        private final NbBuildProgressMonitor progress;

        public NbGradleLaunchSpec(TaskMappingForAction taskMapping) {
            this.taskMapping = Preconditions.checkNotNull(taskMapping);
            IOProvider ioProvider = IOProvider.getDefault();
            title = projectInfo.getName() + " (" + Joiner.on(',').join(getTaskNames()) + ")";
            progress = new NbBuildProgressMonitor(title);
            io = ioProvider.getIO(title, false);
            streams = new IOToStreamsAdapter(io);
        }

        @Override
        public BuildProgressMonitor createProgressMonitor() {
            return progress;
        }

        @Override
        public final Iterable<String> getTaskNames() {
            return Collections.singleton(taskMapping.getTaskName());
        }

        @Override
        public CancellationToken getCancellationToken() {
            return progress.cancelSource.token();
        }

        @Override
        public StandardStreams getStandardStreams() {
            return streams;
        }
    }

    private static class IOToStreamsAdapter implements StandardStreams {
        // private final InputOutput io;
        private final InputStream in;
        private final OutputStream out;
        private final OutputStream err;

        private IOToStreamsAdapter(InputOutput io) {
            // this.io = io;
            in = new ReaderInputStream(io.getIn());
            out = new WriterOutputStream(io.getOut());
            err = new WriterOutputStream(io.getErr());
        }

        @Override
        public InputStream getInputStream() {
            return in;
        }

        @Override
        public OutputStream getOutputStream() {
            return out;
        }

        @Override
        public OutputStream getErrorStream() {
            return err;
        }

        @Override
        public void close() throws IOException {
            Closeables.closeQuietly(in);
            out.close();
            err.close();
        }
    }

    private class NbBuildProgressMonitor implements BuildProgressMonitor {
        private final CancellationTokenSource cancelSource;
        private final ProgressHandle progress;

        private NbBuildProgressMonitor(String title) {
            cancelSource = GradleConnector.newCancellationTokenSource();
            progress = ProgressHandleFactory.createHandle(title,
                    new Cancellable() {
                        @Override
                        public boolean cancel() {
                            cancelSource.cancel();
                            return true;
                        }
                    });
        }

        @Override
        public void start() {
            progress.start();
        }

        @Override
        public void statusChanged(ProgressEvent event) {
            progress.progress(event.getDescription());
        }

        @Override
        public void finish() {
            progress.finish();
        }
    }
}
