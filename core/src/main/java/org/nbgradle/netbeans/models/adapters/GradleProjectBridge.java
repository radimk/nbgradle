/*
 */
package org.nbgradle.netbeans.models.adapters;

import org.gradle.api.Nullable;
import org.gradle.tooling.model.GradleProject;
import org.nbgradle.netbeans.project.lookup.GradleProjectInformation;
import org.netbeans.api.project.Project;

/**
 *
 * @author radim
 */
public class GradleProjectBridge {
    private final GradleProject model;

    public GradleProjectBridge(GradleProject ideaModel) {
        this.model = ideaModel;
    }

    public @Nullable GradleProject moduleForProject(Project project) {
        return model.findByPath(project.getLookup().lookup(GradleProjectInformation.class).getProjectPath());
    }
}
