package org.nbgradle.netbeans.project.utils;

import java.io.File;

/**
 *
 * @author radim
 */
public class IoUtils {

    public static boolean isParentOrSame(File parent, File child) {
        for (File current = child; current != null; current = current.getParentFile()) {
            if (current.equals(parent)) {
                return true;
            }
        }
        return false;
    }
}
