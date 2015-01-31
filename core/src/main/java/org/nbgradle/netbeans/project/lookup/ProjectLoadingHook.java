/*
 */
package org.nbgradle.netbeans.project.lookup;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.ProjectOpenedHook;

/**
 *
 * @author radim
 */
public class ProjectLoadingHook extends ProjectOpenedHook {
    private static final Logger LOG = Logger.getLogger(ProjectLoadingHook.class.getName());

    private final Project project;

    public ProjectLoadingHook(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {
        LOG.log(Level.INFO, "Project {0} opened", project);
    }

    @Override
    protected void projectClosed() {
        LOG.log(Level.INFO, "Project {0} closed", project);
    }

}
