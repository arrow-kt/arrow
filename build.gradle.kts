import org.jetbrains.dokka.gradle.DokkaTask

buildscript {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }
    dependencies {
      classpath(libs.plugin.kotlin)
    }
}

plugins {
  alias(libs.plugins.dokka)
  alias(libs.plugins.animalSniffer) apply false
  alias(libs.plugins.kotest.multiplatform) apply false
  alias(libs.plugins.kotlin.binaryCompatibilityValidator)
  alias(libs.plugins.arrowGradleConfig.nexus)
}

allprojects {
  group = property("projects.group").toString()
}

tasks {
  val generateDoc by creating(Exec::class) {
    group = "documentation"
    commandLine("sh", "gradlew", "dokkaJekyll")
  }
  val buildDoc by creating(Exec::class) {
    group = "documentation"
    description = "Generates and validates the documentation"
    dependsOn(generateDoc)
  }
}

configure(subprojects - project(":arrow-site")) {
  apply(plugin = "org.jetbrains.dokka")

  val ank: Configuration by configurations.creating

  dependencies {
    dokkaGfmPlugin("io.arrow-kt:arrow-ank-dokka-plugin:0.5.2-alpha.1")
    ank(project(this@configure.path))
  }

  tasks.named<DokkaTask>("dokkaGfm") {
    outputDirectory.set(file("$rootDir/arrow-site/docs/apidocs"))

    dependsOn("assemble")
    dokkaSourceSets {
      val arrowMetaBlobMain = "https://github.com/arrow-kt/arrow-meta/blob/main"

      configureEach {
        skipDeprecated.set(true)
        reportUndocumented.set(true)
        sourceRoots.filter { it.path.contains(file("test/").path, ignoreCase = true) }
          .forEach {
            val file = it.relativeTo(projectDir)
            sourceLink {
              localDirectory.set(file)
              remoteUrl.set(
                uri("$arrowMetaBlobMain/$file").toURL()
              )
              remoteLineSuffix.set("#L")
            }
          }
        classpath.from(ank.files)
      }
    }
  }

}
