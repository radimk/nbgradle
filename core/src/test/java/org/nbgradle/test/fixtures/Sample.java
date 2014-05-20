package org.nbgradle.test.fixtures;

import org.junit.Assert;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.EnumSet;

/**
 * A Junit rule which copies a sample into the test directory before the test executes. Looks for a
 * {@link org.nbgradle.test.fixtures.UsesSample} annotation on the test method to determine which sample the
 * test requires. If not found, uses the default sample provided in the constructor.
 */
public class Sample implements MethodRule {
    private final Logger logger = LoggerFactory.getLogger(Sample.class);
    private final String defaultSampleName;
    private final String testSampleDirName;

    private Path sampleDir;
    private TestDirectoryProvider testDirectoryProvider;

    public Sample(TestDirectoryProvider testDirectoryProvider) {
        this(testDirectoryProvider, null);
    }

    public Sample(TestDirectoryProvider testDirectoryProvider, String defaultSampleName) {
        this(testDirectoryProvider, defaultSampleName, null);
    }

    public Sample(TestDirectoryProvider testDirectoryProvider, String defaultSampleName, String testSampleDirName) {
        this.testDirectoryProvider = testDirectoryProvider;
        this.defaultSampleName = defaultSampleName;
        this.testSampleDirName = testSampleDirName;
    }

    public Statement apply(final Statement base, FrameworkMethod method, Object target) {
        final String sampleName = getSampleName(method);
        if (testSampleDirName != null) {
            sampleDir = testDirectoryProvider.getTestDirectory().resolve(testSampleDirName);
        } else {
            sampleDir = sampleName == null ? null : testDirectoryProvider.getTestDirectory().resolve(sampleName);
        }

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (sampleName != null) {
                    Path srcDir = new IntegrationTestBuildContext().getSamplesDir().resolve(sampleName);
                    Assert.assertTrue("path exists: " + srcDir, Files.isDirectory(srcDir));
                    logger.debug("Copying sample '{}' to test directory.", sampleName);
                    TreeCopier tc = new TreeCopier(srcDir, sampleDir, true);
                    Files.createDirectories(sampleDir);
                    Files.walkFileTree(srcDir, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, tc);
                } else {
                    logger.debug("No sample specified for this test, skipping.");
                }
                base.evaluate();
            }
        };
    }

    private String getSampleName(FrameworkMethod method) {
        String sampleName;
        UsesSample annotation = method.getAnnotation(UsesSample.class);
        if (annotation == null) {
            sampleName = defaultSampleName;
        } else {
            sampleName = annotation.value();
        }
        return sampleName;
    }

    public Path getDir() {
        return sampleDir;
    }

    /**
     * Copy source file to target location. If {@code prompt} is true then
     * prompt user to overwrite target if it exists. The {@code preserve}
     * parameter determines if file attributes should be copied/preserved.
     */
    private static void copyFile(Path source, Path target, boolean preserve) throws IOException {
        CopyOption[] options = (preserve) ?
                new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING } :
                new CopyOption[] { StandardCopyOption.REPLACE_EXISTING };
        Files.copy(source, target, options);
    }

    /**
     * A {@code FileVisitor} that copies a file-tree ("cp -r")
     */
    static class TreeCopier implements FileVisitor<Path> {
        private final Path source;
        private final Path target;
        private final boolean preserve;

        TreeCopier(Path source, Path target, boolean preserve) {
            this.source = source;
            this.target = target;
            this.preserve = preserve;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            // before visiting entries in a directory we copy the directory
            // (okay if directory already exists).
            CopyOption[] options = (preserve) ?
                    new CopyOption[] {StandardCopyOption.COPY_ATTRIBUTES } : new CopyOption[0];

            Path newdir = target.resolve(source.relativize(dir));
            try {
                Files.copy(dir, newdir, options);
            } catch (FileAlreadyExistsException x) {
                // ignore
            } catch (IOException x) {
                System.err.format("Unable to create: %s: %s%n", newdir, x);
                return FileVisitResult.SKIP_SUBTREE;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            copyFile(file, target.resolve(source.relativize(file)), preserve);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            // fix up modification time of directory when done
            if (exc == null && preserve) {
                Path newdir = target.resolve(source.relativize(dir));
                try {
                    FileTime time = Files.getLastModifiedTime(dir);
                    Files.setLastModifiedTime(newdir, time);
                } catch (IOException x) {
                    System.err.format("Unable to copy all attributes to: %s: %s%n", newdir, x);
                }
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            if (exc instanceof FileSystemLoopException) {
                System.err.println("cycle detected: " + file);
            } else {
                System.err.format("Unable to copy: %s: %s%n", file, exc);
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
