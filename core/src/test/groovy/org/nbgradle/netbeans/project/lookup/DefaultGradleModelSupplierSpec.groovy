package org.nbgradle.netbeans.project.lookup

import org.gradle.tooling.model.gradle.GradleBuild
import spock.lang.Specification

class DefaultGradleModelSupplierSpec extends Specification {
    def 'first get initiates loading'() {
        def modelLoader = Mock(GradleModelLoader)
        def modelSupplier = new DefaultGradleModelSupplier(modelLoader)
        def buildModel = Mock(GradleBuild)

        when:
        def build = modelSupplier.getModel(GradleBuild)
        def loadedBuild = modelSupplier.loadModel(GradleBuild)
        def loadedBuild2 = modelSupplier.getModel(GradleBuild)

        then:
        1 * modelLoader.getModel(GradleBuild) >> buildModel
        build == null
        loadedBuild == buildModel
        loadedBuild2 == buildModel

    }
}
