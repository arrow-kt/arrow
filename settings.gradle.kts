@file:Suppress("LocalVariableName")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "arrow"

pluginManagement {
  @Suppress("LocalVariableName") val kotlin_repo_url: String? by settings
  repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
    kotlin_repo_url?.also { maven(it) }
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
}

plugins {
  id("com.gradle.develocity") version "4.0.1"
  id("org.gradle.toolchains.foojay-resolver-convention") version("0.10.0")
}

val kotlin_repo_url: String? by settings
val kotlin_version: String? by settings
val ksp_version: String? by settings
val compose_version: String? by settings

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
    kotlin_repo_url?.also { maven(it) }
    google()
  }
  versionCatalogs {
    create("libs") {
      if (!kotlin_version.isNullOrBlank()) {
        println("Overriding Kotlin version with $kotlin_version")
        version("kotlin", kotlin_version!!)
      }
      if (!ksp_version.isNullOrBlank()) {
        println("Overriding KSP version with $ksp_version")
        version("kspVersion", ksp_version!!)
      }
      if (!compose_version.isNullOrBlank()) {
        println("Overriding Compose version with $compose_version")
        version("composePlugin", compose_version!!)
      }
    }
  }
}

//CORE
include("arrow-annotations")
project(":arrow-annotations").projectDir = file("arrow-libs/core/arrow-annotations")

include("arrow-platform")
project(":arrow-platform").projectDir = file("arrow-libs/core/arrow-platform")

include("arrow-core")
project(":arrow-core").projectDir = file("arrow-libs/core/arrow-core")

include("arrow-functions")
project(":arrow-functions").projectDir = file("arrow-libs/core/arrow-functions")

include("arrow-core-high-arity")
project(":arrow-core-high-arity").projectDir = file("arrow-libs/core/arrow-core-high-arity")

include("arrow-atomic")
project(":arrow-atomic").projectDir = file("arrow-libs/core/arrow-atomic")

include("arrow-eval")
project(":arrow-eval").projectDir = file("arrow-libs/core/arrow-eval")

include("arrow-cache4k")
project(":arrow-cache4k").projectDir = file("arrow-libs/core/arrow-cache4k")

include("arrow-autoclose")
project(":arrow-autoclose").projectDir = file("arrow-libs/core/arrow-autoclose")

// FX
include("arrow-fx-coroutines")
project(":arrow-fx-coroutines").projectDir = file("arrow-libs/fx/arrow-fx-coroutines")

include("arrow-fx-stm")
project(":arrow-fx-stm").projectDir = file("arrow-libs/fx/arrow-fx-stm")

include("arrow-collectors")
project(":arrow-collectors").projectDir = file("arrow-libs/fx/arrow-collectors")

include("arrow-resilience")
project(":arrow-resilience").projectDir = file("arrow-libs/resilience/arrow-resilience")

// OPTICS
include("arrow-optics")
project(":arrow-optics").projectDir = file("arrow-libs/optics/arrow-optics")

include("arrow-optics-reflect")
project(":arrow-optics-reflect").projectDir = file("arrow-libs/optics/arrow-optics-reflect")

include("arrow-optics-compose")
project(":arrow-optics-compose").projectDir = file("arrow-libs/optics/arrow-optics-compose")

include("arrow-optics-ksp-plugin")
project(":arrow-optics-ksp-plugin").projectDir = file("arrow-libs/optics/arrow-optics-ksp-plugin")

// SUSPENDAPP
include("suspendapp")
project(":suspendapp").projectDir = file("arrow-libs/suspendapp/suspendapp")

include("suspendapp-ktor")
project(":suspendapp-ktor").projectDir = file("arrow-libs/suspendapp/suspendapp-ktor")

include("suspendapp-test-app")
project(":suspendapp-test-app").projectDir = file("arrow-libs/suspendapp/suspendapp-test-app")

include("suspendapp-test-runner")
project(":suspendapp-test-runner").projectDir = file("arrow-libs/suspendapp/suspendapp-test-runner")

// INTEGRATIONS
include("arrow-core-retrofit")
project(":arrow-core-retrofit").projectDir = file("arrow-libs/integrations/arrow-core-retrofit")

include("arrow-core-jackson")
project(":arrow-core-jackson").projectDir = file("arrow-libs/integrations/arrow-core-jackson")

include("arrow-core-serialization")
project(":arrow-core-serialization").projectDir = file("arrow-libs/integrations/arrow-core-serialization")

include("arrow-resilience-ktor-client")
project(":arrow-resilience-ktor-client").projectDir = file("arrow-libs/integrations/arrow-resilience-ktor-client")

include("arrow-raise-ktor-server")
project(":arrow-raise-ktor-server").projectDir = file("arrow-libs/integrations/arrow-raise-ktor-server")

// STACK
include("arrow-stack")
project(":arrow-stack").projectDir = file("arrow-libs/stack")

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
  }
}
