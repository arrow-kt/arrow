@file:Suppress("DSL_SCOPE_VIOLATION")

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion


plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
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
    (project.rootProject.properties["kotlin_language_version"] as? String)?.also { languageVersion = KotlinVersion.fromVersion(it) }
    (project.rootProject.properties["kotlin_api_version"] as? String)?.also { apiVersion = KotlinVersion.fromVersion(it) }
    freeCompilerArgs.add("-Xexpect-actual-classes")
  }
}
