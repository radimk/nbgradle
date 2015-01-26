package org.nbgradle.netbeans.java;

import org.nbgradle.netbeans.project.ModelProcessor;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Phaser;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gradle.tooling.model.idea.IdeaProject;
import org.nbgradle.netbeans.models.ModelProvider;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author radim
 */
@ProjectServiceProvider(service={ClassPathProvider.class, ModelProcessor.class}, projectType=NbGradleConstants.PROJECT_TYPE)
public final class GradleProjectClasspathProvider implements ClassPathProvider, ModelProcessor {
    private static final Logger LOG = Logger.getLogger(GradleProjectClasspathProvider.class.getName());

    private final @NonNull ModelProvider modelProvider;

    public GradleProjectClasspathProvider(Project project, Lookup baseLookup) {
        Preconditions.checkNotNull(project);
        modelProvider = baseLookup.lookup(ModelProvider.class);
    }

    @Override
    public void loadFromGradle(final Phaser phaser) {
        phaser.register();
        modelProvider.getModel(IdeaProject.class).addListener(new Runnable() {

            @Override
            public void run() {
                LOG.log(Level.INFO, "Processing classpath from IDEA");
                // TODO compute classpath here
                phaser.arriveAndDeregister();
            }
        }, MoreExecutors.sameThreadExecutor());
    }

    @Override
    public ClassPath findClassPath(FileObject fo, String string) {
        return null;
    }

}
