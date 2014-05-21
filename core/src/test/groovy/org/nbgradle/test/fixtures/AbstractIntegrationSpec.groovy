package org.nbgradle.test.fixtures

import org.junit.Rule
import spock.lang.Specification

import java.nio.file.Path

abstract class AbstractIntegrationSpec extends Specification {
    @Rule TestNameTestDirectoryProvider temporaryFolder = new TestNameTestDirectoryProvider();

    protected File getFile(Map options, String filename) {
        def path = options?.dirPath ? options.dirPath.resolve(filename) : temporaryFolder.testDirectory.resolve(filename)
        if (options?.print) { println path.toFile().text }
        path.toFile()
    }

    protected parseFile(Map options = [:], String filename) {
        def file = getFile(options, filename)
        new XmlSlurper().parse(file)
    }
}
