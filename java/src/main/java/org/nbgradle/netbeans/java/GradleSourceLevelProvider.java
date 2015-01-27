package org.nbgradle.netbeans.java;

import org.nbgradle.netbeans.project.ModelProcessor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.gradle.tooling.model.idea.IdeaProject;
import org.nbgradle.netbeans.project.AbstractModelProducer;
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
public final class GradleSourceLevelProvider extends AbstractModelProducer<IdeaProject>
        implements SourceLevelQueryImplementation2 {
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
    private final SourceLevelResult sourceLevel = new SourceLevelResult();

    public GradleSourceLevelProvider(Project project, Lookup baseLookup) {
        super(baseLookup, IdeaProject.class);
        this.project = Preconditions.checkNotNull(project);
    }

    @Override
    protected void updateFromModel(IdeaProject ideaModel) {
        if (ideaModel == null) {
            // maybe it is better to hold previous state.
            return;
        }
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
