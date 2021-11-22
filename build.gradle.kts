buildscript {
  repositories {
    mavenCentral()
    mavenLocal()
  }

  dependencies {
    classpath(libs.kotlinx.knit)
    classpath(libs.arrowGradleConfig.dokkaFenceWorkaround)
  }
}

plugins {
  base
  alias(libs.plugins.dokka)
  alias(libs.plugins.animalSniffer) apply false
  alias(libs.plugins.kotest.multiplatform) apply false
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.kotlin.binaryCompatibilityValidator)
  alias(libs.plugins.arrowGradleConfig.nexus)
}

apply(plugin = libs.plugins.kotlinx.knit.get().pluginId)

configure<kotlinx.knit.KnitPluginExtension> {
  siteRoot = "https://arrow-kt.io/"
  rootDir = file("arrow-libs")
  files = fileTree(file("arrow-libs")) {
    include("**/*.md")
    include("**/*.kt")
    include("**/*.kts")

    exclude("**/build/**")
    exclude("**/.gradle/**")
  }
}

allprojects {
  group = property("projects.group").toString()
}

tasks {
  val generateDoc by creating(Exec::class) {
    group = "documentation"
    commandLine("sh", "gradlew", "dokkaGfm")
  }
  val runValidation by creating(Exec::class) {
    group = "documentation"
    commandLine("sh", "gradlew", "arrow-ank:runAnk")
  }
  val buildDoc by creating(Exec::class) {
    group = "documentation"
    description = "Generates and validates the documentation"
    dependsOn(generateDoc)
    dependsOn(runValidation)
  }

  runValidation.mustRunAfter(generateDoc)

  val undocumentedProjects = listOf(
    project(":arrow-core-test"),
    project(":arrow-meta"),
    project(":arrow-fx-coroutines-test"),
    project(":arrow-optics-test"),
  )

  dokkaGfmMultiModule { removeChildTasks(undocumentedProjects) }
  dokkaHtmlMultiModule { removeChildTasks(undocumentedProjects) }
  dokkaJekyllMultiModule { removeChildTasks(undocumentedProjects) }
}
