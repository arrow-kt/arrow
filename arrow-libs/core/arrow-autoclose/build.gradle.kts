plugins {
  id("arrow.kotlin")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.arrowAtomic)
        implementation(projects.arrowExceptionUtils)
      }
    }
    commonTest {
      dependencies {
        implementation(libs.bundles.testing)
      }
    }
  }
}
