package org.nbgradle.netbeans.models;

import com.google.common.util.concurrent.ListenableFuture;

public interface ModelProvider {

    <T> T getModelIfLoaded(Class<T> clz);
    <T> ListenableFuture<T> getModel(Class<T> clz);
    void invalidateAll();
}
