package org.nbgradle.netbeans.project

import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import org.nbgradle.netbeans.models.GradleRunner
import org.nbgradle.netbeans.models.ModelProvider
import org.gradle.tooling.model.DomainObjectSet
import org.gradle.tooling.model.Task
import org.gradle.tooling.model.gradle.BuildInvocations
import org.nbgradle.netbeans.project.lookup.GradleProjectInformation
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
        ModelProvider modelSupplier = Mock(ModelProvider)
        GradleRunner toolingRunner = Mock(GradleRunner)
        GradleProjectInformation info = Mock(GradleProjectInformation)
        _ * info.projectPath >> ':'
        def actionProvider = new GradleActionProvider(info, modelSupplier, toolingRunner)
        def emptyBuild = Mock(BuildInvocations)
        def aBuild = Mock(BuildInvocations)
        def tasks = Mock(DomainObjectSet)
        def buildTask = Mock(Task)
        ListenableFuture<BuildInvocations> modelFuture = Futures.immediateFuture(null)

        when:
        2 * modelSupplier.getModel(BuildInvocations) >> modelFuture

        then:
        !actionProvider.isActionEnabled(ActionProvider.COMMAND_BUILD, Lookup.EMPTY)
        !actionProvider.isActionEnabled(ActionProvider.COMMAND_CLEAN, Lookup.EMPTY)

        when:
        2 * modelSupplier.getModel(BuildInvocations) >> Futures.immediateFuture(emptyBuild)
        _ * emptyBuild.tasks >> tasks
        1 * tasks.iterator() >> [].iterator()

        then:
        !actionProvider.isActionEnabled(ActionProvider.COMMAND_BUILD, Lookup.EMPTY)
        !actionProvider.isActionEnabled(ActionProvider.COMMAND_CLEAN, Lookup.EMPTY)

        when:
        1 * modelSupplier.getModel(BuildInvocations) >> Futures.immediateFuture(aBuild)
        1 * aBuild.tasks >> tasks
        1 * tasks.iterator() >> [buildTask].iterator()
        _ * buildTask.path >> ":build"

        then:
        actionProvider.isActionEnabled(ActionProvider.COMMAND_BUILD, Lookup.EMPTY)
    }
}