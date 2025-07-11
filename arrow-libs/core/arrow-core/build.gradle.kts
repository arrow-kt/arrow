import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  id("arrow.kotlin")
  alias(libs.plugins.kotlin.jsPlainObjects)
}

kotlin {
  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")

  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowAtomic)
        api(projects.arrowAnnotations)
      }
    }

    jsMain {
      dependencies {
        implementation(libs.kotlinWrappers.js)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.arrowFxCoroutines)
        implementation(projects.arrowPlatform)
        implementation(libs.bundles.testing)
      }
    }
  }
}
