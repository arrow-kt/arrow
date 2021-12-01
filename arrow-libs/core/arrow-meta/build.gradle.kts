plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  id(libs.plugins.kotlin.kapt.get().pluginId)
}

dependencies {
  api(projects.arrowAnnotations)
  api(libs.arrow.kotlinMetadata)
  api(libs.squareup.kotlinPoet) {
    exclude(
      group = libs.kotlin.reflect.get().module.group,
      module = libs.kotlin.reflect.get().module.toString(),
    )
  }
  api(libs.kotlin.reflect)
  implementation(libs.google.autoService)
  kapt(libs.google.autoService)

  // To generate stubs for data classes
  kaptTest(libs.google.autoService)
  testImplementation(libs.google.compileTesting)
  testImplementation(fileTree("dir" to "./src/test/libs", "includes" to listOf("*.jar")))
  testImplementation(projects.arrowMetaTestModels)
  testImplementation(libs.jUnitJUnit)
  testRuntimeOnly(libs.jUnitVintageEngine)
  testImplementation(libs.kotlinTest.runnerJUnit4) { exclude(group = "io.arrow-kt") }
}
