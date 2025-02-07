import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  id("arrow.kotlin")
}

kotlin {
  @OptIn(ExperimentalKotlinGradlePluginApi::class)
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
        implementation(libs.ktor.test)
        implementation(libs.bundles.testing)
      }
    }
  }
}
