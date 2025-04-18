plugins {
  id("arrow.kotlin")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        // implementation(projects.arrowAtomic)
      }
    }
    commonTest {
      dependencies {
        implementation(libs.bundles.testing)
      }
    }
  }
}
