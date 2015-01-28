/*
 */
package org.nbgradle.netbeans.models.idea;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.gradle.api.Nullable;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;

/**
 *
 * @author radim
 */
public class IdeaModelHelper {
    private final IdeaProject ideaModel;

    public IdeaModelHelper(IdeaProject ideaModel) {
        this.ideaModel = ideaModel;
    }

    public @Nullable IdeaModule moduleForProject(String projectPath) {
        return Iterables.find(
                ideaModel.getModules(),
                new Predicate<IdeaModule>() {
                    @Override
                    public boolean apply(IdeaModule input) {
                        return ":".equals(input.getGradleProject().getPath());
                    }
                },
                null);
    }
}
