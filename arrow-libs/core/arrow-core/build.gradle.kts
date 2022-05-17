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
        api(projects.arrowContinuations)
        api(projects.arrowAnnotations)
        api(libs.kotlin.stdlibCommon)
      }
    }
    commonTest {
      dependencies {
        implementation(projects.arrowCoreTest)
        implementation(projects.arrowFxCoroutines)
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

// enables context receivers for Jvm Tests
tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileTestKotlinJvm") {
  kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"
}
