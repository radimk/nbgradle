package com.gradleware.tooling.eclipse.core.models;

import org.nbgradle.netbeans.models.GradleBuildSettings;
import org.nbgradle.netbeans.models.DistributionSpecs;
import org.nbgradle.netbeans.models.GradleIdeConnector;
import org.gradle.integtests.fixtures.TestDirectoryProvider;
import org.gradle.tooling.ProjectConnection;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class GradleIdeConnectorTest {
    @Rule
    public final TestDirectoryProvider temporaryFolder = new TestDirectoryProvider();

    @Test
    public void createsConnector() {
        GradleBuildSettings buildSettings = mock(GradleBuildSettings.class);
        File projectDir = temporaryFolder.getTestDirectory();
        GradleIdeConnector connector = new GradleIdeConnector(buildSettings, projectDir);
        when(buildSettings.getDistributionSpec()).thenReturn(DistributionSpecs.defaultDistribution());

        ProjectConnection conn = connector.getConnection();
        assertNotNull(conn);
    }
}
