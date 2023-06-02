@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.arrowGradleConfig.versioning)
}

apply(plugin = "io.kotest.multiplatform")
apply(from = property("TEST_COVERAGE"))

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
        api(libs.coroutines.core)
        implementation(libs.kotlin.stdlibCommon)
      }
    }

    if (!enableCompatibilityMetadataVariant) {
      commonTest {
        dependencies {
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
}
