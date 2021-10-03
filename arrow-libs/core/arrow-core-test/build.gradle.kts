plugins {
  alias(libs.plugins.arrowGradleConfig.multiplatform)
  alias(libs.plugins.arrowGradleConfig.publishMultiplatform)
}

apply(from = property("ANIMALSNIFFER_MPP"))

publishMultiplatform {
  isDokkaEnabled = false
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        api(libs.coroutines.core)
        api(libs.kotest.assertionsCore)
        api(libs.kotest.frameworkEngine)
        api(libs.kotest.property)
        implementation(libs.kotlin.stdlibCommon)
      }
    }
    jvmMain {
      dependencies {
        implementation(libs.kotlin.stdlibJDK8)
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
