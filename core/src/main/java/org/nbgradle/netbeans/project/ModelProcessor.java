/*
 */
package org.nbgradle.netbeans.project;

import java.util.concurrent.Phaser;

/**
 *
 * @author radim
 */
public interface ModelProcessor {

    void loadFromGradle(final Phaser phaser);

}
