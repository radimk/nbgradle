/*
 */
package org.nbgradle.netbeans.java;

/**
 *
 * @author radim
 */
public interface RegisteredClassPathProvider {
  /** Register classpath to GlobalPathRegistry. */
  void register();
  /** Unregister classpath from GlobalPathRegistry. */
  void unregister();
}
