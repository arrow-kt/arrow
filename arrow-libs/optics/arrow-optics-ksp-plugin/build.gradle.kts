@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.spotless)
}

spotless {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}

kotlin {
  explicitApi = null
}

apply(from = property("ANIMALSNIFFER_MPP"))

val enableCompatibilityMetadataVariant =
  providers.gradleProperty("kotlin.mpp.enableCompatibilityMetadataVariant")
    .orNull?.toBoolean() == true

if (enableCompatibilityMetadataVariant) {
  tasks.withType<Test>().configureEach {
    exclude("**/*")
  }
}

dependencies {
  implementation(libs.ksp)

  if (!enableCompatibilityMetadataVariant) {
    testImplementation(libs.kotlin.stdlib)
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
        group = libs.kotlin.stdlib.get().module.group,
        module = libs.kotlin.stdlib.get().module.name
      )
    }
    testImplementation(libs.kotlinCompileTestingKsp)
    testRuntimeOnly(projects.arrowOpticsKspPlugin)
    testRuntimeOnly(projects.arrowAnnotations)
    testRuntimeOnly(projects.arrowCore)
    testRuntimeOnly(projects.arrowOptics)
  }
}

tasks {
  withType<Test>().configureEach {
    maxParallelForks = 1
  }
}
