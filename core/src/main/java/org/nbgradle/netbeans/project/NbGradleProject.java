package org.nbgradle.netbeans.project;

import org.gradle.jarjar.com.google.common.base.Preconditions;
import org.nbgradle.netbeans.project.lookup.GradleProjectInformation;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

public class NbGradleProject implements Project {
    private final FileObject projectDirectory;

    public NbGradleProject(FileObject projectDirectory) {
        this.projectDirectory = Preconditions.checkNotNull(projectDirectory);
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDirectory;
    }

    @Override
    public Lookup getLookup() {
        return Lookups.fixed(
                new GradleProjectInformation(this),
                new GradleActionProvider());
    }
}
