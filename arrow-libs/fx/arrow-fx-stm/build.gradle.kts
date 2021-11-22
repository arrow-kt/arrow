plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

apply(plugin = "io.kotest.multiplatform")
apply(from = property("TEST_COVERAGE"))
apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        compileOnly(libs.kotlin.stdlibCommon)
        implementation(libs.coroutines.core)
      }
    }
    commonTest {
      dependencies {
        implementation(projects.arrowFxCoroutinesTest)
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
