/*
 */
package org.nbgradle.netbeans.models;

import org.nbgradle.netbeans.project.lookup.ProjectInfoNode;

/**
 *
 * @author radim
 */
public interface GradleContext {

    GradleBuildSettings getBuildSettings();
    ProjectInfoNode getProjectTreeInformation();
    GradleRunner getRunner();
    ModelProvider getModelProvider();
    // ModelProvider getModelProvider(GradleOperationCustomizer opCustomizer);
}
