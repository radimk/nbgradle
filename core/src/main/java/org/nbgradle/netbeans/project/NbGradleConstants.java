package org.nbgradle.netbeans.project;

public class NbGradleConstants {
    /** Name of file to store root project configuration. */
    public static final String NBGRADLE_BUILD_XML = "nbgradlebuild.xml";
    /** Name of file to store project configuration. */
    public static final String NBGRADLE_XML = "nbgradle.xml";

    public static final String PROJECT_TYPE = "org-nbgradle-netbeans-project";
    public static final String BUILD_GRADLE_FILENAME = "build.gradle";
    public static final String SETTINGS_GRADLE_FILENAME = "settings.gradle";

    // TODO possibly move to java module
    /**
     * Java package root sources type.
     * @see org.netbeans.api.project.Sources
     */
    public static final String SOURCES_TYPE_TEST_JAVA = "test-java"; // NOI18N

    /**
     * Package root sources type for resources, if these are not put together with Java sources.
     * @see org.netbeans.api.project.Sources
     * @since org.netbeans.modules.java.project/1 1.11
     */
    public static final String SOURCES_TYPE_TEST_RESOURCES = "test-resources"; // NOI18N
}
