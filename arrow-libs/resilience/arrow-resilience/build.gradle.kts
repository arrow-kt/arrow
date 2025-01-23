@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  id(libs.plugins.android.library.get().pluginId)
  id("arrow.kotlin")
  alias(libs.plugins.publish)
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.spotless)
  alias(libs.plugins.dokka)
}

spotless {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        implementation(libs.kotlin.stdlib)
        implementation(libs.coroutines.core)
      }
    }
    commonTest {
      dependencies {
        implementation(projects.arrowFxCoroutines)
        implementation(projects.arrowPlatform)
        implementation(libs.kotlin.test)
        implementation(libs.coroutines.test)
      }
    }
  }

  jvm {
    tasks.jvmJar {
      manifest {
        attributes["Automatic-Module-Name"] = "arrow.resilience"
      }
    }
  }
}

android {
  namespace = "arrow.resilience"
  compileSdk = libs.versions.android.compileSdk.get().toInt()
}
