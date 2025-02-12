plugins {
  id("arrow.kotlin")
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
        implementation(libs.ktor.test)
        implementation(libs.bundles.testing)
      }
    }
  }
}
