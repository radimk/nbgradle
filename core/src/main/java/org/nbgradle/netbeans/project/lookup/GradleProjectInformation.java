package org.nbgradle.netbeans.project.lookup;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by radim on 5/20/14.
 */
public class GradleProjectInformation implements ProjectInformation {
    private final Project project;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public GradleProjectInformation(Project project) {
        this.project = project;
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
