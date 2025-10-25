plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id("arrow.kotlin")
  alias(libs.plugins.kotlinx.serialization) // Needed for testing only
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(projects.arrowCore)
  implementation(libs.jackson.databind)
  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.testing)
  testImplementation(libs.jackson.annotations)
  testImplementation(libs.jackson.module.kotlin)
}

kotlin {
  compilerOptions.freeCompilerArgs.add("-Xannotation-target-all")
}
