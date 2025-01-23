@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  id(libs.plugins.android.library.get().pluginId)
  id("arrow.kotlin")
  alias(libs.plugins.publish)
  alias(libs.plugins.spotless)
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.dokka)
}

apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowFxCoroutines)
        api(projects.arrowAtomic)
        api(libs.coroutines.core)
        implementation(libs.kotlin.stdlib)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.coroutines.test)
        implementation(libs.kotest.assertionsCore)
        implementation(libs.kotest.property)
      }
    }
  }

  jvm {
    tasks.jvmJar {
      manifest {
        attributes["Automatic-Module-Name"] = "arrow.collectors"
      }
    }
  }
}

android {
  namespace = "arrow.collectors"
  compileSdk = libs.versions.android.compileSdk.get().toInt()
}
