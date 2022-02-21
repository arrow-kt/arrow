plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

apply(from = property("TEST_COVERAGE"))
apply(from = property("ANIMALSNIFFER_MPP"))

dependencies {
  api(projects.arrowCore)
  api(projects.arrowOptics)
  api(libs.kotlin.reflect)
  implementation(libs.kotlin.stdlibJDK8)

  testImplementation(projects.arrowOpticsTest)
  testImplementation(libs.kotlin.stdlibJDK8)
  testImplementation(libs.junitJupiterEngine)
  testImplementation(libs.kotlin.reflect)
}
