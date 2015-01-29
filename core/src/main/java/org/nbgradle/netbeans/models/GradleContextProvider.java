/*
 */
package org.nbgradle.netbeans.models;

import java.io.File;

/**
 *
 * @author radim
 */
public interface GradleContextProvider {

    GradleContext forProject(File projectDir);
}
