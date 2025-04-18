plugins {
  id("arrow.kotlin")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        api(projects.arrowAutoclose)
        api(libs.coroutines.core)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.arrowCore)
        // implementation(projects.arrowAtomic)
        implementation(projects.arrowPlatform)
        implementation(libs.bundles.testing)
      }
    }
  }
}
