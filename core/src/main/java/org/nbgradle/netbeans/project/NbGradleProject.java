package org.nbgradle.netbeans.project;

import java.util.concurrent.Phaser;
import org.gradle.jarjar.com.google.common.base.Preconditions;
import org.nbgradle.netbeans.project.lookup.DefaultGradleProjectInformation;
import org.nbgradle.netbeans.project.lookup.NbSubprojectProvider;
import org.nbgradle.netbeans.project.ui.GradleLogicalViewProvider;
import org.nbgradle.netbeans.project.ui.customizer.ProjectCustomizerProvider;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.LookupProviderSupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.nbgradle.netbeans.models.GradleContext;
import org.nbgradle.netbeans.project.lookup.ProjectLoadingHook;
import org.nbgradle.netbeans.project.lookup.ProjectInfoNode;

public class NbGradleProject implements Project {
    private static final Logger LOG = Logger.getLogger(NbGradleProject.class.getName());

    private final FileObject projectDirectory;
    private final Lookup lookup;
    public final Phaser phaser = new Phaser(1);

    public NbGradleProject(GradleContext gradleContext, FileObject projectDirectory, ProjectInfoNode currentProject) {
        this.projectDirectory = Preconditions.checkNotNull(projectDirectory);
        lookup = createLookup(gradleContext, currentProject);
        LOG.log(Level.FINE, "Created Gradle project for {0}", projectDirectory);
    }

    private Lookup createLookup(GradleContext gradleContext, ProjectInfoNode currentProject) {

        Lookup base = Lookups.fixed(
                this,
                gradleContext.getBuildSettings(),
                new DefaultGradleProjectInformation(this, currentProject.getPath()),
                new NbSubprojectProvider(gradleContext.getProjectTreeInformation()),
                gradleContext.getRunner(),
                gradleContext.getModelProvider(),
                new GradleLogicalViewProvider(this),
                new ProjectCustomizerProvider(this),
                // new ProjectLoadingHook(this),
                LookupProviderSupport.createActionProviderMerger());
        return LookupProviderSupport.createCompositeLookup(base, "Projects/" + NbGradleConstants.PROJECT_TYPE + "/Lookup");
    }

    public void loadGradleModels() {
        LOG.log(Level.INFO, "Reloading Gradle models in project {0}", this);
        for (ModelProcessor processor : getLookup().lookupAll(ModelProcessor.class)) {
            LOG.log(Level.FINE, "Calling ModelProcessor {0} hook", processor);
            processor.loadFromGradle(phaser);
        }

    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDirectory;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public String toString() {
        return "NbGradleProject{" + "projectDirectory=" + projectDirectory + '}';
    }
}
