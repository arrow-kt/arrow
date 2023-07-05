@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.arrowGradleConfig.versioning)
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.spotless)
}

spotless {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}

val enableCompatibilityMetadataVariant =
  providers.gradleProperty("kotlin.mpp.enableCompatibilityMetadataVariant")
    .orNull?.toBoolean() == true

if (enableCompatibilityMetadataVariant) {
  tasks.withType<Test>().configureEach {
    exclude("**/*")
  }
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        compileOnly(libs.kotlin.stdlibCommon)
        implementation(libs.coroutines.core)
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
      }
    }
    if (!enableCompatibilityMetadataVariant) {
      commonTest {
        dependencies {
          implementation(projects.arrowFxCoroutines)
          implementation(libs.coroutines.test)
          implementation(kotlin("test"))
        }
      }
    }
  }
}

tasks.jar {
  manifest {
    attributes["Automatic-Module-Name"] = "arrow.resilience"
  }
}
