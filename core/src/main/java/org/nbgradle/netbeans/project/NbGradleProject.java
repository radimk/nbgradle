package org.nbgradle.netbeans.project;

import org.nbgradle.netbeans.models.GradleIdeConnector;
import org.nbgradle.netbeans.models.GradleRunner;
import org.nbgradle.netbeans.models.ModelProvider;
import org.nbgradle.netbeans.models.DefaultModelProvider;
import org.nbgradle.netbeans.models.GradleBuildSettings;
import org.nbgradle.netbeans.models.DefaultGradleToolingRunner;
import com.google.common.io.ByteSource;
import org.gradle.jarjar.com.google.common.base.Preconditions;
import org.nbgradle.netbeans.project.lookup.DefaultGradleProjectInformation;
import org.nbgradle.netbeans.project.lookup.NbGradleOperationCustomizer;
import org.nbgradle.netbeans.project.lookup.NbSubprojectProvider;
import org.nbgradle.netbeans.project.ui.GradleLogicalViewProvider;
import org.nbgradle.netbeans.project.ui.customizer.ProjectCustomizerProvider;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.LookupProviderSupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nbgradle.netbeans.project.lookup.ProjectLoadingHook;

public class NbGradleProject implements Project {
    private static final Logger LOG = Logger.getLogger(NbGradleProject.class.getName());

    private final FileObject projectDirectory;
    private final File projectDir;
    private /*final*/ Lookup lookup;

    public NbGradleProject(FileObject projectDirectory, File projectDir) {
        this.projectDirectory = Preconditions.checkNotNull(projectDirectory);
        this.projectDir = Preconditions.checkNotNull(projectDir);
        lookup = createLookup(projectDirectory);
        LOG.log(Level.FINE, "Created Gradle project for {0}", projectDir.getAbsolutePath());
    }

    private Lookup createLookup(final FileObject projectDirectory) {
        ByteSource settingsByteSource = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                FileObject settings = projectDirectory.getFileObject(NbGradleConstants.NBGRADLE_BUILD_XML);
                return settings != null ? settings.getInputStream() : ByteSource.empty().openStream();
            }
        };
        GradleProjectImporter.ImportedData importedData = new GradleProjectImporter().readBuildSettings(settingsByteSource);
        GradleBuildSettings buildSettings = importedData.buildSettings;
        GradleIdeConnector connector = new GradleIdeConnector(buildSettings, projectDir);
        GradleRunner runner = new DefaultGradleToolingRunner(connector, new NbGradleOperationCustomizer());
        ModelProvider modelProvider = new DefaultModelProvider(runner);
        Lookup base = Lookups.fixed(
                this,
                buildSettings,
                new DefaultGradleProjectInformation(this, ":"),
                new NbSubprojectProvider(importedData.projectTree),
                runner,
                modelProvider,
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
