package org.nbgradle.netbeans.models;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class GradleBuildWrapperTest {

    @Test
    public void simpleCreate() throws Exception {
        File projectDir = mock(File.class);
        GradleBuildSettings buildSettings = mock(GradleBuildSettings.class);
        GradleOperationCustomizer operationCustomizer = mock(GradleOperationCustomizer.class);

        ModelProvider modelProvider = new GradleBuildWrapper().forProject(projectDir, buildSettings, operationCustomizer);
        assertNotNull(modelProvider);
    }

    @Test
    public void sameProviderForTheSameDirectory() throws Exception {
        GradleBuildWrapper build = new GradleBuildWrapper();

        File projectDir = mock(File.class);
        GradleBuildSettings buildSettings = mock(GradleBuildSettings.class);
        GradleOperationCustomizer operationCustomizer = mock(GradleOperationCustomizer.class);

        ModelProvider modelProvider1 = build.forProject(projectDir, buildSettings, operationCustomizer);
        ModelProvider modelProvider2 = build.forProject(projectDir, buildSettings, operationCustomizer);
        assertSame(modelProvider1, modelProvider2);
    }
}
