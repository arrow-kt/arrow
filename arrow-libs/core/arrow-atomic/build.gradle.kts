plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

apply(plugin = "io.kotest.multiplatform")
apply(from = property("TEST_COVERAGE"))
apply(from = property("ANIMALSNIFFER_MPP"))

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
        api(libs.kotlin.stdlibCommon)
      }
    }

    jvmMain {
      dependencies {
        implementation(libs.kotlin.stdlibJDK8)
      }
    }
    
    jvmTest {
      dependencies {
        implementation(projects.arrowFxCoroutines)
      }
    }
    
    jsMain {
      dependencies {
        implementation(libs.kotlin.stdlibJS)
      }
    }
  }
}
