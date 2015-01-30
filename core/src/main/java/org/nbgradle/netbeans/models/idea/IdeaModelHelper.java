/*
 */
package org.nbgradle.netbeans.models.idea;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.gradle.api.Nullable;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;
import org.nbgradle.netbeans.project.lookup.GradleProjectInformation;
import org.netbeans.api.project.Project;

/**
 *
 * @author radim
 */
public class IdeaModelHelper {
    private final IdeaProject ideaModel;

    public IdeaModelHelper(IdeaProject ideaModel) {
        this.ideaModel = ideaModel;
    }

    private @Nullable IdeaModule moduleForProject(final String projectPath) {
        return Iterables.find(
                ideaModel.getModules(),
                new Predicate<IdeaModule>() {
                    @Override
                    public boolean apply(IdeaModule input) {
                        return projectPath.equals(input.getGradleProject().getPath());
                    }
                },
                null);
    }

    public @Nullable IdeaModule moduleForProject(Project project) {
        return moduleForProject(project.getLookup().lookup(GradleProjectInformation.class).getProjectPath());
    }
}
