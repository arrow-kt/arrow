plugins {
  id("arrow.kotlin")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowFxCoroutines)
        // api(projects.arrowAtomic)
        api(libs.coroutines.core)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.bundles.testing)
      }
    }
  }
}
