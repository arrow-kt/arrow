plugins {
  id("arrow.kotlin")
}

kotlin {
  compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")

  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.bundles.testing)
      }
    }
  }
}
