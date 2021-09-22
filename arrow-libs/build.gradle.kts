import io.github.nomisrev.setupDokka

buildscript {
  apply(from = "gradle/setup.gradle")
  repositories {
    mavenCentral()
  }
}

plugins {
  id("org.jlleitschuh.gradle.ktlint") version "10.1.0" apply false
  id("org.jetbrains.dokka") version "1.5.0" apply false
  id("ru.vyarus.animalsniffer") version "1.5.0" apply false
  id("io.kotest.multiplatform") version "5.0.0.5" apply false
  id("org.jetbrains.kotlin.multiplatform") version "1.5.31" apply false
  id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.7.1"
  id("documentation")
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

val excludeDocs = setOf(
  "arrow-ank-gradle",
  "arrow-core-test",
  "arrow-fx-coroutines-test",
  "arrow-meta",
  "arrow-optics-test",
  "examples",
  "jekyll",
  "arrow-meta-test-models",
)

configure(
  subprojects.filter { it.name !in excludeDocs }
) {
  apply(plugin = "org.jetbrains.dokka")

  setupDokka(
    outputDirectory = file("${rootDir}/../arrow-site/docs/apidocs"),
    baseUrl = "https://github.com/arrow-kt/arrow/blob/main"
  )

  dependencies {
    // Really Gradle !?
    "dokkaGfmPlugin"(project(":jekyll"))
  }
}
