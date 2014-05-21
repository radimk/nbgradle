package org.nbgradle.netbeans.project;

/**
 * Created by radim on 5/21/14.
 */
public class ProjectImportException extends RuntimeException {
    public ProjectImportException(String message) {
        super(message);
    }

    public ProjectImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
