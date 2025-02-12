plugins {
  id("arrow.kotlin")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        api(libs.cache4k)
      }
    }
    commonTest {
      dependencies {
        implementation(libs.bundles.testing)
      }
    }
  }
}
