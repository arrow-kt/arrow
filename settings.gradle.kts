enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "arrow"

pluginManagement {
  @Suppress("LocalVariableName") val kotlin_repo_url: String? by settings
  repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
    kotlin_repo_url?.also { maven(it) }
  }
}

plugins {
  id("com.gradle.enterprise") version "3.15.1"
  id("org.gradle.toolchains.foojay-resolver-convention") version("0.7.0")
}

dependencyResolutionManagement {
  @Suppress("LocalVariableName") val kotlin_repo_url: String? by settings
  @Suppress("LocalVariableName") val kotlin_version: String? by settings

  repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
    kotlin_repo_url?.also { maven(it) }
  }
  versionCatalogs {
    create("libs") {
      if (!kotlin_version.isNullOrBlank()) {
        println("Overriding Kotlin version with $kotlin_version")
        version("kotlin", kotlin_version!!)
      }
    }
  }
}

//CORE
include("arrow-annotations")
project(":arrow-annotations").projectDir = file("arrow-libs/core/arrow-annotations")

include("arrow-core")
project(":arrow-core").projectDir = file("arrow-libs/core/arrow-core")

include("arrow-atomic")
project(":arrow-atomic").projectDir = file("arrow-libs/core/arrow-atomic")

include("arrow-continuations")
project(":arrow-continuations").projectDir = file("arrow-libs/core/arrow-continuations")

include("arrow-core-retrofit")
project(":arrow-core-retrofit").projectDir = file("arrow-libs/core/arrow-core-retrofit")

include("arrow-core-serialization")
project(":arrow-core-serialization").projectDir = file("arrow-libs/core/arrow-core-serialization")

include("arrow-cache4k")
project(":arrow-cache4k").projectDir = file("arrow-libs/core/arrow-cache4k")

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
