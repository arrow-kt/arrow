plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  // alias(libs.plugins.kotest.multiplatform)
}

apply(from = property("TEST_COVERAGE"))

val enableCompatibilityMetadataVariant =
  providers.gradleProperty("kotlin.mpp.enableCompatibilityMetadataVariant")
    .forUseAtConfigurationTime().orNull?.toBoolean() == true

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
        implementation(libs.coroutines.test)
      }
    }

    if (!enableCompatibilityMetadataVariant) {
      commonTest {
        dependencies {
          implementation(projects.arrowCore)
          implementation(libs.kotest.frameworkApi)
          implementation(libs.kotest.assertionsCore)
          implementation(libs.kotest.property)
          implementation(libs.kotest.arrowAssertions.get().toString()) {
            exclude(group = "io.arrow-kt", module = "arrow-core")
          }
          implementation(libs.kotest.arrowProperty.get().toString()) {
            exclude(group = "io.arrow-kt", module = "arrow-core")
          }
          implementation(libs.kotest.arrowFxAssertions.get().toString()) {
            exclude(group = "io.arrow-kt", module = "arrow-core")
            exclude(group = "io.arrow-kt", module = "arrow-fx-coroutines")
          }
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
        implementation(libs.kotlin.stdlibJDK8)
      }
    }
    jsMain {
      dependencies {
        implementation(libs.kotlin.stdlibJS)
      }
    }
  }
}
