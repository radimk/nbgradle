package org.nbgradle.netbeans.java;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.nbgradle.test.fixtures.Sample;
import org.nbgradle.test.fixtures.TestNameTestDirectoryProvider;
import org.nbgradle.test.fixtures.UsesSample;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.ClassPathProvider;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

public class GradleProjectClasspathProviderTest {
    @Rule public final TestNameTestDirectoryProvider temporaryFolder = new TestNameTestDirectoryProvider();
    @Rule public Sample sample = new Sample(temporaryFolder);

    @Test
    @UsesSample("java/quickstart")
    public void quickstart() throws Exception {
        Project project = new GradleProjectFixture(sample.getDir().toFile()).importAndFindRootProject();

        assertNotNull(project.getLookup().lookup(ClassPathProvider.class));

        FileObject foSrcMainJava = project.getProjectDirectory().getFileObject("src/main/java");
        FileObject foSrcTestJava = project.getProjectDirectory().getFileObject("src/test/java");
        ClassPath mainCp = ClassPath.getClassPath(foSrcMainJava, ClassPath.SOURCE);
        assertThat(mainCp.getRoots()).containsOnlyOnce(foSrcMainJava);
        ClassPath mainBootCp = ClassPath.getClassPath(foSrcMainJava, ClassPath.BOOT);
        // TODO boot CP according to selected platform
        assertThat(mainBootCp).isNotNull();
        ClassPath mainCompileCp = ClassPath.getClassPath(foSrcMainJava, ClassPath.COMPILE);
        assertThat(toArchiveRoots(mainCompileCp)).extracting("nameExt").containsOnlyOnce("commons-collections-3.2.jar");
        // assertThat(mainCompileCp.getRoots()).extracting("nameExt").containsOnlyOnce("commons-collections-3.2.jar");

        // source under the root get the same classpath
        FileObject foJava = project.getProjectDirectory().getFileObject("src/main/java/org/gradle/Person.java");
        assertThat(ClassPath.getClassPath(foJava, ClassPath.SOURCE)).isSameAs(mainCp);
        assertThat(ClassPath.getClassPath(foJava, ClassPath.BOOT)).isSameAs(mainBootCp);
        assertThat(ClassPath.getClassPath(foJava, ClassPath.COMPILE)).isSameAs(mainCompileCp);

        ClassPath testCp = ClassPath.getClassPath(foSrcTestJava, ClassPath.SOURCE);
        assertThat(testCp.getRoots()).containsOnlyOnce(foSrcTestJava);
        ClassPath testCompileCp = ClassPath.getClassPath(foSrcTestJava, ClassPath.COMPILE);
        assertThat(toArchiveRoots(testCompileCp)).extracting("nameExt").containsOnlyOnce("commons-collections-3.2.jar", "junit-4.12.jar", "hamcrest-core-1.3.jar");
        // TODO output from main compile
    }

    @Test
    @UsesSample("java/multiproject")
    public void multiproject() throws Exception {
        final GradleProjectFixture prjFixture = new GradleProjectFixture(sample.getDir().toFile());
        Project project = prjFixture.importAndFindRootProject();
        Project apiProject = prjFixture.findSubProject("api");
        Project sharedProject = prjFixture.findSubProject("shared");
        prjFixture.build("build");

        FileObject foSharedSrcTestJava = sharedProject.getProjectDirectory().getFileObject("src/test/java");
        ClassPath sharedTestCompileCp = ClassPath.getClassPath(foSharedSrcTestJava, ClassPath.COMPILE);
        assertThat(toArchiveRoots(sharedTestCompileCp)).extracting("nameExt").containsOnlyOnce("junit-4.11.jar", "hamcrest-core-1.3.jar");
        assertThat(toDirectoryRoots(sharedTestCompileCp)).containsOnlyOnce(
                sharedProject.getProjectDirectory().getFileObject("build/classes/main"),
                sharedProject.getProjectDirectory().getFileObject("build/resources/main")
        );

        FileObject foApiSrcMainJava = apiProject.getProjectDirectory().getFileObject("src/main/java");
        ClassPath apiMainCompileCp = ClassPath.getClassPath(foApiSrcMainJava, ClassPath.COMPILE);
        assertThat(apiMainCompileCp.findResource("org/gradle/shared/Person.class")).isNotNull();
        assertThat(apiMainCompileCp.findResource("org/gradle/shared/PersonTest.class")).isNull();
        assertThat(apiMainCompileCp.findResource("org/apache/commons/lang/builder/ToStringBuilder.class")).isNotNull();
    }

    private static Iterable<FileObject> toDirectoryRoots(ClassPath cpRoots) {
        return Iterables.filter(
                Lists.newArrayList(cpRoots.getRoots()),
                new Predicate<FileObject>() {
                    @Override public boolean apply(FileObject input) {
                        return input.isFolder();
                    }
                });
    }

    private static Iterable<FileObject> toArchiveRoots(ClassPath cpRoots) {
        return Iterables.filter(Iterables.transform(
                Lists.newArrayList(cpRoots.getRoots()),
                new Function<FileObject, FileObject>() {
                    @Override public FileObject apply(FileObject input) {
                        return FileUtil.getArchiveFile(input);
                    }
                }),
                Predicates.notNull());
    }
}
