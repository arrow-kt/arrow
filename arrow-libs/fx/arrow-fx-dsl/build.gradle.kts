@file:Suppress("DSL_SCOPE_VIOLATION")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.publish)
  alias(libs.plugins.spotless)
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
        implementation(libs.coroutines.core)
        implementation(projects.arrowAtomic)
        implementation(projects.arrowAutoclose)
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
    tasks.jvmJar {
      manifest {
        attributes["Automatic-Module-Name"] = "arrow.autocloseable"
      }
    }
  }
}

// enables context receivers for Jvm Tests
tasks.named<KotlinCompile>("compileTestKotlinJvm") {
  compilerOptions.freeCompilerArgs.add("-Xcontext-receivers")
}
