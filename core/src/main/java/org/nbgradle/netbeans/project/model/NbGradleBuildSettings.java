package org.nbgradle.netbeans.project.model;

import org.nbgradle.netbeans.models.GradleBuildSettings;

public interface NbGradleBuildSettings extends GradleBuildSettings {
    DistributionSettings getDistributionSettings();
}
