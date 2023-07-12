@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  
  alias(libs.plugins.kotest.multiplatform)
  id(libs.plugins.kotlinx.serialization.get().pluginId)
}

apply(from = property("ANIMALSNIFFER_MPP"))

val enableCompatibilityMetadataVariant =
  providers.gradleProperty("kotlin.mpp.enableCompatibilityMetadataVariant")
    .orNull?.toBoolean() == true

if (enableCompatibilityMetadataVariant) {
  tasks.withType<Test>().configureEach {
    exclude("**/*")
  }
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        api(libs.kotlin.stdlibCommon)
        api(libs.kotlinx.serializationCore)
      }
    }
    if (!enableCompatibilityMetadataVariant) {
      commonTest {
        dependencies {
          implementation(libs.kotlinx.serializationJson)
          implementation(libs.kotest.frameworkEngine)
          implementation(libs.kotest.assertionsCore)
          implementation(libs.kotest.property)
        }
      }

      jvmTest {
        dependencies {
          runtimeOnly(libs.kotest.runnerJUnit5)
        }
      }
    }
  }
}
