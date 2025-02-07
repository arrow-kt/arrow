plugins {
  id("arrow.kotlin")
  id(libs.plugins.kotlinx.serialization.get().pluginId)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        api(libs.kotlinx.serializationCore)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlinx.serializationJson)
        implementation(libs.bundles.testing)
      }
    }
  }
}
