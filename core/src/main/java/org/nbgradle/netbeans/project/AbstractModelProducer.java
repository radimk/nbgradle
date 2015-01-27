package org.nbgradle.netbeans.project;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Phaser;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gradle.api.Nullable;
import org.nbgradle.netbeans.models.ModelProvider;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.project.Project;
import org.openide.util.Lookup;

/**
 *
 * @author radim
 */
public abstract class AbstractModelProducer<M> implements ModelProcessor {
    private static final Logger LOG = Logger.getLogger(AbstractModelProducer.class.getName());

    private final @NonNull ModelProvider modelProvider;
    private final @NonNull Class<M> modelClz;

    protected AbstractModelProducer(Lookup baseLookup, Class<M> modelClz) {
        this.modelClz = Preconditions.checkNotNull(modelClz);
        modelProvider = baseLookup.lookup(ModelProvider.class);
    }

    @Override
    public final void loadFromGradle(final Phaser phaser) {
        phaser.register();
        ListenableFuture<M> ideaModel = modelProvider.getModel(modelClz);
        Futures.addCallback(ideaModel, new FutureCallback<M>() {

            @Override
            public void onSuccess(M model) {
                try {
                    LOG.log(Level.INFO, "Processing source from IDEA");
                    updateFromModel(model);
                } finally {
                    phaser.arriveAndDeregister();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                try {
                    LOG.log(Level.INFO, "Cannot get source level using idea model", t);
                    updateFromModel(null);
                } finally {
                    phaser.arriveAndDeregister();
                }
            }
        });
    }

    protected abstract void updateFromModel(@Nullable M model);
}
