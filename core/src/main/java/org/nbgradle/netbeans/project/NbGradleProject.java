package org.nbgradle.netbeans.project;

import com.google.common.io.ByteSource;
import org.gradle.jarjar.com.google.common.base.Preconditions;
import org.nbgradle.netbeans.project.lookup.DefaultGradleToolingRunner;
import org.nbgradle.netbeans.project.lookup.DefaultGradleModelSupplier;
import org.nbgradle.netbeans.project.lookup.DefaultGradleProjectInformation;
import org.nbgradle.netbeans.project.lookup.GradleToolingRunner;
import org.nbgradle.netbeans.project.model.GradleBuildSettings;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.LookupProviderSupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class NbGradleProject implements Project {
    private final FileObject projectDirectory;
    private final File projectDir;
    private /*final*/ Lookup lookup;

    public NbGradleProject(FileObject projectDirectory, File projectDir) {
        this.projectDirectory = Preconditions.checkNotNull(projectDirectory);
        this.projectDir = Preconditions.checkNotNull(projectDir);
        lookup = createLookup(projectDirectory);
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
        GradleToolingRunner toolingRunner = new DefaultGradleToolingRunner(buildSettings, projectDir);
        Lookup base = Lookups.fixed(
                buildSettings,
                new DefaultGradleProjectInformation(this, ":"),
                toolingRunner,
                new DefaultGradleModelSupplier(toolingRunner),
                LookupProviderSupport.createActionProviderMerger());
        lookup = base; // a workaround for merged lookups calling Project.getLookup too early
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
