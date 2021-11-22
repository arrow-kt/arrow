plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
  sourceSets {
    sourceSets {
      commonMain {
        dependencies {
          api(projects.arrowCoreTest)
          api(projects.arrowFxCoroutines)
          api(libs.coroutines.core)
          api(libs.kotest.assertionsCore)
          api(libs.kotest.frameworkEngine)
          api(libs.kotest.property)
          implementation(libs.kotlin.stdlibCommon)
        }
      }

      named("jvmMain") {
        dependencies {
          implementation(libs.kotlin.stdlibJDK8)
        }
      }
      named("jvmTest") {
        dependencies {
          runtimeOnly(libs.kotest.runnerJUnit5)
        }
      }
      named("jsMain") {
        dependencies {
          implementation(libs.kotlin.stdlibJS)
        }
      }
    }
  }
}
