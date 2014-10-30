package com.gradleware.tooling.eclipse.core.models;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.gradle.tooling.ModelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class DefaultModelProvider implements ModelProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultModelProvider.class);

    private final class ModelLoader extends CacheLoader<Class<?>, Object> {
        @Override
        public Object load(Class<?> modelType) throws Exception {
            ModelBuilder<?> modelBuilder = DefaultModelProvider.this.connector.getConnection().model(modelType);
            operationCustomizer.execute(modelBuilder);
            try {
                return modelBuilder.get();
            } finally {
                try {
                    operationCustomizer.close();
                } catch (IOException ioe) {
                    log.info("Model loading cleanup failed", ioe);
                }
            }
        }
    }

    private static final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(50));

    private final GradleIdeConnector connector;
    private final GradleOperationCustomizer operationCustomizer;
    private final LoadingCache<Class<?>, Object> loadedModels;

    public DefaultModelProvider(GradleIdeConnector connector, GradleOperationCustomizer operationCustomizer) {
        this.connector = connector;
        this.operationCustomizer = operationCustomizer;
        loadedModels = CacheBuilder.newBuilder()
                .weakValues()
                .build(new ModelLoader());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getModelIfLoaded(Class<T> clz) {
        return (T) loadedModels.getIfPresent(clz);
    }

    @Override
    public <T> ListenableFuture<T> getModel(final Class<T> clz) {
        return executor.submit(new Callable<T>() {

            @SuppressWarnings("unchecked")
            @Override
            public T call() throws Exception {
                return (T) loadedModels.get(clz);
            }
        });
    }

    @Override
    public void invalidateAll() {
        loadedModels.invalidateAll();
    }

}
