package org.nbgradle.netbeans.project

import org.netbeans.api.project.Project
import org.netbeans.api.project.ProjectInformation
import org.openide.filesystems.FileObject
import spock.lang.Specification

class NbGradleProjectSpec extends AbstractProjectSpec {

    /*
The following abilities are recommended:

ProjectInformation
LogicalViewProvider
CustomizerProvider
Sources
ActionProvider
SubprojectProvider
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
        Project prj = new NbGradleProject(prjDir)
        def pi = prj.lookup.lookup(org.netbeans.api.project.ProjectInformation)

        then:
        pi != null
        pi.project == prj
        pi.name == 'name'
    }
}