buildscript {
  apply(from = "gradle/setup.gradle")
}

plugins {
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.animalSniffer) apply false
    alias(libs.plugins.kotest.multiplatform) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.binaryCompatibilityValidator)
    alias(libs.plugins.arrowGradleConfig.nexus)
}

apply(from = "gradle/main.gradle")

tasks {
  val generateDoc by creating(Exec::class) {
    group = "documentation"
    commandLine("sh", "gradlew", "dokkaGfm")
  }
  val runValidation by creating(Exec::class) {
    group = "documentation"
    workingDir = file("../arrow-site")
    commandLine("sh", "gradlew", "dokkaGfm")
  }
  val buildDoc by creating(Exec::class) {
    group = "documentation"
    description = "Generates and validates the documentation"
    dependsOn(generateDoc)
    dependsOn(runValidation)
  }

  runValidation.mustRunAfter(generateDoc)
}

apiValidation {
  ignoredProjects.add("jekyll")
}
