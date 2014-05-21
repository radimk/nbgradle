package org.nbgradle.netbeans.project.model;

import java.io.File;

/**
 * Created by radim on 5/21/14.
 */
public interface GradleBuildSettings {

    DistributionSpec getDistributionSpec();
    File getGradleUserHomeDir();
}
