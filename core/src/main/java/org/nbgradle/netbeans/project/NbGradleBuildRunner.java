package org.nbgradle.netbeans.project;

import com.google.common.collect.Iterables;
import com.gradleware.tooling.eclipse.core.models.GradleRunner;
import org.gradle.api.Action;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.ResultHandler;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

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
    public void execute(GradleLaunchSpec gradleLaunchSpec) {
        LOGGER.log(Level.INFO, "Starting Gradle build: {0}", gradleLaunchSpec.getTaskNames());
        final ProgressHandle progress = ProgressHandleFactory.createHandle(gradleLaunchSpec.getDescription());
        BuildLauncher buildLauncher = toolingRunner.newBuild().forTasks(Iterables.toArray(gradleLaunchSpec.getTaskNames(), String.class));
        progress.start();

        buildLauncher.run(new ResultHandler<Void>() {
            @Override
            public void onComplete(Void result) {
                LOGGER.log(Level.INFO, "Gradle build completed.");
                progress.finish();
            }

            @Override
            public void onFailure(GradleConnectionException failure) {
                LOGGER.log(Level.INFO, "Gradle build failed.");
                progress.finish();
            }
        });
    }
}
