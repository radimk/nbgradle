package org.nbgradle.test.fixtures;

import org.gradle.util.GradleVersion;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IntegrationTestBuildContext {

    public Path getGradleHomeDir() {
        return file("integTest.gradleHomeDir", null);
    }

    public Path getSamplesDir() {
        return file("integTest.samplesdir", String.format("%s/gradle-2.2-rc-1/samples", getGradleHomeDir()));
    }

//    public Path getLibsRepo() {
//        return file("integTest.libsRepo", "build/repo");
//    }
//
//    public Path getGradleUserHomeDir() {
//        return file("integTest.gradleUserHomeDir", "intTestHomeDir").resolve("worker-1");
//    }
//
//    public GradleVersion getVersion() {
//        return GradleVersion.current();
//    }

    private static Path file(String propertyName, String defaultFile) {
        String path = System.getProperty(propertyName, defaultFile);
        if (path == null) {
            throw new RuntimeException(String.format("You must set the '%s' property to run the integration tests. The default passed was: '%s'",
                    propertyName, defaultFile));
        }
        return Paths.get(path);
    }


}
