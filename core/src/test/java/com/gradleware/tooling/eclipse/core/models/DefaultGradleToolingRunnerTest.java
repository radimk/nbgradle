package com.gradleware.tooling.eclipse.core.models;

import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.eclipse.EclipseProject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultGradleToolingRunnerTest {

    IdeConnector connector;
    GradleOperationCustomizer operationCustomizer;
    DefaultGradleToolingRunner runner;
    
    @Before
    public void setup() {
        connector = mock(GradleIdeConnector.class);
        operationCustomizer = mock(GradleOperationCustomizer.class);
        runner = new DefaultGradleToolingRunner(connector, operationCustomizer);
    }

    @Test
    public void returnModelFromProvider() throws Exception {
        ProjectConnection conn = mock(ProjectConnection.class);
        ModelBuilder<EclipseProject> modelBuilder = mock(ModelBuilder.class);
        EclipseProject model = mock(EclipseProject.class);
        when(connector.getConnection()).thenReturn(conn);
        when(conn.model(EclipseProject.class)).thenReturn(modelBuilder);
        when(modelBuilder.get()).thenReturn(model);

        EclipseProject futureProject = runner.getModel(EclipseProject.class);
        assertNotNull(futureProject);
        verify(operationCustomizer).execute(modelBuilder);
        verify(modelBuilder).get();
        verify(operationCustomizer).close();
    }

    @Test
    public void propagatesExceptionModelFromProvider() throws Exception {
        ProjectConnection conn = mock(ProjectConnection.class);
        ModelBuilder<EclipseProject> builder = mock(ModelBuilder.class); 
        RuntimeException ex = new RuntimeException("testing");
        when(connector.getConnection()).thenReturn(conn);
        when(conn.model(EclipseProject.class)).thenReturn(builder);
        when(builder.get()).thenThrow(ex);
        doThrow(new IOException("Pretend the stream is closed.")).when(operationCustomizer).close();

        try {
            runner.getModel(EclipseProject.class);
        } catch (Exception e) {
            assertSame(ex, e);
        }
        verify(operationCustomizer).execute(builder);
        verify(builder).get();
        verify(operationCustomizer).close();
    }
}
