package org.nbgradle.netbeans.project;

/**
 *
 * @author radim
 */
public class DefaultGradleProjectPath implements NbGradleProjectPath {

    private final String projectPath;

    public DefaultGradleProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    @Override
    public String getProjectPath() {
        return projectPath;
    }
}
