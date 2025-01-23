@file:Suppress("DSL_SCOPE_VIOLATION")

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  id(libs.plugins.android.library.get().pluginId)
  id("arrow.kotlin")
  alias(libs.plugins.publish)
  alias(libs.plugins.spotless)
  alias(libs.plugins.dokka)
}

spotless {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}

apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlin.testAnnotations)
      }
    }
    jvmMain {
      dependencies {
        implementation(libs.kotlin.testJUnit5)
      }
    }
    jsMain {
      dependencies {
        implementation(libs.kotlin.test)
      }
    }
  }

  jvm {
    tasks.jvmJar {
      manifest {
        attributes["Automatic-Module-Name"] = "arrow.platform"
      }
    }
  }

  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  compilerOptions {
    freeCompilerArgs.add("-Xexpect-actual-classes")
  }
}

android {
  namespace = "arrow.platform"
  compileSdk = libs.versions.android.compileSdk.get().toInt()
}
