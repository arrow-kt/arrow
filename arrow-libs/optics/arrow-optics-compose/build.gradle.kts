@file:Suppress("DSL_SCOPE_VIOLATION")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


repositories {
  google()
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  // alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.spotless)
  alias(libs.plugins.jetbrainsCompose)
  alias(libs.plugins.android.library)
}

apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
  explicitApi()

  jvm {
    jvmToolchain(8)
  }
  js(IR) {
    browser()
    nodejs()
  }
  androidTarget()
  // Native: https://kotlinlang.org/docs/native-target-support.html
  // -- Tier 1 --
  linuxX64()
  macosX64()
  macosArm64()
  iosSimulatorArm64()
  iosX64()
  // -- Tier 2 --
  // linuxArm64()
  watchosSimulatorArm64()
  watchosX64()
  watchosArm64()
  tvosSimulatorArm64()
  tvosX64()
  tvosArm64()
  iosArm64()
  // -- Tier 3 --
  mingwX64()

  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowOptics)
        api(libs.coroutines.core)
        api(compose.runtime)
        implementation(libs.kotlin.stdlibCommon)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.kotest.assertionsCore)
        implementation(libs.kotest.property)
      }
    }
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

compose {
  // override the choice of Compose if we use a Kotlin -dev version
  val kotlinVersion = project.rootProject.properties["kotlin_version"] as? String
  if (kotlinVersion != null && kotlinVersion.contains("-dev-")) {
    kotlinCompilerPlugin.set(dependencies.compiler.forKotlin("1.9.20"))
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.9.20")
  }
}

android {
  namespace = "arrow.optics.compose"
  compileSdk = libs.versions.android.compileSdk.get().toInt()
}

tasks.named<Jar>("jvmJar").configure {
  manifest {
    attributes["Automatic-Module-Name"] = "arrow.optics.compose"
  }
}
