/*

    Reason of this file:

      The use of Ank Gradle Plugin in the same workspace where is developed.

    Details:

      Ank Gradle Plugin could be added with project(":arrow-ank-gradle").
      However, the problem raises with the things that are done by that plugin.
      It adds the dependency of arrow-ank with groupId and all its transitive
      dependencies are added as well.
      That's the reason why of using this file that replaces those dependencies
      with groupId with the correspondent local projects.

*/
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }

    repositories {
        mavenCentral()
    }
}

includeBuild("../arrow-libs/") {
    dependencySubstitution {
        substitute(module("io.arrow-kt:arrow-ank-gradle")).using(project(":arrow-ank-gradle"))
        substitute(module("io.arrow-kt:arrow-ank")).using(project(":arrow-ank"))
        substitute(module("io.arrow-kt:arrow-core")).using(project(":arrow-core"))
        substitute(module("io.arrow-kt:arrow-meta")).using(project(":arrow-meta"))
        substitute(module("io.arrow-kt:arrow-fx-stm")).using(project(":arrow-fx-stm"))
        substitute(module("io.arrow-kt:arrow-fx-coroutines")).using(project(":arrow-fx-coroutines"))
        substitute(module("io.arrow-kt:arrow-optics")).using(project(":arrow-optics"))
    }
}
