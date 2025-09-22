plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id("arrow.kotlin")
}

kotlin {
  compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")
}

dependencies {
  api(projects.arrowCore)
  api(libs.result4k)

  testImplementation(projects.arrowCore)
  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.testing)
}
