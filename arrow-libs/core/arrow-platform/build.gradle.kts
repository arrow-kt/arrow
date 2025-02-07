import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  id("arrow.kotlin")
}

kotlin {
  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")

  sourceSets {
    commonMain {
      dependencies {
        implementation(kotlin("test-annotations-common"))
      }
    }
    jvmMain {
      dependencies {
        implementation(kotlin("test-junit5"))
      }
    }
    jsMain {
      dependencies {
        implementation(kotlin("test"))
      }
    }
  }
}
