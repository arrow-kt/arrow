plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  alias(libs.plugins.arrowGradleConfig.publishJvm)
  id("org.jetbrains.kotlin.kapt")
}

publishJVM {
  isDokkaEnabled = false
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
