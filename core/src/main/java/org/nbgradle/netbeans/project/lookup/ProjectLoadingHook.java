/*
 */
package org.nbgradle.netbeans.project.lookup;

import java.util.concurrent.Phaser;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nbgradle.netbeans.project.ModelProcessor;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.ProjectOpenedHook;

/**
 *
 * @author radim
 */
public class ProjectLoadingHook extends ProjectOpenedHook {
    private static final Logger LOG = Logger.getLogger(ProjectLoadingHook.class.getName());

    public final Phaser phaser = new Phaser(1);
    private final Project project;

    public ProjectLoadingHook(Project project) {
        this.project = project;
    }

    @Override
    protected void projectOpened() {
        LOG.log(Level.FINE, "Project {0} opened", project);
        for (ModelProcessor processor : project.getLookup().lookupAll(ModelProcessor.class)) {
            LOG.log(Level.FINE, "Calling ModelProcessor {0} hook", processor);
            processor.loadFromGradle(phaser);
        }
    }

    @Override
    protected void projectClosed() {
        // TODO empty at the moment
    }

}
