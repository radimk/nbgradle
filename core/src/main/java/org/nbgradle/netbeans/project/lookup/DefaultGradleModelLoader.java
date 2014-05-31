package org.nbgradle.netbeans.project.lookup;

import com.google.common.base.Preconditions;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.gradle.GradleBuild;
import org.nbgradle.netbeans.project.model.GradleBuildSettings;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultGradleModelLoader implements GradleModelLoader {
    private static final Logger LOGGER = Logger.getLogger(DefaultGradleModelLoader.class.getName());

    private final GradleBuildSettings buildSettings;
    private final File projectDir;

    public DefaultGradleModelLoader(GradleBuildSettings buildSettings, File projectDir) {
        this.buildSettings = Preconditions.checkNotNull(buildSettings);
        this.projectDir = Preconditions.checkNotNull(projectDir);
    }

    @Override
    public <M> M getModel(Class<M> clz) {
        LOGGER.log(Level.FINE, "Requested model {0} for project in {1}", new Object[] {clz, projectDir});
        GradleConnector connector = GradleConnector.newConnector()
                .forProjectDirectory(projectDir);
        buildSettings.getDistributionSpec().process(connector);
        if (buildSettings.getGradleUserHomeDir() != null) {
            connector.useGradleUserHomeDir(buildSettings.getGradleUserHomeDir());
        }
        ProjectConnection connection = connector.connect();
        M model = connection.getModel(clz);
        LOGGER.log(Level.FINE, "Retrieved model {0}", model);
        return model;
    }
}
