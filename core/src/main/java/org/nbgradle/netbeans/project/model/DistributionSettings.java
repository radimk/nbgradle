package org.nbgradle.netbeans.project.model;

import org.nbgradle.netbeans.models.DistributionSpec;

public abstract class DistributionSettings {
    public enum Type {
        DEFAULT,
        FILE,
        VERSION,
        URI
    }

    public final Type type;
    private String version;

    protected DistributionSettings(Type type) {
        this.type = type;
    }

    public String getValue() {
        return version;
    }

    public void setValue(String version) {
        this.version = version;
    }

    public abstract DistributionSpec toSpec();
}
