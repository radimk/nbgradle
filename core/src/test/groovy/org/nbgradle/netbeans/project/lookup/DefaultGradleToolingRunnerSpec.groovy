package org.nbgradle.netbeans.project.lookup

import com.gradleware.tooling.eclipse.core.models.GradleBuildSettings
import org.gradle.tooling.model.gradle.GradleBuild
import spock.lang.Specification

class DefaultGradleToolingRunnerSpec extends Specification {
    def 'first get initiates loading'() {
        def projectDir = Mock(File)
        def buildSettings = Mock(GradleBuildSettings)
        def buildRunner = new DefaultGradleToolingRunner(buildSettings, projectDir)

        when:
        def build = buildRunner.newBuild()

        then:
        build != null
    }
}
