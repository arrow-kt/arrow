plugins {
  id("arrow.kotlin")
}

kotlin {
  sourceSets {
    commonTest {
      dependencies {
        implementation(projects.arrowCore)
        implementation(libs.bundles.testing)
      }
    }
  }
}
