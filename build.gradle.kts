buildscript {
  repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
  }

  dependencies {
    classpath(libs.kotlinx.knit)
  }
}

allprojects {
  repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
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
  // alias(libs.plugins.arrowGradleConfig.versioning)
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

val enableCompatibilityMetadataVariant =
  providers.gradleProperty("kotlin.mpp.enableCompatibilityMetadataVariant")
    .forUseAtConfigurationTime().orNull?.toBoolean() == true

tasks {
  val generateDoc by creating(Exec::class) {
    group = "documentation"
    commandLine("sh", "gradlew", "dokkaGfm")
  }
  val buildDoc by creating(Exec::class) {
    group = "documentation"
    description = "Generates and validates the documentation"
    dependsOn(generateDoc)
  }

  val undocumentedProjects = if (!enableCompatibilityMetadataVariant) {
    listOf(
      project(":arrow-core-test"),
      project(":arrow-fx-coroutines-test"),
      project(":arrow-optics-test"),
      project(":arrow-optics-ksp-plugin"),
    )
  } else {
    listOf(project(":arrow-optics-ksp-plugin"))
  }

  dokkaGfmMultiModule { removeChildTasks(undocumentedProjects) }
  dokkaHtmlMultiModule { removeChildTasks(undocumentedProjects) }
  dokkaJekyllMultiModule { removeChildTasks(undocumentedProjects) }
}

apiValidation {
  val ignoreApiValidation = if (!enableCompatibilityMetadataVariant) {
    listOf("arrow-optics-ksp-plugin", "arrow-optics-test", "arrow-site")
  } else {
    listOf("arrow-optics-ksp-plugin")
  }

  ignoredProjects.addAll(ignoreApiValidation)
}
