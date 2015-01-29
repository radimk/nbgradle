/*
 */
package org.nbgradle.netbeans.java;

import org.netbeans.api.java.classpath.ClassPath;

/**
 *
 * @author radim
 */
public interface RegisteredClassPathProvider {

    /**
     * Register classpath to GlobalPathRegistry.
     */
    void register();

    /**
     * Unregister classpath from GlobalPathRegistry.
     */
    void unregister();

    /**
     * 
     * @param sourceType constant for main java sources or test sources.
     * @param type BOOT/COMPILE/...
     * @return classpath or null
     */
    ClassPath findClassPath(String sourceType, String type);
}
