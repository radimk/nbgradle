package org.nbgradle.netbeans.project.model;

import com.gradleware.tooling.eclipse.core.models.DistributionSpec;

public abstract class DistributionSettings {
    public abstract DistributionSpec toSpec();
}
