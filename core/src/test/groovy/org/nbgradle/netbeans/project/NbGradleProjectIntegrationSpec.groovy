package org.nbgradle.netbeans.project

import org.nbgradle.netbeans.models.GradleBuildSettings
import org.nbgradle.netbeans.models.ModelProvider
import org.junit.Rule
import org.nbgradle.netbeans.project.model.DefaultGradleBuildSettings
import org.nbgradle.test.fixtures.AbstractIntegrationSpec
import org.nbgradle.test.fixtures.Sample
import org.nbgradle.test.fixtures.UsesSample
import org.netbeans.api.project.ProjectManager
import org.openide.filesystems.FileUtil

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
}