package org.nbgradle.netbeans.java;

import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.nbgradle.test.fixtures.Sample;
import org.nbgradle.test.fixtures.TestNameTestDirectoryProvider;
import org.nbgradle.test.fixtures.UsesSample;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.netbeans.api.java.queries.SourceForBinaryQuery;

import static org.assertj.core.api.Assertions.*;

public class GradleSourceForCompiledClassesTest {
    @Rule public final TestNameTestDirectoryProvider temporaryFolder = new TestNameTestDirectoryProvider();
    @Rule public Sample sample = new Sample(temporaryFolder);

    @Test
    @UsesSample("java/quickstart")
    public void quickstart() throws Exception {
        Project project = new GradleProjectFixture(sample.getDir().toFile()).importAndFindRootProject();

        FileObject foSrcMainJava = project.getProjectDirectory().getFileObject("src/main/java");
        FileObject foSrcTestJava = project.getProjectDirectory().getFileObject("src/test/java");
        File classesMainJava = new File(FileUtil.toFile(project.getProjectDirectory()), "build/classes/main");
        FileObject foClassesTestJava = project.getProjectDirectory().getFileObject("build/classes/test");
        SourceForBinaryQuery.Result2 sourceRoots = SourceForBinaryQuery.findSourceRoots2(FileUtil.urlForArchiveOrDir(classesMainJava));
        assertThat(sourceRoots.getRoots()).containsOnlyOnce(foSrcMainJava);
    }

    @Test
    @UsesSample("java/multiproject")
    public void multiproject() throws Exception {
        final GradleProjectFixture prjFixture = new GradleProjectFixture(sample.getDir().toFile());
        Project project = prjFixture.importAndFindRootProject();
        Project sharedProject = prjFixture.findSubProject("shared");

        FileObject foSrcMainJava = sharedProject.getProjectDirectory().getFileObject("src/main/java");
        File classesMainJava = new File(FileUtil.toFile(sharedProject.getProjectDirectory()), "build/classes/main");
        SourceForBinaryQuery.Result2 sourceRoots = SourceForBinaryQuery.findSourceRoots2(FileUtil.urlForArchiveOrDir(classesMainJava));
        assertThat(sourceRoots.getRoots()).containsOnlyOnce(foSrcMainJava);
    }
}
