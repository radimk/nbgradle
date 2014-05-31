package org.nbgradle.netbeans.project.lookup;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public interface GradleProjectInformation extends ProjectInformation {
    String getProjectPath();
}
