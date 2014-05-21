package org.nbgradle.netbeans.project

import org.junit.Rule
import org.nbgradle.netbeans.project.model.DefaultDistributionSpec
import org.nbgradle.netbeans.project.model.DistributionSpec
import org.nbgradle.netbeans.project.model.GradleBuildSettings
import org.nbgradle.netbeans.project.model.VersionDistributionSpec
import org.nbgradle.test.fixtures.AbstractIntegrationSpec
import org.nbgradle.test.fixtures.Sample
import org.nbgradle.test.fixtures.UsesSample

class GradleProjectImporterIntegrationSpec extends AbstractIntegrationSpec {
    @Rule Sample sample = new Sample(temporaryFolder);

    @UsesSample("java/quickstart")
    def "simple project"() {
        DistributionSpec distribution = new DefaultDistributionSpec()
        GradleBuildSettings buildSettings = Mock(GradleBuildSettings)
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
        DistributionSpec distribution = new VersionDistributionSpec()
        distribution.version = '1.11'
        GradleBuildSettings buildSettings = Mock(GradleBuildSettings)
        _ * buildSettings.distributionSpec >> distribution
        _ * buildSettings.gradleUserHomeDir >> null

        when:
        GradleProjectImporter importer = new GradleProjectImporter()
        importer.importProject(buildSettings, sample.dir.toFile())
        def buildXml = parseFile('dirPath': sample.dir, 'print': true, NbGradleConstants.NBGRADLE_BUILD_XML)

        then:
        buildXml != null
        buildXml.distribution.version == '1.11'
        buildXml.rootProject.name == 'quickstart'
        buildXml.rootProject.path == ':'
        buildXml.rootProject.projectDirectory == sample.dir.toFile().canonicalFile
    }
}