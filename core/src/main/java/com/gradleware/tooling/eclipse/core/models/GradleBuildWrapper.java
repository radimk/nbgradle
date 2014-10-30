package com.gradleware.tooling.eclipse.core.models;

import java.io.File;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class GradleBuildWrapper {

    /**
     * Wrapper for values used to create model provider using only a project directory as a key.
     */
    private static class BuildKey {
        private final File projectDir;
        private final GradleBuildSettings buildSettings;
        private final GradleOperationCustomizer operationCustomizer;

        public BuildKey(File projectDir, GradleBuildSettings buildSettings, GradleOperationCustomizer operationCustomizer) {
            this.projectDir = projectDir;
            this.buildSettings = buildSettings;
            this.operationCustomizer = operationCustomizer;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((projectDir == null) ? 0 : projectDir.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            BuildKey other = (BuildKey) obj;
            if (projectDir == null) {
                if (other.projectDir != null) {
                    return false;
                }
            } else if (!projectDir.equals(other.projectDir)) {
                return false;
            }
            return true;
        }
    }

    // TODO change value to a container wrapping model provider and tooling runner to share connection
    private final LoadingCache<BuildKey, ModelProvider> providerCache;

    public GradleBuildWrapper() {
        providerCache = CacheBuilder.newBuilder()
                .weakValues()
                .build(new CacheLoader<BuildKey, ModelProvider>() {
                    @Override
                    public ModelProvider load(BuildKey key) throws Exception {
                        GradleIdeConnector connector = new GradleIdeConnector(key.buildSettings, key.projectDir);
                        GradleRunner runner = new DefaultGradleToolingRunner(connector, key.operationCustomizer);
                        return new DefaultModelProvider(runner);
                    }
                });
    }

    public ModelProvider forProject(File projectDir, GradleBuildSettings buildSettings, GradleOperationCustomizer operationCustomizer) {
        return providerCache.getUnchecked(
                new BuildKey(projectDir, buildSettings, operationCustomizer));
    }
}
