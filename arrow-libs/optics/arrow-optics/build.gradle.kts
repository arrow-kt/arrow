@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.kotest.multiplatform)
  alias(libs.plugins.spotless)
}

spotless {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}

apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        api(libs.kotlin.stdlib)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.coroutines.test)
        implementation(libs.kotest.frameworkEngine)
        implementation(libs.kotest.assertionsCore)
        implementation(libs.kotest.property)
      }
    }

    jvmTest {
      dependencies {
        implementation(libs.kotlin.stdlib)
        implementation(libs.kotest.frameworkEngine)
        implementation(libs.junitJupiterEngine)
        implementation(libs.kotlin.reflect)
      }
    }

    jvmMain {
      dependencies {
        api(libs.kotlin.reflect)
      }
    }
  }

  jvm {
    tasks.jvmJar {
      manifest {
        attributes["Automatic-Module-Name"] = "arrow.optics"
      }
    }
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
