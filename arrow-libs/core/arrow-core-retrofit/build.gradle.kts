@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.publish)
  alias(libs.plugins.kotlinx.serialization) // Needed for testing only
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.spotless)
}

spotless {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}

apply(from = property("ANIMALSNIFFER"))

dependencies {
  implementation(libs.kotlin.stdlib)
  api(projects.arrowCore)
  compileOnly(libs.squareup.retrofit.lib)

  testImplementation(projects.arrowCore)
  testImplementation(libs.kotlin.test)
  testImplementation(libs.coroutines.test)
  testImplementation(libs.kotest.assertionsCore)
  testImplementation(libs.kotest.property)
  testCompileOnly(libs.kotlin.reflect)
  testImplementation(libs.squareup.okhttpMockWebServer)
  testImplementation(libs.squareup.retrofit.converter.gson)
  testImplementation(libs.squareup.retrofit.converter.moshi)
  testImplementation(libs.kotlinx.serializationJson)
  testImplementation(libs.squareup.retrofit.converter.kotlinxSerialization)
  testImplementation(libs.squareup.moshi.kotlin)
}

tasks.jar {
  manifest {
    attributes["Automatic-Module-Name"] = "arrow.core.retrofit"
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
