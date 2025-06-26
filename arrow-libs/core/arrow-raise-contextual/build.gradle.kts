plugins {
  id("arrow.kotlin")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.add("-Xcontext-parameters")
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.arrowCore)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.arrowPlatform)
        implementation(libs.bundles.testing)
      }
    }
  }
}
