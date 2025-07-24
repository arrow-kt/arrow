plugins {
  id("arrow.kotlin")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.arrowCore)
        implementation(libs.coroutines.core)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.arrowPlatform)
        implementation(libs.bundles.testing)
      }
    }

    jvmTest {
      dependencies {
        implementation(libs.coroutines.core)
      }
    }
  }
}
