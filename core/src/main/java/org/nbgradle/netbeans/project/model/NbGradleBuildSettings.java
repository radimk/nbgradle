package org.nbgradle.netbeans.project.model;

import com.gradleware.tooling.eclipse.core.models.GradleBuildSettings;

public interface NbGradleBuildSettings extends GradleBuildSettings {
    DistributionSettings getDistributionSettings();
}
