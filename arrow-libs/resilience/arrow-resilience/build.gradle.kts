plugins {
  id("arrow.kotlin")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowFxCoroutines)
      }
    }
    commonTest {
      dependencies {
        implementation(projects.arrowFxCoroutines)
        implementation(projects.arrowPlatform)
        implementation(libs.coroutines.test)
      }
    }
  }
}
