package org.nbgradle.netbeans.project

import com.google.common.io.ByteSink
import com.google.common.io.FileBackedOutputStream
import org.gradle.tooling.model.gradle.BasicGradleProject
import org.nbgradle.netbeans.project.model.DefaultDistributionSpec
import org.netbeans.api.project.Project
import org.netbeans.spi.project.ActionProvider
import org.openide.filesystems.FileObject
import spock.lang.Specification

class AbstractProjectSpec extends Specification {
    protected FileObject prjDir
    protected File projectDir

    def setup() {
        BasicGradleProject gradleProject = Mock(BasicGradleProject)
        _ * gradleProject.path >> ':'
        _ * gradleProject.name >> 'name'
        _ * gradleProject.children >> []
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
}