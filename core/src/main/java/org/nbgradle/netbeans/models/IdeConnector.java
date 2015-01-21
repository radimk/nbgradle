package org.nbgradle.netbeans.models;

import org.gradle.tooling.ProjectConnection;

public interface IdeConnector {

    ProjectConnection getConnection();

}
