package com.gradleware.tooling.eclipse.core.models;

import org.gradle.tooling.ProjectConnection;

public interface IdeConnector {

    ProjectConnection getConnection();

}
