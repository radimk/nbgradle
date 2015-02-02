package org.nbgradle.netbeans.project.lookup;

import org.nbgradle.netbeans.models.GradleOperationCustomizer;
import org.gradle.tooling.LongRunningOperation;

import java.io.IOException;
import org.nbgradle.netbeans.models.GradleBuildSettings;
import org.openide.util.Utilities;

public class NbGradleOperationCustomizer implements GradleOperationCustomizer {
    private final GradleBuildSettings buildSettings;

    public NbGradleOperationCustomizer(GradleBuildSettings buildSettings) {
        this.buildSettings = buildSettings;
    }

    @Override
    public void execute(LongRunningOperation operation) {
        String jvmOptions = buildSettings.getJvmOptions();
        if (jvmOptions != null) {
            String[] jvmOptionsArgs = Utilities.parseParameters(jvmOptions);
            operation.setJvmArguments(jvmOptionsArgs);
        }
    }

    @Override
    public void close() throws IOException {
    }
}
