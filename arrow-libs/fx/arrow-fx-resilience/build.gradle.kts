plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
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
        implementation(projects.arrowFxCoroutines)
        compileOnly(libs.kotlin.stdlibCommon)
        implementation("org.jetbrains.kotlin:kotlin-stdlib") {
          version {
            strictly("[1.7, 1.8[")
            prefer("1.8.10")
          }
        }
      }
    }
    if (!enableCompatibilityMetadataVariant) {
      commonTest {
        dependencies {
          implementation(libs.coroutines.test)
          implementation(kotlin("test"))
        }
      }
    }
  }
}
