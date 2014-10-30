package com.gradleware.tooling.eclipse.core.models;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultGradleToolingRunner implements GradleRunner {
    private static final Logger LOGGER = Logger.getLogger(DefaultGradleToolingRunner.class.getName());

    private final IdeConnector connector;
    private final GradleOperationCustomizer operationCustomizer;

    private volatile ProjectConnection connection;

    public DefaultGradleToolingRunner(IdeConnector connector, GradleOperationCustomizer operationCustomizer) {
        this.connector = connector;
        this.operationCustomizer = operationCustomizer;
    }

    private void initConnection() {
        if (connection == null) {
            connection = connector.getConnection();
        }
    }

    @Override
    public <M> M getModel(Class<M> clz) {
        LOGGER.log(Level.FINE, "Requested model {0} using {1}", new Object[] {clz, connector});
        initConnection();
        ModelBuilder<M> modelBuilder = connector.getConnection().model(clz);
        operationCustomizer.execute(modelBuilder);
        try {
            M model = modelBuilder.get();
            LOGGER.log(Level.FINE, "Retrieved model {0}", model);
            return model;
        } finally {
            try {
                operationCustomizer.close();
            } catch (IOException ioe) {
                LOGGER.log(Level.INFO, "Model loading cleanup failed", ioe);
            }
        }
    }

    @Override
    public BuildLauncher newBuild() {
        initConnection();
        return connection.newBuild();
    }
}
