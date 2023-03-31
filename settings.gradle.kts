enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "arrow"

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
  }
}

plugins {
  id("com.gradle.enterprise") version "3.11.4"
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
  }
}

val enableCompatibilityMetadataVariant =
  providers.gradleProperty("kotlin.mpp.enableCompatibilityMetadataVariant")
    .forUseAtConfigurationTime().orNull?.toBoolean() == true

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

// SITE
include("arrow-site")
project(":arrow-site").projectDir = file("arrow-site")

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}
