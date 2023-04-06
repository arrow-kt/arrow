@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.arrowGradleConfig.versioning)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlin.stdlibCommon)
      }
    }
    jvmMain {
      dependencies {
        implementation(libs.kotlin.stdlib)
      }
    }
    jvmTest {
      dependencies {
        runtimeOnly(libs.kotest.runnerJUnit5)
      }
    }
    jsMain {
      dependencies {
        implementation(libs.kotlin.stdlibJS)
      }
    }
  }
}

apply(from = property("ANIMALSNIFFER_MPP"))
