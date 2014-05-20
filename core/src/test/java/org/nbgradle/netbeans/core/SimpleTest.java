package org.nbgradle.netbeans.core;

import org.junit.Rule;
import org.junit.Test;
import org.nbgradle.test.fixtures.Sample;
import org.nbgradle.test.fixtures.TestNameTestDirectoryProvider;
import org.nbgradle.test.fixtures.UsesSample;

import java.nio.file.Files;

import static org.junit.Assert.*;

public class SimpleTest {
    @Rule public final TestNameTestDirectoryProvider temporaryFolder = new TestNameTestDirectoryProvider();
    @Rule public Sample sample = new Sample(temporaryFolder);

    @Test
    @UsesSample("java/quickstart")
    public void foo() {
        assertTrue(Files.isDirectory(temporaryFolder.getTestDirectory()));
        fail("todo");
    }
}
