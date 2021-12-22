plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

kotlin {
  explicitApi = null
}

apply(plugin = "io.kotest.multiplatform")

apply(from = property("TEST_COVERAGE"))
apply(from = property("ANIMALSNIFFER_MPP"))

dependencies {
  implementation(libs.ksp)

  testImplementation(libs.kotlin.stdlibJDK8)
  testImplementation(libs.junitJupiter)
  testImplementation(libs.junitJupiterEngine)
  testImplementation(libs.assertj)
  testImplementation(libs.classgraph)
  testImplementation(libs.kotlinCompileTesting) {
    exclude(
      group = libs.classgraph.get().module.group,
      module = libs.classgraph.get().module.name
    )
    exclude(
      group = libs.kotlin.stdlibJDK8.get().module.group,
      module = libs.kotlin.stdlibJDK8.get().module.name
    )
  }
  testImplementation(libs.kotlinCompileTestingKsp)
  testRuntimeOnly(projects.arrowOpticsKspPlugin)
  testRuntimeOnly(projects.arrowAnnotations)
  testRuntimeOnly(projects.arrowCore)
  testRuntimeOnly(projects.arrowOptics)
}
