plugins {
  id("arrow.kotlin")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(libs.coroutines.core)
      }
    }
  }
}
