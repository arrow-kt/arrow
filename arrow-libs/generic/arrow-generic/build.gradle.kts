plugins {
  id("arrow.kotlin")
}

kotlin {
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

  compilerOptions {
    freeCompilerArgs.add("-Xcollection-literals")
    freeCompilerArgs.add("-Xname-based-destructuring=only-syntax")
    freeCompilerArgs.add("-Xnested-type-aliases")
  }
}
