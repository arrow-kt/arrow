plugins {
  id("arrow.kotlin")
  alias(libs.plugins.kotlinx.serialization)
}

kotlin {
  compilerOptions.freeCompilerArgs.add("-Xcontext-receivers")

  sourceSets {
    commonMain {
      dependencies {
        api(libs.ktor.server.core)
        api(projects.arrowCore)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.ktor.server.contentNegotiation)
        implementation(libs.ktor.serialization.kotlinxJson)
        implementation(libs.ktor.test)
        implementation(libs.bundles.testing)
      }
    }
  }
}
