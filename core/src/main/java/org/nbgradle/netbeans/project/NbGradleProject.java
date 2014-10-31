package org.nbgradle.netbeans.project;

import com.google.common.io.ByteSource;
import com.gradleware.tooling.eclipse.core.models.*;
import org.gradle.jarjar.com.google.common.base.Preconditions;
import org.nbgradle.netbeans.project.lookup.DefaultGradleProjectInformation;
import org.nbgradle.netbeans.project.lookup.NbGradleOperationCustomizer;
import org.nbgradle.netbeans.project.ui.GradleLogicalViewProvider;
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
        GradleBuildSettings buildSettings = new GradleProjectImporter().readBuildSettings(settingsByteSource);
        GradleIdeConnector connector = new GradleIdeConnector(buildSettings, projectDir);
        GradleRunner runner = new DefaultGradleToolingRunner(connector, new NbGradleOperationCustomizer());
        ModelProvider modelProvider = new DefaultModelProvider(runner);
        Lookup base = Lookups.fixed(
                buildSettings,
                new DefaultGradleProjectInformation(this, ":"),
                runner,
                modelProvider,
                new GradleLogicalViewProvider(this),
                LookupProviderSupport.createActionProviderMerger());
        // lookup = base; // a workaround for merged lookups calling Project.getLookup too early
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
}
