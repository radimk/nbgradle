package org.nbgradle.netbeans.java;

import org.nbgradle.netbeans.project.ModelProcessor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Objects;
import java.util.concurrent.Phaser;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.gradle.api.Nullable;
import org.gradle.tooling.model.idea.IdeaProject;
import org.nbgradle.netbeans.models.ModelProvider;
import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.queries.SourceLevelQueryImplementation2;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;

/**
 *
 * @author radim
 */
@ProjectServiceProvider(service={SourceLevelQueryImplementation2.class, ModelProcessor.class},
        projectType=NbGradleConstants.PROJECT_TYPE)
public final class GradleSourceLevelProvider implements SourceLevelQueryImplementation2, ModelProcessor {
    private static final Logger LOG = Logger.getLogger(GradleSourceLevelProvider.class.getName());

    private static final ImmutableMap<String, String> sourceLevels = ImmutableMap.<String, String>builder()
            .put("JDK_1_2", "1.2")
            .put("JDK_1_3", "1.3")
            .put("JDK_1_4", "1.4")
            .put("JDK_1_5", "1.5")
            .put("JDK_1_6", "1.6")
            .put("JDK_1_7", "1.7")
            .put("JDK_1_8", "1.8")
            .put("JDK_1_9", "1.9")
            .build();


    private final @NonNull Project project;
    private final @NonNull ModelProvider modelProvider;
    private final SourceLevelResult sourceLevel = new SourceLevelResult();

    public GradleSourceLevelProvider(Project project, Lookup baseLookup) {
        this.project = Preconditions.checkNotNull(project);
        modelProvider = baseLookup.lookup(ModelProvider.class);
    }

    @Override
    public void loadFromGradle(final Phaser phaser) {
        phaser.register();
        ListenableFuture<IdeaProject> ideaModel = modelProvider.getModel(IdeaProject.class);
        Futures.addCallback(ideaModel, new FutureCallback<IdeaProject>() {

            @Override
            public void onSuccess(IdeaProject model) {
                try {
                    LOG.log(Level.INFO, "Processing source from IDEA");
                    updateSourceLevel(model);
                } finally {
                    phaser.arriveAndDeregister();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                try {
                    LOG.log(Level.INFO, "Cannot get source level using idea model", t);
                    updateSourceLevel(null);
                } finally {
                    phaser.arriveAndDeregister();
                }
            }
        });
    }

    private void updateSourceLevel(@Nullable IdeaProject ideaModel) {
        if (ideaModel == null) {
            // maybe it is better to hold previous state.
            return;
        }
        LOG.log(Level.INFO, "obtained level " + ideaModel.getLanguageLevel().getLevel());
        sourceLevel.setSourceLevel(sourceLevels.get(ideaModel.getLanguageLevel().getLevel()));

    }

    @Override
    public Result getSourceLevel(FileObject javaFile) {
        return sourceLevel;
    }

    private class SourceLevelResult implements SourceLevelQueryImplementation2.Result {
        private final ChangeSupport pcs = new ChangeSupport(this);
        private String sourceLevel;

        @Override
        public String getSourceLevel() {
            return sourceLevel;
        }

        public void setSourceLevel(String sourceLevel) {
            LOG.log(Level.INFO, "set level to " + sourceLevel);
            if (!Objects.equals(this.sourceLevel, sourceLevel)) {
                LOG.log(Level.FINE, "Set source level for {0} to {1}", new Object[] {project, sourceLevel});
                this.sourceLevel = sourceLevel;
                pcs.fireChange();
            }
        }

        @Override
        public void addChangeListener(ChangeListener listener) {
            pcs.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) {
            pcs.removeChangeListener(listener);
        }
    }
}
