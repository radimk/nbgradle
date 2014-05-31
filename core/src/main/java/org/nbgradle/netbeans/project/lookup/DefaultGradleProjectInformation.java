package org.nbgradle.netbeans.project.lookup;

import com.google.common.base.Preconditions;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class DefaultGradleProjectInformation implements GradleProjectInformation {
    private final Project project;
    private final String projectPath;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public DefaultGradleProjectInformation(Project project, String projectPath) {
        this.projectPath = Preconditions.checkNotNull(projectPath);
        this.project = Preconditions.checkNotNull(project);
    }

    @Override
    public String getProjectPath() {
        return projectPath;
    }

    @Override
    public String getName() {
        return project.getProjectDirectory().getNameExt();
    }

    @Override
    public String getDisplayName() {
        return project.getProjectDirectory().getNameExt();
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
