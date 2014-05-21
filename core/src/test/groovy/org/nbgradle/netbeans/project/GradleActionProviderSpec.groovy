package org.nbgradle.netbeans.project

import org.netbeans.api.project.Project
import org.netbeans.spi.project.ActionProvider
import org.openide.filesystems.FileObject
import spock.lang.Specification

class GradleActionProviderSpec extends Specification {

    def 'in lookup'() {
        FileObject prjDir = Mock(FileObject)
        _ * prjDir.nameExt >> 'name'

        when:
        Project prj = new NbGradleProject(prjDir)
        def actionProvider = prj.lookup.lookup(ActionProvider)

        then:
        actionProvider != null
        Arrays.asList(actionProvider.supportedActions).contains(ActionProvider.COMMAND_BUILD)
        Arrays.asList(actionProvider.supportedActions).contains(ActionProvider.COMMAND_CLEAN)
    }
}