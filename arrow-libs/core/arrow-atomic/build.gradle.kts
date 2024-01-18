@file:Suppress("DSL_SCOPE_VIOLATION")

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
  targets.all {
    compilations.all {
      kotlinOptions.freeCompilerArgs += "-Xexpect-actual-classes"
    }
  }

  sourceSets {
    commonMain {
      dependencies {
        api(libs.kotlin.stdlibCommon)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.arrowFxCoroutines)
        implementation(libs.kotlin.test)
        implementation(libs.kotest.assertionsCore)
        implementation(libs.kotest.property)
        implementation(libs.coroutines.test)
      }
    }

    jvmMain {
      dependencies {
        implementation(libs.kotlin.stdlib)
      }
    }
    
    jsMain {
      dependencies {
        implementation(libs.kotlin.stdlibJS)
      }
    }
  }

  jvm {
    tasks.jvmJar {
      manifest {
        attributes["Automatic-Module-Name"] = "arrow.atomic"
      }
    }
  }
}
