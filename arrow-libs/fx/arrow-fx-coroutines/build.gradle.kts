@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.spotless)
}

spotless {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}

apply(plugin = "io.kotest.multiplatform")

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        api(libs.coroutines.core)
        implementation(libs.kotlin.stdlibCommon)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.kotest.frameworkEngine)
        implementation(libs.kotest.assertionsCore)
        implementation(libs.kotest.property)
        implementation(libs.coroutines.test)
      }
    }

    jvmTest {
      dependencies {
        runtimeOnly(libs.kotest.runnerJUnit5)
      }
    }

    jvmMain {
      dependencies {
        implementation(libs.kotlin.stdlib)
      }
    }

    jsMain {
      dependencies {
        implementation(libs.kotlin.stdlibJS)
      }
    }
  }

  jvm {
    tasks.jvmJar {
      manifest {
        attributes["Automatic-Module-Name"] = "arrow.fx.coroutines"
      }
    }
  }
}
