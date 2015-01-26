package org.nbgradle.netbeans.java;

import org.nbgradle.netbeans.project.NbGradleConstants;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author radim
 */
@ProjectServiceProvider(service={ClassPathProvider.class, }, projectType=NbGradleConstants.PROJECT_TYPE)
public final class GradleProjectClasspathProvider implements ClassPathProvider {

    @Override
    public ClassPath findClassPath(FileObject fo, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
