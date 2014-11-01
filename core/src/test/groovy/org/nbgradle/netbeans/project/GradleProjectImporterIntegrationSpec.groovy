package org.nbgradle.netbeans.project

import com.gradleware.tooling.eclipse.core.models.DistributionSpec
import com.gradleware.tooling.eclipse.core.models.DistributionSpecs
import com.gradleware.tooling.eclipse.core.models.GradleBuildSettings
import org.junit.Rule
import org.nbgradle.netbeans.project.model.DistributionSettings
import org.nbgradle.netbeans.project.model.NbGradleBuildSettings
import org.nbgradle.netbeans.project.model.VersionDistributionSpec
import org.nbgradle.test.fixtures.AbstractIntegrationSpec
import org.nbgradle.test.fixtures.Sample
import org.nbgradle.test.fixtures.UsesSample

class GradleProjectImporterIntegrationSpec extends AbstractIntegrationSpec {
    @Rule Sample sample = new Sample(temporaryFolder);

    @UsesSample("java/quickstart")
    def "simple project"() {
        DistributionSpec distribution = DistributionSpecs.defaultDistribution()
        NbGradleBuildSettings buildSettings = Mock(NbGradleBuildSettings)
        _ * buildSettings.distributionSpec >> distribution
        _ * buildSettings.gradleUserHomeDir >> null

        when:
        GradleProjectImporter importer = new GradleProjectImporter()
        importer.importProject(buildSettings, sample.dir.toFile())
        def buildXml = parseFile('dirPath': sample.dir, 'print': true, NbGradleConstants.NBGRADLE_BUILD_XML)

        then:
        buildXml != null
        buildXml.rootProject.name == 'quickstart'
        buildXml.rootProject.path == ':'
        buildXml.rootProject.projectDirectory == sample.dir.toFile().canonicalFile
    }

    @UsesSample("java/quickstart")
    def "simple project imported using gradle version"() {
        DistributionSettings distroSettings = new VersionDistributionSpec()
        distroSettings.value = '1.11'
        DistributionSpec distribution = DistributionSpecs.versionDistribution('1.11')
        NbGradleBuildSettings buildSettings = Mock(NbGradleBuildSettings)
        _ * buildSettings.distributionSettings >> distroSettings
        _ * buildSettings.distributionSpec >> distribution
        _ * buildSettings.gradleUserHomeDir >> null

        when:
        GradleProjectImporter importer = new GradleProjectImporter()
        importer.importProject(buildSettings, sample.dir.toFile())
        def buildXml = parseFile('dirPath': sample.dir, 'print': true, NbGradleConstants.NBGRADLE_BUILD_XML)

        then:
        buildXml != null
        buildXml.distribution.value == '1.11'
        buildXml.rootProject.name == 'quickstart'
        buildXml.rootProject.path == ':'
        buildXml.rootProject.projectDirectory == sample.dir.toFile().canonicalFile
    }
}