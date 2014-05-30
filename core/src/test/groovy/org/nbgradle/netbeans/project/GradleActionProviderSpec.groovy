package org.nbgradle.netbeans.project

import org.netbeans.api.project.Project
import org.netbeans.spi.project.ActionProvider

class GradleActionProviderSpec extends AbstractProjectSpec {

    def 'in lookup'() {
        when:
        Project prj = new NbGradleProject(prjDir)
        def actionProvider = prj.lookup.lookup(ActionProvider)

        then:
        actionProvider != null
        Arrays.asList(actionProvider.supportedActions).contains(ActionProvider.COMMAND_BUILD)
        Arrays.asList(actionProvider.supportedActions).contains(ActionProvider.COMMAND_CLEAN)
    }
}