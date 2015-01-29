package org.nbgradle.netbeans.project.lookup;

import java.io.File;
import java.util.List;

public interface ProjectInfoNode {
    String getName();
    String getPath();
    File getProjectDirectory();
    List<? extends ProjectInfoNode> getChildProjects();
}
