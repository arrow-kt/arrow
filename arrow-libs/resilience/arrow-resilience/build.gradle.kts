plugins {
  id("arrow.kotlin")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        implementation(libs.coroutines.core)
        implementation(projects.arrowExceptionUtils)
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
