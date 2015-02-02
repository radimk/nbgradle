package org.nbgradle.netbeans.models;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.internal.consumer.DefaultGradleConnector;

public class GradleIdeConnector implements IdeConnector {
    private final GradleBuildSettings buildSettings;
    private final File projectDir;

    public GradleIdeConnector(GradleBuildSettings buildSettings, File projectDir) {
        this.buildSettings = buildSettings;
        this.projectDir = projectDir;
    }

    @Override
    public ProjectConnection getConnection() {
        return createConnection();
    }

    private ProjectConnection createConnection() {
        GradleConnector connector = GradleConnector.newConnector().forProjectDirectory(projectDir);
        buildSettings.getDistributionSpec().process(connector);
        if (buildSettings.getGradleUserHomeDir() != null) {
            connector.useGradleUserHomeDir(buildSettings.getGradleUserHomeDir());
        }
        if (buildSettings.getMaxDaemonIdleTimeoutMillis() != null &&
                connector instanceof DefaultGradleConnector) {
            ((DefaultGradleConnector) connector).daemonMaxIdleTime(buildSettings.getMaxDaemonIdleTimeoutMillis(), TimeUnit.MILLISECONDS);
        }
        ProjectConnection connection = connector.connect();
        return connection;
    }
}
