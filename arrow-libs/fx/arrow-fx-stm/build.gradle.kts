import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  id("arrow.kotlin")
}

kotlin {
  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  compilerOptions {
    freeCompilerArgs.add("-Xexpect-actual-classes")
    freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.arrowCore)
        implementation(libs.coroutines.core)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.arrowFxCoroutines)
        implementation(libs.bundles.testing)
      }
    }
  }
}
