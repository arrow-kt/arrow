@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)

  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.spotless)
  id(libs.plugins.kotlinx.serialization.get().pluginId)
}

apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        api(libs.kotlin.stdlib)
        api(libs.kotlinx.serializationCore)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlinx.serializationJson)
        implementation(libs.kotlin.test)
        implementation(libs.coroutines.test)
        implementation(libs.kotest.assertionsCore)
        implementation(libs.kotest.property)
      }
    }
  }

  jvm {
    tasks.jvmJar {
      manifest {
        attributes["Automatic-Module-Name"] = "arrow.core.serialization"
      }
    }
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
