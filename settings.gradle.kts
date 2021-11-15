enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "arrow"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

//CORE
include("arrow-annotations")
project(":arrow-annotations").projectDir = file("arrow-libs/core/arrow-annotations")

include("arrow-core")
project(":arrow-core").projectDir = file("arrow-libs/core/arrow-core")

include("arrow-core-test")
project(":arrow-core-test").projectDir = file("arrow-libs/core/arrow-core-test")

include("arrow-continuations")
project(":arrow-continuations").projectDir = file("arrow-libs/core/arrow-continuations")

include("arrow-meta")
project(":arrow-meta").projectDir = file("arrow-libs/core/arrow-meta")

include("arrow-meta-test-models")
project(":arrow-meta-test-models").projectDir = file("arrow-libs/core/arrow-meta/arrow-meta-test-models")

include("arrow-core-retrofit")
project(":arrow-core-retrofit").projectDir = file("arrow-libs/core/arrow-core-retrofit")

// FX
include("arrow-fx-coroutines")
project(":arrow-fx-coroutines").projectDir = file("arrow-libs/fx/arrow-fx-coroutines")

include("arrow-fx-coroutines-test")
project(":arrow-fx-coroutines-test").projectDir = file("arrow-libs/fx/arrow-fx-coroutines-test")

include("arrow-fx-stm")
project(":arrow-fx-stm").projectDir = file("arrow-libs/fx/arrow-fx-stm")

// OPTICS
include("arrow-optics")
project(":arrow-optics").projectDir = file("arrow-libs/optics/arrow-optics")

include("arrow-optics-test")
project(":arrow-optics-test").projectDir = file("arrow-libs/optics/arrow-optics-test")

// STACK
include("arrow-stack")
project(":arrow-stack").projectDir = file("arrow-libs/stack")

// SITE
include("arrow-site")
project(":arrow-site").projectDir = file("arrow-site")
