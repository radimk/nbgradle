package org.nbgradle.netbeans.models;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class DefaultModelProvider implements ModelProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultModelProvider.class);

    private final class ModelLoader extends CacheLoader<Class<?>, Object> {
        @Override
        public Object load(Class<?> modelType) throws Exception {
            return DefaultModelProvider.this.runner.getModel(modelType);
        }
    }

    private static final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(50));

    private final GradleRunner runner;
    private final LoadingCache<Class<?>, Object> loadedModels;

    public DefaultModelProvider(GradleRunner runner) {
        this.runner = runner;
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
