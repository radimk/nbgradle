# nbgradle
NetBeans plugins providing support for Gradle projects.

This is an alternative to a [netbeans-gradle-plugin](https://github.com/kelemen/netbeans-gradle-project) done by
Attila Kelemen. 
The goal is to provide more extensible integration. 
Then it should be possible to better support Android, JEE, and other projects.

## Installation

You need to build the plugins and install them manually.

```./gradlew nbm```

and then install created NBM file (in `core/build` and `java/build`) using Plugin manager accessible from main menu Tools | Plugins.

A simpler version is `./gradlew netBeansRun` to run the plugin in a downloaded local installation of 
NetBeans 8.0.