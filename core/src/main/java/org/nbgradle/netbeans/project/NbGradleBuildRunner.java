package org.nbgradle.netbeans.project;

import com.google.common.collect.Iterables;
import org.nbgradle.netbeans.models.GradleRunner;
import org.gradle.api.Action;
import org.gradle.tooling.*;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runs a Gradle build from the IDE.
 */
public class NbGradleBuildRunner implements Action<GradleLaunchSpec> {
    private static final Logger LOGGER = Logger.getLogger(NbGradleBuildRunner.class.getName());

    private final GradleRunner toolingRunner;

    public NbGradleBuildRunner(GradleRunner toolingRunner) {
        this.toolingRunner = toolingRunner;
    }

    @Override
    public void execute(final GradleLaunchSpec gradleLaunchSpec) {
        LOGGER.log(Level.INFO, "Starting Gradle build: {0}", gradleLaunchSpec.getTaskNames());
        final BuildProgressMonitor progress = gradleLaunchSpec.createProgressMonitor();
        BuildLauncher buildLauncher = toolingRunner.newBuild().forTasks(Iterables.toArray(gradleLaunchSpec.getTaskNames(), String.class));
        buildLauncher.addProgressListener(new ProgressListener() {
            @Override
            public void statusChanged(ProgressEvent event) {
                progress.statusChanged(event);
            }
        });
        buildLauncher.setStandardInput(gradleLaunchSpec.getStandardStreams().getInputStream());
        buildLauncher.setStandardOutput(gradleLaunchSpec.getStandardStreams().getOutputStream());
        buildLauncher.setStandardError(gradleLaunchSpec.getStandardStreams().getErrorStream());
        CancellationToken cancel = gradleLaunchSpec.getCancellationToken();
        if (cancel != null) {
            buildLauncher.withCancellationToken(cancel);
        }
        progress.start();

        buildLauncher.run(new ResultHandler<Void>() {
            @Override
            public void onComplete(Void result) {
                LOGGER.log(Level.INFO, "Gradle build completed.");
                progress.finish();
                try {
                    gradleLaunchSpec.getStandardStreams().close();
                } catch (IOException e) {
                    LOGGER.log(Level.INFO, null, e);
                }
            }

            @Override
            public void onFailure(GradleConnectionException failure) {
                LOGGER.log(Level.INFO, "Gradle build failed.");
                progress.finish();
                try {
                    gradleLaunchSpec.getStandardStreams().close();
                } catch (IOException e) {
                    LOGGER.log(Level.INFO, null, e);
                }
            }
        });
    }
}
