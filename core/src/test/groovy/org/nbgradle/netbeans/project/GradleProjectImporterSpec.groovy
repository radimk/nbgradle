package org.nbgradle.netbeans.project

import com.google.common.io.ByteSink
import com.google.common.io.FileBackedOutputStream
import org.gradle.tooling.model.DomainObjectSet
import org.gradle.tooling.model.gradle.BasicGradleProject
import org.nbgradle.netbeans.project.model.DefaultDistributionSpec
import org.nbgradle.netbeans.project.model.DefaultGradleBuildSettings
import org.nbgradle.netbeans.project.model.NbGradleBuildSettings
import spock.lang.Specification

class GradleProjectImporterSpec extends Specification {

    def "build settings persistence"() {
        NbGradleBuildSettings buildSettings = new DefaultGradleBuildSettings()
        buildSettings.distributionSettings = new DefaultDistributionSpec()
        def userHomeDir = new File('/gradle/user/home/dir')
        buildSettings.gradleUserHomeDir = userHomeDir
        def project = Mock(BasicGradleProject)
        _ * project.path >> ':'
        _ * project.name >> 'name'
        DomainObjectSet children = Mock(DomainObjectSet)
        _ * project.children >> children
        _ * children.all >> []
        def fbos = new FileBackedOutputStream(2048)
        def byteSink = new ByteSink() {
            @Override
            OutputStream openStream() throws IOException {
                return fbos;
            }
        }


        when:
        GradleProjectImporter importer = new GradleProjectImporter()
        importer.writeProjectSettings(project, buildSettings, byteSink)
        def importedData = importer.readBuildSettings(fbos.asByteSource())

        then:
        importedData.buildSettings.jvmOptions == buildSettings.jvmOptions
        importedData.buildSettings.gradleUserHomeDir == buildSettings.gradleUserHomeDir
    }
}