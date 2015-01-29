package org.nbgradle.netbeans.java;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.nbgradle.netbeans.project.model.DefaultGradleBuildSettings;
import org.nbgradle.test.fixtures.Sample;
import org.nbgradle.test.fixtures.TestNameTestDirectoryProvider;
import org.nbgradle.test.fixtures.UsesSample;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import java.io.IOException;
import org.junit.Ignore;
import org.nbgradle.netbeans.project.GradleProjectImporter;
import org.nbgradle.netbeans.project.lookup.ProjectLoadingHook;
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
        Project project = new GradleProjectFixture(sample.getDir().toFile()).importAndFindProject();

        assertNotNull(project.getLookup().lookup(ClassPathProvider.class));
        project.getLookup().lookup(ProjectLoadingHook.class).projectOpened();
        project.getLookup().lookup(ProjectLoadingHook.class).phaser.arriveAndAwaitAdvance();

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
    @Ignore
    @UsesSample("java/multiproject")
    public void multiproject() throws Exception {
        Project project = new GradleProjectFixture(sample.getDir().toFile()).importAndFindProject();
        Project apiProject = new GradleProjectFixture(sample.getDir().resolve("api").toFile()).importAndFindProject();
        Project sharedProject = new GradleProjectFixture(sample.getDir().resolve("shared").toFile()).importAndFindProject();

        project.getLookup().lookup(ProjectLoadingHook.class).projectOpened();
        project.getLookup().lookup(ProjectLoadingHook.class).phaser.arriveAndAwaitAdvance();

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
