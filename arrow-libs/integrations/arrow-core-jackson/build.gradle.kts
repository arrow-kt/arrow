@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id("arrow.kotlin")
  alias(libs.plugins.publish)
  alias(libs.plugins.kotlinx.serialization) // Needed for testing only
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.spotless)
  alias(libs.plugins.dokka)
}

spotless {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}

apply(from = property("ANIMALSNIFFER"))

dependencies {
  api(projects.arrowCore)
  implementation(libs.jacksonModuleKotlin)
  testImplementation(libs.kotlin.test)
  testImplementation(libs.coroutines.test)
  testImplementation(libs.kotest.property)
  testImplementation(libs.kotest.assertionsCore)
}

tasks.jar {
  manifest {
    attributes["Automatic-Module-Name"] = "arrow.core.jackson"
  }
}
