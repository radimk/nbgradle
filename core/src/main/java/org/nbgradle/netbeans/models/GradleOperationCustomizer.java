package org.nbgradle.netbeans.models;

import java.io.Closeable;

import org.gradle.api.Action;
import org.gradle.tooling.LongRunningOperation;

/**
 * Customizes operation performed by Tooling API before it is executed to hook
 * it into IDE infrastructure.
 */
public interface GradleOperationCustomizer extends Action<LongRunningOperation>, Closeable {
}
