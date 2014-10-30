package com.gradleware.tooling.eclipse.core.models;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.eclipse.EclipseProject;
import org.junit.Before;
import org.junit.Test;

import com.google.common.util.concurrent.ListenableFuture;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class DefaultModelProviderTest {

    GradleIdeConnector connector;
    GradleOperationCustomizer operationCustomizer;
    DefaultModelProvider modelProvider;
    
    @Before
    public void setup() {
        connector = mock(GradleIdeConnector.class);
        operationCustomizer = mock(GradleOperationCustomizer.class);
        modelProvider = new DefaultModelProvider(connector, operationCustomizer);
    }

    @Test
    public void returnNullForNotYetLoadedModels() {
        assertNull(modelProvider.getModelIfLoaded(EclipseProject.class));
    }

    @Test
    public void returnModelFromProvider() throws Exception {
        ProjectConnection conn = mock(ProjectConnection.class);
        ModelBuilder<EclipseProject> builder = mock(ModelBuilder.class); 
        EclipseProject model = mock(EclipseProject.class);
        when(connector.getConnection()).thenReturn(conn);
        when(conn.model(EclipseProject.class)).thenReturn(builder);
        when(builder.get()).thenReturn(model);

        ListenableFuture<EclipseProject> futureProject = modelProvider.getModel(EclipseProject.class);
        assertNotNull(futureProject);
        assertSame(model, futureProject.get());
        verify(operationCustomizer).execute(builder);
        verify(builder).get();
        verify(operationCustomizer).close();
    }

    @Test
    public void loadedModelReturnedFromCache() throws Exception {
        ProjectConnection conn = mock(ProjectConnection.class);
        ModelBuilder<EclipseProject> builder = mock(ModelBuilder.class); 
        EclipseProject model = mock(EclipseProject.class);
        when(connector.getConnection()).thenReturn(conn);
        when(conn.model(EclipseProject.class)).thenReturn(builder);
        when(builder.get()).thenReturn(model);

        ListenableFuture<EclipseProject> futureProject = modelProvider.getModel(EclipseProject.class);
        futureProject.get();

        EclipseProject project = modelProvider.getModelIfLoaded(EclipseProject.class);
        assertSame(model, project);
        verify(operationCustomizer).execute(builder);
        verify(builder).get();
        verify(operationCustomizer).close();
    }

    @Test
    public void secondLoadReturnedFromCache() throws Exception {
        ProjectConnection conn = mock(ProjectConnection.class);
        ModelBuilder<EclipseProject> builder = mock(ModelBuilder.class); 
        EclipseProject model = mock(EclipseProject.class);
        when(connector.getConnection()).thenReturn(conn);
        when(conn.model(EclipseProject.class)).thenReturn(builder);
        when(builder.get()).thenReturn(model);

        ListenableFuture<EclipseProject> futureProject = modelProvider.getModel(EclipseProject.class);
        futureProject.get();

        ListenableFuture<EclipseProject> futureProject2 = modelProvider.getModel(EclipseProject.class);
        assertSame(model, futureProject2.get());
        verify(operationCustomizer).execute(builder);
        verify(builder).get();
        verify(operationCustomizer).close();
    }

    @Test
    public void propagatesExceptionModelFromProvider() throws Exception {
        ProjectConnection conn = mock(ProjectConnection.class);
        ModelBuilder<EclipseProject> builder = mock(ModelBuilder.class); 
        // EclipseProject model = mock(EclipseProject.class);
        RuntimeException ex = new RuntimeException("testing");
        when(connector.getConnection()).thenReturn(conn);
        when(conn.model(EclipseProject.class)).thenReturn(builder);
        when(builder.get()).thenThrow(ex);
        doThrow(new IOException("Pretend the stream is closed.")).when(operationCustomizer).close();

        ListenableFuture<EclipseProject> futureProject = modelProvider.getModel(EclipseProject.class);
        assertNotNull(futureProject);
        try {
            futureProject.get();
        } catch (Exception e) {
            assertTrue(e instanceof ExecutionException);
            assertSame(ex, e.getCause().getCause());
        }
        verify(operationCustomizer).execute(builder);
        verify(builder).get();
        verify(operationCustomizer).close();
    }
}
