package org.nbgradle.netbeans.project

import org.netbeans.api.project.Project
import org.netbeans.spi.project.SubprojectProvider
import org.netbeans.spi.project.ui.CustomizerProvider
import org.netbeans.spi.project.ui.LogicalViewProvider

class NbGradleProjectSpec extends AbstractProjectSpec {

    /*
The following abilities are recommended:

Sources
AuxiliaryConfiguration
AuxiliaryProperties
CacheDirectoryProvider
You might also have e.g.:

ProjectConfigurationProvider
FileBuiltQueryImplementation
SharabilityQueryImplementation
FileEncodingQueryImplementation
ProjectOpenedHook
RecommendedTemplates
PrivilegedTemplates
ClassPathProvider
SourceForBinaryQueryImplementation
SourceLevelQueryImplementation2
JavadocForBinaryQueryImplementation
AccessibilityQueryImplementation
MultipleRootsUnitTestForSourceQueryImplementation
ProjectXmlSavedHook
SearchInfo
BinaryForSourceQueryImplementation
CreateFromTemplateAttributesProvider
     */
    def 'ProjectInformation'() {
        when:
        Project prj = new NbGradleProject(contextProvider(), prjDir, projectDir)
        def pi = prj.lookup.lookup(org.netbeans.api.project.ProjectInformation)

        then:
        pi != null
        pi.project == prj
        pi.name == 'name'
    }

    def 'logical view provider'() {
        when:
        Project prj = new NbGradleProject(contextProvider(), prjDir, projectDir)
        def viewProvider = prj.lookup.lookup(LogicalViewProvider)

        then:
        viewProvider != null

        when:
        def node = viewProvider.createLogicalView()

        then:
        node != null
    }

    def 'project customizes provider'() {
        when:
        Project prj = new NbGradleProject(contextProvider(), prjDir, projectDir)
        def provider = prj.lookup.lookup(CustomizerProvider)

        then:
        provider != null
    }

    def 'subproject provider'() {
        when:
        Project prj = new NbGradleProject(contextProvider(), prjDir, projectDir)
        def provider = prj.lookup.lookup(SubprojectProvider)

        then:
        provider != null
    }
}