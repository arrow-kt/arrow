plugins {
  id("arrow.kotlin")
  alias(libs.plugins.kotlinx.serialization)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(libs.ktor.server.core)
        implementation(libs.ktor.server.resources)
        api(projects.arrowCore)
        api(projects.arrowRaiseKtorServer)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.ktor.server.contentNegotiation)
        implementation(libs.ktor.serialization.kotlinxJson)
        implementation(libs.ktor.test)
        implementation(libs.bundles.testing)
      }
    }
  }
}
