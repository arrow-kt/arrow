enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "arrow"

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
  }
}

plugins {
  id("com.gradle.enterprise") version "3.14.1"
  id("org.gradle.toolchains.foojay-resolver-convention") version("0.7.0")
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
  }
  versionCatalogs {
    create("libs") {
      val kotlinOverride = System.getenv("KOTLIN_OVERRIDE")
      if (!kotlinOverride.isNullOrBlank()) {
        println("Overriding Kotlin version with $kotlinOverride")
        version("kotlin", kotlinOverride)
      }
    }
  }
}

//CORE
include("arrow-annotations")
project(":arrow-annotations").projectDir = file("arrow-libs/core/arrow-annotations")

include("arrow-core")
project(":arrow-core").projectDir = file("arrow-libs/core/arrow-core")

include("arrow-functions")
project(":arrow-functions").projectDir = file("arrow-libs/core/arrow-functions")

include("arrow-atomic")
project(":arrow-atomic").projectDir = file("arrow-libs/core/arrow-atomic")

include("arrow-core-retrofit")
project(":arrow-core-retrofit").projectDir = file("arrow-libs/core/arrow-core-retrofit")

include("arrow-core-serialization")
project(":arrow-core-serialization").projectDir = file("arrow-libs/core/arrow-core-serialization")

// FX
include("arrow-fx-coroutines")
project(":arrow-fx-coroutines").projectDir = file("arrow-libs/fx/arrow-fx-coroutines")

include("arrow-fx-stm")
project(":arrow-fx-stm").projectDir = file("arrow-libs/fx/arrow-fx-stm")

include("arrow-resilience")
project(":arrow-resilience").projectDir = file("arrow-libs/resilience/arrow-resilience")

// OPTICS
include("arrow-optics")
project(":arrow-optics").projectDir = file("arrow-libs/optics/arrow-optics")

include("arrow-optics-reflect")
project(":arrow-optics-reflect").projectDir = file("arrow-libs/optics/arrow-optics-reflect")

include("arrow-optics-ksp-plugin")
project(":arrow-optics-ksp-plugin").projectDir = file("arrow-libs/optics/arrow-optics-ksp-plugin")

// STACK
include("arrow-stack")
project(":arrow-stack").projectDir = file("arrow-libs/stack")

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}
