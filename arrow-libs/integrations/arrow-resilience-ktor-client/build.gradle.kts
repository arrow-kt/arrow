plugins {
  id("arrow.kotlin")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(libs.ktor.client.core)
        api(projects.arrowResilience)
        implementation(projects.arrowAtomic)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.ktor.test)
        implementation(libs.ktor.mock)
        implementation(projects.arrowAtomic)
        implementation(libs.bundles.testing)
      }
    }
  }
}
