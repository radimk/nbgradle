package org.nbgradle.netbeans.project;

import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.LookupProvider;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.util.Lookup;
import org.openide.util.Parameters;

public class GradleActionProvider implements ActionProvider {
    @ProjectServiceProvider(
            service=ActionProvider.class,
            projectTypes={@LookupProvider.Registration.ProjectType(id=NbGradleConstants.PROJECT_TYPE, position=1000)})
    public static ActionProvider create(@NonNull final Lookup lkp) {
        Parameters.notNull("lkp", lkp);
        return new GradleActionProvider();
    }

    @Override
    public String[] getSupportedActions() {
        return new String[] {
                COMMAND_BUILD,
                COMMAND_CLEAN
//                COMMAND_REBUILD,
//                COMMAND_RUN,
//                COMMAND_DEBUG,
//        JavaProjectConstants.COMMAND_JAVADOC,
//      COMMAND_TEST,
//                COMMAND_DELETE,
//                COMMAND_COPY,
//                COMMAND_MOVE,
//                COMMAND_RENAME,
        };
    }

    @Override
    public void invokeAction(String command, Lookup context) throws IllegalArgumentException {

    }

    @Override
    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        return false;
    }
}
