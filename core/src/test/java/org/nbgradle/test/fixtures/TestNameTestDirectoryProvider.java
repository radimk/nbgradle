package org.nbgradle.test.fixtures;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A JUnit rule which provides a unique temporary folder for the test.
 */
public class TestNameTestDirectoryProvider implements MethodRule, TestRule, TestDirectoryProvider {
    private Path dir;
    private String prefix;
    private static Path root;
    private static AtomicInteger testCounter = new AtomicInteger(1);

    static {
        // NOTE: the space in the directory name is intentional
        root = Paths.get("build/tmp/test files");
    }

    private String determinePrefix() {
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().endsWith("Test") || element.getClassName().endsWith("Spec")) {
                return Iterables.getLast(Splitter.on('.').split(element.getClassName())) + "/unknown-test-" + testCounter.getAndIncrement();
            }
        }
        return "unknown-test-class-" + testCounter.getAndIncrement();
    }

    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        init(method.getName(), target.getClass().getSimpleName());
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
                deleteRecursively(getTestDirectory());
                // Don't delete on failure
            }
        };
    }

    public Statement apply(final Statement base, Description description) {
        init(description.getMethodName(), description.getTestClass().getSimpleName());
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
                deleteRecursively(getTestDirectory());
                // Don't delete on failure
            }
        };
    }

    private void deleteRecursively(Path path) throws Throwable {
        if (!Files.isDirectory(path)) {
            Files.delete(path);
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path file: stream) {
                deleteRecursively(file);
            }
            Files.delete(path);
        } catch (IOException | DirectoryIteratorException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
            throw new RuntimeException(x);
        }

    }

    private void init(String methodName, String className) {
        if (methodName == null) {
            // must be a @ClassRule; use the rule's class name instead
            methodName = getClass().getSimpleName();
        }
        if (prefix == null) {
            String safeMethodName = methodName.replaceAll("\\s", "_").replace(File.pathSeparator, "_").replace(":", "_").replace('"', '_');
            if (safeMethodName.length() > 64) {
                safeMethodName = safeMethodName.substring(0, 32) + "..." + safeMethodName.substring(safeMethodName.length() - 32);
            }
            prefix = String.format("%s/%s", className, safeMethodName);
        }
    }

    public static TestNameTestDirectoryProvider newInstance() {
        return new TestNameTestDirectoryProvider();
    }

    public static TestNameTestDirectoryProvider newInstance(FrameworkMethod method, Object target) {
        TestNameTestDirectoryProvider testDirectoryProvider = new TestNameTestDirectoryProvider();
        testDirectoryProvider.init(method.getName(), target.getClass().getSimpleName());
        return testDirectoryProvider;
    }

    public Path getTestDirectory() {
        if (dir == null) {
            if (prefix == null) {
                // This can happen if this is used in a constructor or a @Before method. It also happens when using
                // @RunWith(SomeRunner) when the runner does not support rules.
                prefix = determinePrefix();
            }
            for (int counter = 1; true; counter++) {
                dir = root.resolve(counter == 1 ? prefix : String.format("%s%d", prefix, counter));
                try {
                    Files.createDirectories(dir);
                    break;
                } catch (IOException e) {
                    // ignore and try another
                }
            }
        }
        return dir;
    }
}