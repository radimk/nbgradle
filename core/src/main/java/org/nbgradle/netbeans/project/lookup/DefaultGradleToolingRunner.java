package org.nbgradle.netbeans.project.lookup;

import com.google.common.base.Preconditions;
import com.gradleware.tooling.eclipse.core.models.GradleBuildSettings;
import com.gradleware.tooling.eclipse.core.models.GradleIdeConnector;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultGradleToolingRunner implements GradleToolingRunner {
    private static final Logger LOGGER = Logger.getLogger(DefaultGradleToolingRunner.class.getName());

    private final GradleBuildSettings buildSettings;
    private final File projectDir;
    private final GradleIdeConnector connector;

    private volatile ProjectConnection connection;

    public DefaultGradleToolingRunner(GradleBuildSettings buildSettings, File projectDir) {
        this.buildSettings = Preconditions.checkNotNull(buildSettings);
        this.projectDir = Preconditions.checkNotNull(projectDir);
        connector = new GradleIdeConnector(buildSettings, projectDir);
    }

    private void initConnection() {
        if (connection == null) {
            connection = connector.getConnection();
        }
    }

    @Override
    public <M> M getModel(Class<M> clz) {
        LOGGER.log(Level.FINE, "Requested model {0} for project in {1}", new Object[] {clz, projectDir});
        initConnection();
        M model = connection.getModel(clz);
        LOGGER.log(Level.FINE, "Retrieved model {0}", model);
        return model;
    }

    @Override
    public BuildLauncher newBuild() {
        initConnection();
        return connection.newBuild();
    }
}
