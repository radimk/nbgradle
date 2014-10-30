package org.nbgradle.netbeans.project.lookup;

import com.gradleware.tooling.eclipse.core.models.GradleOperationCustomizer;
import org.gradle.tooling.LongRunningOperation;

import java.io.IOException;

public class NbGradleOperationCustomizer implements GradleOperationCustomizer {
    @Override
    public void execute(LongRunningOperation longRunningOperation) {
    }

    @Override
    public void close() throws IOException {
    }
}
