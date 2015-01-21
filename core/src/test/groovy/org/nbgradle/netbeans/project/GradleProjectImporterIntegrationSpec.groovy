package org.nbgradle.netbeans.project

import org.nbgradle.netbeans.models.DistributionSpec
import org.nbgradle.netbeans.models.DistributionSpecs
import org.nbgradle.netbeans.models.GradleBuildSettings
import org.junit.Rule
import org.nbgradle.netbeans.project.model.DistributionSettings
import org.nbgradle.netbeans.project.model.NbGradleBuildSettings
import org.nbgradle.netbeans.project.model.NbGradleProjectJAXB
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

    @UsesSample("java/multiproject")
    def "multiproject"() {
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
        buildXml.rootProject.name == 'multiproject'
        buildXml.rootProject.path == ':'
        buildXml.rootProject.projectDirectory == sample.dir.toFile().canonicalFile

        when:
        def subprojects = buildXml.rootProject.childProjects

        then:
        subprojects != null
        subprojects.size() == 3
        subprojects.find { it.path == ':api' } != null
        subprojects.find { it.path == ':shared' } != null
        subprojects.find { it.path == ':services' } != null

    }
}