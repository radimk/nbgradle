package org.nbgradle.netbeans.project

import com.google.common.io.ByteSink
import com.google.common.io.FileBackedOutputStream
import org.gradle.tooling.model.DomainObjectSet
import org.gradle.tooling.model.gradle.BasicGradleProject
import org.nbgradle.netbeans.models.GradleBuildSettings
import org.nbgradle.netbeans.models.GradleContext
import org.nbgradle.netbeans.models.GradleRunner
import org.nbgradle.netbeans.models.ModelProvider
import org.nbgradle.netbeans.project.lookup.ProjectInfoNode
import org.nbgradle.netbeans.project.model.DefaultDistributionSpec
import org.openide.filesystems.FileObject
import spock.lang.Specification

class AbstractProjectSpec extends Specification {
    protected FileObject prjDir
    protected File projectDir

    def setup() {
        BasicGradleProject gradleProject = Mock(BasicGradleProject)
        DomainObjectSet children = Mock(DomainObjectSet)
        _ * gradleProject.path >> ':'
        _ * gradleProject.name >> 'name'
        _ * gradleProject.children >> children
        _ * children.all >> []
        def fbos = new FileBackedOutputStream(2048)
        new GradleProjectImporter().writeProjectSettings(gradleProject, new DefaultDistributionSpec(), new ByteSink() {
            @Override
            OutputStream openStream() throws IOException {
                return fbos;
            }
        })
        prjDir = Mock(FileObject)
        FileObject settingsXml = Mock(FileObject)
        _ * prjDir.nameExt >> 'name'
        _ * prjDir.getFileObject(NbGradleConstants.NBGRADLE_BUILD_XML) >> settingsXml
        _ * settingsXml.inputStream >> fbos.asByteSource().openStream()

        projectDir = Mock(File)
    }

    def gradleContext() {
        GradleContext context = Mock(GradleContext)
        _ * context.buildSettings >> Mock(GradleBuildSettings)
        _ * context.projectTreeInformation >> Mock(ProjectInfoNode)
        _ * context.runner >> Mock(GradleRunner)
        _ * context.modelProvider >> Mock(ModelProvider)

        context
    }

    def currentProjectNode() {
        ProjectInfoNode prjNode = Mock(ProjectInfoNode)
        _ * prjNode.path >> ':'
        _ * prjNode.projectDirectory >> projectDir
        _ * prjNode.name >> 'testProject'
        _ * prjNode.childProjects >> []
        prjNode
    }
}