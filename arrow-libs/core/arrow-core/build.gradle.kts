@file:Suppress("DSL_SCOPE_VIOLATION")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Duration

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.kotlinx.kover)
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
        api(projects.arrowAtomic)
        api(projects.arrowAnnotations)
        api(libs.kotlin.stdlib)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.arrowFxCoroutines)
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
        attributes["Automatic-Module-Name"] = "arrow.core"
      }
    }
  }

  js {
    nodejs {
      testTask {
        useMocha {
          timeout = "300s"
        }
      }
    }
    browser {
      testTask {
        useKarma {
          useChromeHeadless()
          timeout.set(Duration.ofMinutes(5))
        }
      }
    }
  }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.freeCompilerArgs += "-Xexpect-actual-classes"
}

// enables context receivers for Jvm Tests
tasks.named<KotlinCompile>("compileTestKotlinJvm") {
  kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
