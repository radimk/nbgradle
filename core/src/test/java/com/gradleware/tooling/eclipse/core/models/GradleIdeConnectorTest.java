package com.gradleware.tooling.eclipse.core.models;

import org.gradle.tooling.ProjectConnection;
import org.junit.Rule;
import org.junit.Test;
import org.nbgradle.test.fixtures.TestNameTestDirectoryProvider;

import java.io.File;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class GradleIdeConnectorTest {
    @Rule
    public final TestNameTestDirectoryProvider temporaryFolder = new TestNameTestDirectoryProvider();

    @Test
    public void createsConnector() {
        GradleBuildSettings buildSettings = mock(GradleBuildSettings.class);
        File projectDir = temporaryFolder.getTestDirectory().toFile();
        GradleIdeConnector connector = new GradleIdeConnector(buildSettings, projectDir);
        when(buildSettings.getDistributionSpec()).thenReturn(DistributionSpecs.defaultDistribution());

        ProjectConnection conn = connector.getConnection();
        assertNotNull(conn);
    }
}
