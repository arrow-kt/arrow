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
        attributes["Automatic-Module-Name"] = "arrow.continuations"
      }
    }
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
