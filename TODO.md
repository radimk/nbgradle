TODOs for Gradle support in NetBeans

Core functionality

- Multi-projects
    - project cross-dependencies
    - loading/opening/sharing models
- Project settings to use VM args, Gradle distro
- refresh/reload
- Target JDK platform customization, show in project view
- File encoding
- Sharability query
- caches
- Binary <-> source <-> javadoc mapping in projects, repos, caches
- .gradle files support
    - Groovy extending editor 
    - outline view with tasks

Execution

- action mapping to tasks
    - application run (with arguments)
    - single test run
    - webapps
    - custom actions
- debugging

Better mapping to Gradle

- Custom Gradle model
    - information about sourceSets and dependencies

Android inter-operability

- delegate classpath/nodes to Android/Java implementation
- variants support (ProjectConfiguration?)


Project templates