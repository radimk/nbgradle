package org.nbgradle.netbeans.project.lookup;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.gradle.tooling.model.Model;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultGradleModelSupplier implements GradleModelSupplier {
    private static final Logger LOGGER = Logger.getLogger(DefaultGradleModelSupplier.class.getName());
    private static final Executor EXECUTOR = Executors.newCachedThreadPool();
    private static final Model NOT_YET_LOADED_MODEL = new Model() {};

    private final GradleModelLoader modelLoader;
    private final LoadingCache<Class<?>, Object> modelsCache;

    public DefaultGradleModelSupplier(GradleModelLoader modelLoader) {
        this.modelLoader = Preconditions.checkNotNull(modelLoader);
        modelsCache = CacheBuilder.newBuilder()
                .build(new CacheLoader<Class<?>, Object>() {
                    public Object load(Class<?> model) { // no checked exception
                        return NOT_YET_LOADED_MODEL;
                    }

                    public ListenableFuture<Object> reload(final Class<?> key, Object prevModel) {
                        if (needsRefresh(prevModel)) {
                            // asynchronous!
                            ListenableFutureTask<Object> task = ListenableFutureTask.create(new Callable<Object>() {
                                public Object call() {
                                    return DefaultGradleModelSupplier.this.modelLoader.getModel(key);
                                }
                            });
                            EXECUTOR.execute(task);
                            return task;
                        } else {
                            return Futures.immediateFuture(prevModel);
                        }
                    }

                    private boolean needsRefresh(Object prevModel) {
                        return prevModel == NOT_YET_LOADED_MODEL;
                    }
                });

    }

    @Override
    public <T> T loadModel(Class<T> clz) {
        LOGGER.log(Level.FINE, "loadModel {0}", clz);
        try {
            return (T) modelsCache.get(clz);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T getModel(Class<T> clz) {
        LOGGER.log(Level.FINE, "getModel {0}", clz);
        Object model;
        try {
            model = modelsCache.get(clz);
        } catch (ExecutionException e) {
            return null;
        }
        if (model == NOT_YET_LOADED_MODEL) {
            modelsCache.refresh(clz);
            return null;
        }
        return (T) model;
    }

    @Override
    public void reload() {

    }
}
