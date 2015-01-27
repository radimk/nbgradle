package org.nbgradle.netbeans.java;

import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;

/**
 *
 * @author radim
 */
@ProjectServiceProvider(service={ProjectOpenedHook.class}, projectType=NbGradleConstants.PROJECT_TYPE)
public class ClasspathRegistrationHook extends ProjectOpenedHook {
    private final @NonNull Project project;

    public ClasspathRegistrationHook(Project project) {
        this.project = project;
    }

    @Override
    protected void projectOpened() {
        RegisteredClassPathProvider cpp = project.getLookup().lookup(RegisteredClassPathProvider.class);
        cpp.register();
    }

    @Override
    protected void projectClosed() {
        RegisteredClassPathProvider cpp = project.getLookup().lookup(RegisteredClassPathProvider.class);
        cpp.unregister();
    }

}
