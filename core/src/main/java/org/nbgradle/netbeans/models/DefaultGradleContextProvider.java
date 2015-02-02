/*
 */
package org.nbgradle.netbeans.models;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.ByteSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.nbgradle.netbeans.project.GradleProjectImporter;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.nbgradle.netbeans.project.lookup.NbGradleOperationCustomizer;
import org.nbgradle.netbeans.project.lookup.ProjectInfoNode;

/**
 *
 * @author radim
 */
public class DefaultGradleContextProvider implements GradleContextProvider {

    private final LoadingCache<File, GradleContext> providerCache;

    public DefaultGradleContextProvider() {
        providerCache = CacheBuilder.newBuilder()
                .weakValues()
                .build(new CacheLoader<File, GradleContext>() {
                    @Override
                    public GradleContext load(File projectDir) throws Exception {
                        GradleProjectImporter.ImportedData settings = importSettingsData(projectDir);
                        GradleIdeConnector connector = new GradleIdeConnector(settings.buildSettings, projectDir);
                        return new DefaultGradleContext(settings.buildSettings, settings.projectTree, connector);
                    }

                });
    }
    @Override
    public GradleContext forProject(File projectDir) {
        return providerCache.getUnchecked(projectDir);
    }

    private static GradleProjectImporter.ImportedData importSettingsData(final File projectDir) {
        ByteSource settingsByteSource = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                File settings = new File(projectDir, NbGradleConstants.NBGRADLE_BUILD_XML);
                return settings.exists() ? new FileInputStream(settings) : ByteSource.empty().openStream();
            }
        };
        GradleProjectImporter.ImportedData importedData = new GradleProjectImporter().readBuildSettings(settingsByteSource);
        return importedData;
    }

    private static class DefaultGradleContext implements GradleContext {
        private final GradleBuildSettings buildSettings;
        private final ProjectInfoNode projectTreeInformation;
        private final GradleRunner runner;
        private final ModelProvider defaultModelProvider;

        public DefaultGradleContext(GradleBuildSettings buildSettings, ProjectInfoNode projectTreeInformation, GradleIdeConnector connector) {
            this.buildSettings = buildSettings;
            this.projectTreeInformation = projectTreeInformation;
            runner = new DefaultGradleToolingRunner(connector, new NbGradleOperationCustomizer(buildSettings));
            defaultModelProvider = new DefaultModelProvider(runner);
        }

        @Override
        public GradleBuildSettings getBuildSettings() {
            return buildSettings;
        }

        @Override
        public GradleRunner getRunner() {
            return runner;
        }

        @Override
        public ProjectInfoNode getProjectTreeInformation() {
            return projectTreeInformation;
        }

        @Override
        public ModelProvider getModelProvider() {
            return defaultModelProvider;
        }

    }
}
