@file:Suppress("DSL_SCOPE_VIOLATION")

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
  // `java-library`
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.publish)
  alias(libs.plugins.spotless)
}

apply(from = property("ANIMALSNIFFER_MPP"))

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

kotlin {
  explicitApi()

  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        api(libs.cache4k)
      }
    }
    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.kotest.assertionsCore)
        implementation(libs.coroutines.test)
      }
    }
  }

  jvm {
    withJava()
  }

  js(IR) {
    browser()
    nodejs()
  }

  // Native: https://kotlinlang.org/docs/native-target-support.html
  // -- Tier 1 --
  linuxX64()
  macosX64()
  macosArm64()
  iosSimulatorArm64()
  iosX64()
  // -- Tier 2 --
  linuxArm64()
  watchosSimulatorArm64()
  watchosX64()
  watchosArm64()
  tvosSimulatorArm64()
  tvosX64()
  tvosArm64()
  iosArm64()
  // -- Tier 3 --
  mingwX64()
  // Android and watchOS not included
  // -- Deprecated as of 1.8.20 --
  // iosArm32() // deprecated as of 1.8.20
  // watchosX86()

  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  compilerOptions {
    (project.rootProject.properties["kotlin_language_version"] as? String)?.also { languageVersion = KotlinVersion.fromVersion(it) }
    (project.rootProject.properties["kotlin_api_version"] as? String)?.also { apiVersion = KotlinVersion.fromVersion(it) }
  }
}

tasks.named<Jar>("jvmJar").configure {
  manifest {
    attributes["Automatic-Module-Name"] = "arrow.cache4k"
  }
}
