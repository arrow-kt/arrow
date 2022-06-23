plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

apply(from = property("TEST_COVERAGE"))
apply(from = property("ANIMALSNIFFER_MPP"))
apply(plugin = "io.kotest.multiplatform")

val enableCompatibilityMetadataVariant =
  providers.gradleProperty("kotlin.mpp.enableCompatibilityMetadataVariant")
    .forUseAtConfigurationTime().orNull?.toBoolean() == true


if (enableCompatibilityMetadataVariant) {
  tasks.withType<Test>().configureEach {
    enabled = false
  }
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlin.stdlibCommon)
      }
    }
    if(!enableCompatibilityMetadataVariant) {
      commonTest {
        dependencies {
          implementation(project(":arrow-core-test"))
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
