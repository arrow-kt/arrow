plugins {
  id("arrow.kotlin")
}

kotlin {
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
