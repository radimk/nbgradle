package org.nbgradle.netbeans.project.lookup;

public interface GradleModelSupplier {
    <T> T loadModel(Class<T> clz);
    <T> T getModel(Class<T> clz);
    void reload();
}
