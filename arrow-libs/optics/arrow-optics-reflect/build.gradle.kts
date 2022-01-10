plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

apply(plugin = "io.kotest.multiplatform")

apply(from = property("TEST_COVERAGE"))
apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
  sourceSets {
    jvmMain {
      dependencies {
        api(projects.arrowCore)
        api(projects.arrowOptics)
        implementation(libs.kotlin.stdlibJDK8)
        api(libs.kotlin.reflect)
      }
    }
    jvmTest {
      dependencies {
        implementation(projects.arrowOpticsTest)
        implementation(libs.kotlin.stdlibJDK8)
        implementation(libs.junitJupiterEngine)
        implementation(libs.kotlin.reflect)
      }
    }
  }
}
