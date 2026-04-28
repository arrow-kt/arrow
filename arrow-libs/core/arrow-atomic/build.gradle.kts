plugins {
  id("arrow.kotlin")
}

kotlin {
  sourceSets {
    commonTest {
      dependencies {
        implementation(projects.arrowFxCoroutines)
        implementation(projects.arrowPlatform)
        implementation(libs.bundles.testing)
      }
    }
  }
}
