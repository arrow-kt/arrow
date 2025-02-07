plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id("arrow.kotlin")
}

dependencies {
  api(projects.arrowCore)
  api(projects.arrowOptics)
  api(kotlin("reflect"))

  testImplementation(kotlin("test"))
  testImplementation(kotlin("reflect"))
  testImplementation(libs.bundles.testing)
}
