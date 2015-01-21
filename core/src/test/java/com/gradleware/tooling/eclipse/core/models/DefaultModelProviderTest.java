package com.gradleware.tooling.eclipse.core.models;

import org.nbgradle.netbeans.models.GradleRunner;
import org.nbgradle.netbeans.models.DefaultModelProvider;
import com.google.common.util.concurrent.ListenableFuture;
import org.gradle.tooling.model.eclipse.EclipseProject;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultModelProviderTest {

    GradleRunner runner;
    DefaultModelProvider modelProvider;
    
    @Before
    public void setup() {
        runner = mock(GradleRunner.class);
        modelProvider = new DefaultModelProvider(runner);
    }

    @Test
    public void returnNullForNotYetLoadedModels() {
        assertNull(modelProvider.getModelIfLoaded(EclipseProject.class));
    }

    @Test
    public void returnModelFromProvider() throws Exception {
        EclipseProject model = mock(EclipseProject.class);
        when(runner.getModel(EclipseProject.class)).thenReturn(model);

        ListenableFuture<EclipseProject> futureProject = modelProvider.getModel(EclipseProject.class);
        assertNotNull(futureProject);
        assertSame(model, futureProject.get());
    }

    @Test
    public void loadedModelReturnedFromCache() throws Exception {
        EclipseProject model = mock(EclipseProject.class);
        when(runner.getModel(EclipseProject.class)).thenReturn(model);

        modelProvider.getModel(EclipseProject.class).get();

        EclipseProject project = modelProvider.getModelIfLoaded(EclipseProject.class);
        assertSame(model, project);
        verify(runner, times(1)).getModel(EclipseProject.class);
    }

    @Test
    public void secondLoadReturnedFromCache() throws Exception {
        EclipseProject model = mock(EclipseProject.class);
        when(runner.getModel(EclipseProject.class)).thenReturn(model);

        ListenableFuture<EclipseProject> futureProject = modelProvider.getModel(EclipseProject.class);
        futureProject.get();

        ListenableFuture<EclipseProject> futureProject2 = modelProvider.getModel(EclipseProject.class);
        assertSame(model, futureProject2.get());
        verify(runner, times(1)).getModel(EclipseProject.class);
    }

    @Test
    public void propagatesExceptionModelFromProvider() throws Exception {
        EclipseProject model = mock(EclipseProject.class);
        RuntimeException ex = new RuntimeException("testing");
        when(runner.getModel(EclipseProject.class)).thenThrow(ex);

        ListenableFuture<EclipseProject> futureProject = modelProvider.getModel(EclipseProject.class);
        assertNotNull(futureProject);
        try {
            futureProject.get();
        } catch (Exception e) {
            assertTrue(e instanceof ExecutionException);
            assertSame(ex, e.getCause().getCause());
        }
        verify(runner, times(1)).getModel(EclipseProject.class);
    }
}
