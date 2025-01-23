@file:Suppress("DSL_SCOPE_VIOLATION")

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
      }
    }
  }

  jvm {
    tasks.jvmJar {
      manifest {
        attributes["Automatic-Module-Name"] = "arrow.annotations"
      }
    }
  }
}

android {
  namespace = "arrow.annotations"
  compileSdk = libs.versions.android.compileSdk.get().toInt()
}
