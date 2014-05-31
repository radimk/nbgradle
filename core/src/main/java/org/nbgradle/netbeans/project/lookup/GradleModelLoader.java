package org.nbgradle.netbeans.project.lookup;

public interface GradleModelLoader {
    <M> M getModel(Class<M> clz);
}
