@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.publish)
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
  testImplementation(libs.kotlin.test)
  testImplementation(libs.coroutines.test)
  testImplementation(libs.kotlin.reflect)
  testImplementation(libs.kotest.assertionsCore)
  testImplementation(libs.kotest.property)
}

tasks.jar {
  manifest {
    attributes["Automatic-Module-Name"] = "arrow.optics.reflect"
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
