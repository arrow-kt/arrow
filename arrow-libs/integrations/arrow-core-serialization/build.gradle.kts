@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  id(libs.plugins.android.library.get().pluginId)
  id("arrow.kotlin")
  alias(libs.plugins.publish)
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.spotless)
  alias(libs.plugins.dokka)
  id(libs.plugins.kotlinx.serialization.get().pluginId)
}

apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        api(libs.kotlin.stdlib)
        api(libs.kotlinx.serializationCore)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlinx.serializationJson)
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
        attributes["Automatic-Module-Name"] = "arrow.core.serialization"
      }
    }
  }
}

android {
  namespace = "arrow.core.serialization"
  compileSdk = libs.versions.android.compileSdk.get().toInt()
}
