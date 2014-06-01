package org.nbgradle.netbeans.project.lookup;

public interface GradleToolingRunner {
    <M> M getModel(Class<M> clz);
}
