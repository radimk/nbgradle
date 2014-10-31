package org.nbgradle.netbeans.project;

import java.util.List;

/**
 * Created by radim on 10/31/14.
 */
public interface GradleLaunchSpec {
    String getDescription();
    Iterable<String> getTaskNames();
}
