package org.nbgradle.netbeans.project

import org.nbgradle.netbeans.models.GradleRunner
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.CancellationToken
import org.gradle.tooling.ProgressEvent
import org.netbeans.api.progress.ProgressHandle

class NbGradleBuildRunnerSpec extends AbstractProjectSpec {

    def gradleRunner
    def runner
    def runnerSpec
    def launcher
    def progress
    def streams

    def setup() {
        gradleRunner = Mock(GradleRunner)
        runner = new NbGradleBuildRunner(gradleRunner)
        runnerSpec = Mock(GradleLaunchSpec)
        launcher = Mock(BuildLauncher)
        progress = Mock(BuildProgressMonitor)
        streams = Mock(StandardStreams)

        _ * runnerSpec.standardStreams >> streams
        1 * gradleRunner.newBuild() >> launcher
        _ * launcher.forTasks(_) >> launcher
    }

    def 'runs the build with progress'() {

        _ * runnerSpec.taskNames >> ['build']
        1 * launcher.run(_) >> { args -> args[0].onComplete(null) }

        when:
        runner.execute(runnerSpec)

        then:
        1 * runnerSpec.createProgressMonitor() >> progress
        1 * progress.start()
        1 * progress.finish()
    }

    def 'runs failing build with progress'() {
        _ * runnerSpec.taskNames >> ['build']
        1 * launcher.run(_) >> { args -> args[0].onFailure(null) }

        when:
        runner.execute(runnerSpec)

        then:
        1 * runnerSpec.createProgressMonitor() >> progress
        1 * progress.start()
        1 * progress.finish()
    }

    def 'relays progress from build'() {
        _ * runnerSpec.taskNames >> ['build']
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

    def 'uses cancel token for build'() {
        def cancel = Mock(CancellationToken)
        _ * runnerSpec.taskNames >> ['build']
        1 * launcher.run(_) >> { args -> args[0].onFailure(null) }

        when:
        runner.execute(runnerSpec)

        then:
        // TODO want to capture listener and fire progress
        1 * runnerSpec.cancellationToken >> cancel
        1 * runnerSpec.createProgressMonitor() >> progress
        1 * launcher.addProgressListener(_) >> launcher
        1 * launcher.withCancellationToken(cancel) >> launcher
        1 * progress.start()
        1 * progress.finish()
    }
}