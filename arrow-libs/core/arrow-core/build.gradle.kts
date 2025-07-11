import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  id("arrow.kotlin")
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
  compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
  compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")

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
