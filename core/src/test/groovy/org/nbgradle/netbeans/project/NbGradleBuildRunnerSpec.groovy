package org.nbgradle.netbeans.project

import com.gradleware.tooling.eclipse.core.models.GradleRunner
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.ProgressEvent
import org.netbeans.api.progress.ProgressHandle

class NbGradleBuildRunnerSpec extends AbstractProjectSpec {

    def 'runs the build with progress'() {
        def gradleRunner = Mock(GradleRunner)
        def runner = new NbGradleBuildRunner(gradleRunner)
        def runnerSpec = Mock(GradleLaunchSpec)
        def launcher = Mock(BuildLauncher)

        def progress = Mock(BuildProgressMonitor)

        _ * runnerSpec.taskNames >> ['build']
        1 * gradleRunner.newBuild() >> launcher
        _ * launcher.forTasks(_) >> launcher
        1 * launcher.run(_) >> { args -> args[0].onComplete(null) }

        when:
        runner.execute(runnerSpec)

        then:
        1 * runnerSpec.createProgressMonitor() >> progress
        1 * progress.start()
        1 * progress.finish()
    }

    def 'runs failing build with progress'() {
        def gradleRunner = Mock(GradleRunner)
        def runner = new NbGradleBuildRunner(gradleRunner)
        def runnerSpec = Mock(GradleLaunchSpec)
        def launcher = Mock(BuildLauncher)

        def progress = Mock(BuildProgressMonitor)

        _ * runnerSpec.taskNames >> ['build']
        1 * gradleRunner.newBuild() >> launcher
        _ * launcher.forTasks(_) >> launcher
        1 * launcher.run(_) >> { args -> args[0].onFailure(null) }

        when:
        runner.execute(runnerSpec)

        then:
        1 * runnerSpec.createProgressMonitor() >> progress
        1 * progress.start()
        1 * progress.finish()
    }

    def 'relays progress from build'() {
        def gradleRunner = Mock(GradleRunner)
        def runner = new NbGradleBuildRunner(gradleRunner)
        def runnerSpec = Mock(GradleLaunchSpec)
        def launcher = Mock(BuildLauncher)

        def progress = Mock(BuildProgressMonitor)

        _ * runnerSpec.taskNames >> ['build']
        1 * gradleRunner.newBuild() >> launcher
        _ * launcher.forTasks(_) >> launcher
        1 * launcher.run(_) >> { args -> args[0].onFailure(null) }

        when:
        runner.execute(runnerSpec)

        then:
        // TODO want to capture listener and fire progress
        1 * runnerSpec.createProgressMonitor() >> progress
        1 * launcher.addProgressListener(_) >> launcher
        1 * progress.start()
        1 * progress.finish()
    }
}