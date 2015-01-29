package org.nbgradle.netbeans.project;

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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nbgradle.netbeans.models.GradleContext;
import org.nbgradle.netbeans.models.GradleContextProvider;
import org.nbgradle.netbeans.project.lookup.ProjectLoadingHook;

public class NbGradleProject implements Project {
    private static final Logger LOG = Logger.getLogger(NbGradleProject.class.getName());

    private final FileObject projectDirectory;
    private final File projectDir;
    private final Lookup lookup;

    public NbGradleProject(GradleContextProvider contextProvider, FileObject projectDirectory, File projectDir) {
        this.projectDirectory = Preconditions.checkNotNull(projectDirectory);
        this.projectDir = Preconditions.checkNotNull(projectDir);
        lookup = createLookup(contextProvider, projectDir);
        LOG.log(Level.FINE, "Created Gradle project for {0}", projectDir.getAbsolutePath());
    }

    private Lookup createLookup(GradleContextProvider contextProvider, final File projectDir) {
        GradleContext gradleContext = contextProvider.forProject(projectDir);
        Lookup base = Lookups.fixed(
                this,
                gradleContext.getBuildSettings(),
                new DefaultGradleProjectInformation(this, ":"),
                new NbSubprojectProvider(gradleContext.getProjectTreeInformation()),
                gradleContext.getRunner(),
                gradleContext.getModelProvider(),
                new GradleLogicalViewProvider(this),
                new ProjectCustomizerProvider(this),
                new ProjectLoadingHook(this),
                LookupProviderSupport.createActionProviderMerger());
        return LookupProviderSupport.createCompositeLookup(base, "Projects/" + NbGradleConstants.PROJECT_TYPE + "/Lookup");
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
