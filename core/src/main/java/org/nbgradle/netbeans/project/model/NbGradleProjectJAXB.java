package org.nbgradle.netbeans.project.model;

import org.nbgradle.netbeans.project.lookup.ProjectTreeInformation;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.List;

@XmlRootElement(name = "project")
public class NbGradleProjectJAXB implements ProjectTreeInformation {
    private String name;
    private String path;
    private File projectDirectory;

    private List<NbGradleProjectJAXB> childProjects;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getProjectDirectory() {
        return projectDirectory;
    }

    public void setProjectDirectory(File projectDirectory) {
        this.projectDirectory = projectDirectory;
    }

    public List<NbGradleProjectJAXB> getChildProjects() {
        return childProjects;
    }

    public void setChildProjects(List<NbGradleProjectJAXB> childProjects) {
        this.childProjects = childProjects;
    }
}
