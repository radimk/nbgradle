package org.nbgradle.netbeans.project

import org.gradle.tooling.model.DomainObjectSet
import org.gradle.tooling.model.Task
import org.gradle.tooling.model.gradle.BuildInvocations
import org.nbgradle.netbeans.project.lookup.GradleModelSupplier
import org.netbeans.api.project.Project
import org.netbeans.spi.project.ActionProvider
import org.openide.util.Lookup

class GradleActionProviderSpec extends AbstractProjectSpec {

    def 'in lookup'() {
        when:
        Project prj = new NbGradleProject(prjDir, projectDir)
        def actionProvider = prj.lookup.lookup(ActionProvider)

        then:
        actionProvider != null
        Arrays.asList(actionProvider.supportedActions).contains(ActionProvider.COMMAND_BUILD)
        Arrays.asList(actionProvider.supportedActions).contains(ActionProvider.COMMAND_CLEAN)
    }

    def 'enabling'() {
        GradleModelSupplier modelSupplier = Mock(GradleModelSupplier)
        def actionProvider = new GradleActionProvider(":", modelSupplier)
        def emptyBuild = Mock(BuildInvocations)
        def aBuild = Mock(BuildInvocations)
        def tasks = Mock(DomainObjectSet)
        def buildTask = Mock(Task)

        when:
        2 * modelSupplier.getModel(BuildInvocations) >> null

        then:
        !actionProvider.isActionEnabled(ActionProvider.COMMAND_BUILD, Lookup.EMPTY)
        !actionProvider.isActionEnabled(ActionProvider.COMMAND_CLEAN, Lookup.EMPTY)

        when:
        2 * modelSupplier.getModel(BuildInvocations) >> emptyBuild
        _ * emptyBuild.tasks >> tasks
        1 * tasks.iterator() >> [].iterator()

        then:
        !actionProvider.isActionEnabled(ActionProvider.COMMAND_BUILD, Lookup.EMPTY)
        !actionProvider.isActionEnabled(ActionProvider.COMMAND_CLEAN, Lookup.EMPTY)

        when:
        1 * modelSupplier.getModel(BuildInvocations) >> aBuild
        1 * aBuild.tasks >> tasks
        1 * tasks.iterator() >> [buildTask].iterator()
        _ * buildTask.path >> ":build"

        then:
        actionProvider.isActionEnabled(ActionProvider.COMMAND_BUILD, Lookup.EMPTY)
    }
}