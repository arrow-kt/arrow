plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

apply(from = property("TEST_COVERAGE"))
apply(from = property("ANIMALSNIFFER_MPP"))

val enableCompatibilityMetadataVariant =
  providers.gradleProperty("kotlin.mpp.enableCompatibilityMetadataVariant")
    .forUseAtConfigurationTime().orNull?.toBoolean() == true

if (enableCompatibilityMetadataVariant) {
  tasks.withType<Test>().configureEach {
    exclude("**/*")
  }
}

dependencies {
  api(projects.arrowCore)
  api(projects.arrowOptics)
  api(libs.kotlin.reflect)
  implementation(libs.kotlin.stdlibJDK8)

  if(!enableCompatibilityMetadataVariant) {
    testImplementation(libs.kotest.frameworkEngine)
    testImplementation(libs.kotest.assertionsCore)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.arrowOpticsProperty)
    testImplementation(libs.kotlin.stdlibJDK8)
    testImplementation(libs.junitJupiterEngine)
    testImplementation(libs.kotlin.reflect)
  }
}
