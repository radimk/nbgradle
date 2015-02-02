package org.nbgradle.netbeans.project

import org.gradle.tooling.model.gradle.GradleBuild
import org.nbgradle.netbeans.models.GradleBuildSettings
import org.nbgradle.netbeans.models.ModelProvider
import org.junit.Rule
import org.nbgradle.netbeans.project.model.DefaultGradleBuildSettings
import org.nbgradle.test.fixtures.AbstractIntegrationSpec
import org.nbgradle.test.fixtures.Sample
import org.nbgradle.test.fixtures.UsesSample
import org.netbeans.api.project.ProjectManager
import org.openide.filesystems.FileUtil

import java.nio.file.Files

class NbGradleProjectIntegrationSpec extends AbstractIntegrationSpec {
    @Rule Sample sample = new Sample(temporaryFolder);

    @UsesSample("java/quickstart")
    def "simple project"() {
        GradleBuildSettings buildSettings = new DefaultGradleBuildSettings()

        when:
        GradleProjectImporter importer = new GradleProjectImporter()
        importer.importProject(buildSettings, sample.dir.toFile())
        def project = ProjectManager.getDefault().findProject(
                FileUtil.toFileObject(FileUtil.normalizeFile(sample.dir.toFile())));

        then:
        project != null
        def prjBuildSettings = project.lookup.lookup(GradleBuildSettings)
        prjBuildSettings != null
        project.lookup.lookup(ModelProvider) != null
    }

    def 'use VM args from build settings'() {
        GradleBuildSettings buildSettings = new DefaultGradleBuildSettings()
        buildSettings.jvmOptions = '-DprojectName=foo'
        def settings = temporaryFolder.testDirectory.resolve('settings.gradle')
        Files.write(settings,
                ['def prjName = System.getProperties().get(\'projectName\')',
                'rootProject.name = prjName != null ? prjName : \'dummy\''])

        when:
        GradleProjectImporter importer = new GradleProjectImporter()
        importer.importProject(buildSettings, temporaryFolder.testDirectory.toFile())
        def project = ProjectManager.getDefault().findProject(
                FileUtil.toFileObject(FileUtil.normalizeFile(temporaryFolder.testDirectory.toFile())));

        then:
        GradleBuild build = project.lookup.lookup(ModelProvider).getModel(GradleBuild).get()
        build.rootProject.name == 'foo'
    }
}