plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id("arrow.kotlin")
  alias(libs.plugins.kotlinx.serialization) // Needed for testing only
}

dependencies {
  implementation(kotlin("stdlib"))
  api(projects.arrowCore)
  compileOnly(libs.squareup.retrofit.lib)

  testImplementation(projects.arrowCore)
  testImplementation(kotlin("test"))
  testImplementation(libs.bundles.testing)
  testCompileOnly(kotlin("reflect"))
  testImplementation(libs.squareup.okhttpMockWebServer)
  testImplementation(libs.squareup.retrofit.converter.gson)
  testImplementation(libs.squareup.retrofit.converter.moshi)
  testImplementation(libs.kotlinx.serializationJson)
  testImplementation(libs.squareup.retrofit.converter.kotlinxSerialization)
  testImplementation(libs.squareup.moshi.kotlin)
}

