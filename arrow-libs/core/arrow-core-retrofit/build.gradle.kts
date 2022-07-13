plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.kotlinx.serialization) // Needed for testing only
}

apply(from = property("TEST_COVERAGE"))
apply(from = property("ANIMALSNIFFER"))

val enableCompatibilityMetadataVariant =
  providers.gradleProperty("kotlin.mpp.enableCompatibilityMetadataVariant")
    .forUseAtConfigurationTime().orNull?.toBoolean() == true

if (enableCompatibilityMetadataVariant) {
  tasks.withType<Test>().configureEach {
    exclude("**/*")
  }
}

dependencies {
  compileOnly(libs.kotlin.stdlibJDK8)
  compileOnly(projects.arrowCore)
  compileOnly(libs.squareup.retrofit)

  if (!enableCompatibilityMetadataVariant) {
    testCompileOnly(libs.kotlin.reflect)
    testRuntimeOnly(libs.kotest.runnerJUnit5)
    testImplementation(project(":arrow-core-test"))
    testImplementation(libs.squareup.okhttpMockWebServer)
    testImplementation(libs.squareup.retrofitConverterGson)
    testImplementation(libs.squareup.retrofitConverterMoshi)
    testImplementation(libs.kotlinx.serializationJson)
    testImplementation(libs.jakewharton.retrofitConverterKotlinxSerialization)
  }
}
