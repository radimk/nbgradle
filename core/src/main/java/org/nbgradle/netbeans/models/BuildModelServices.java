package org.nbgradle.netbeans.models;

import org.gradle.internal.service.DefaultServiceRegistry;

public class BuildModelServices {
    private static BuildModelServiceRegistry singletonRegistry = new BuildModelServiceRegistry();

    public static GradleBuildWrapper getGradleBuildWrapper() {
        return singletonRegistry.get(GradleBuildWrapper.class);
    }

    private static class BuildModelServiceRegistry extends DefaultServiceRegistry {
        @SuppressWarnings("unused")
        protected GradleBuildWrapper createGradleBuildWrapper() {
            return new GradleBuildWrapper();
        }
    }
}
