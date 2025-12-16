plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id("arrow.kotlin")
  alias(libs.plugins.kotlinx.serialization) // Needed for testing only
}

dependencies {
  api(projects.arrowCore)
  implementation(libs.jackson2.module.kotlin)
  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.testing)
}
