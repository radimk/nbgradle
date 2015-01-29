/*
 */
package org.nbgradle.netbeans.models;

import org.nbgradle.netbeans.project.lookup.ProjectTreeInformation;

/**
 *
 * @author radim
 */
public interface GradleContext {

    GradleBuildSettings getBuildSettings();
    ProjectTreeInformation getProjectTreeInformation();
    GradleRunner getRunner();
    ModelProvider getModelProvider();
    // ModelProvider getModelProvider(GradleOperationCustomizer opCustomizer);
}
