@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
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

dependencies {
  api(projects.arrowCore)
  api(projects.arrowOptics)
  api(libs.kotlin.reflect)
  implementation(libs.kotlin.stdlib)

  testImplementation(libs.kotlin.stdlib)
  testImplementation(libs.junitJupiterEngine)
  testImplementation(libs.kotlin.reflect)

  testImplementation(libs.kotest.frameworkEngine)
  testImplementation(libs.kotest.assertionsCore)
  testImplementation(libs.kotest.property)
  testRuntimeOnly(libs.kotest.runnerJUnit5)
}

tasks.jar {
  manifest {
    attributes["Automatic-Module-Name"] = "arrow.optics.reflect"
  }
}
