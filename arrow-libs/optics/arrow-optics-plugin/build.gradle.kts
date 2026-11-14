plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  pluginDevKit("gradle-plugin")
  id("com.diffplug.spotless")
  id("com.gradle.plugin-publish") version "2.2.1"
}

group = property("projects.group").toString()

repositories {
  gradlePluginPortal()
  maven("https://packages.jetbrains.team/maven/p/compiler-plugin-dev-kit/eap")
}

pluginDevKit {
  compilerPlugin(project(":arrow-optics-compiler-plugin"))
  pluginPackage = "arrow.optics.plugin"
  companionLibrary(project(":arrow-annotations"))
  companionLibrary(project(":arrow-optics"))
}

gradlePlugin {
  website = "https://arrow-kt.io/"
  vcsUrl = "https://github.com/arrow-kt/arrow"

  plugins {
    create("ArrowOpticsPlugin") {
      id = "io.arrow-kt.optics"
      displayName = "ArrowOpticsPlugin"
      description = "Arrow Optics for Kotlin (Multiplatform)"
      implementationClass = "arrow.optics.plugin.ArrowOpticsPlugin"
      tags = listOf("arrow", "optics", "kotlin")
    }
  }
}

if (project.findProperty("onlyLocal")?.toString()?.toBooleanStrict() == true) {
  publishing {
    repositories {
      maven {
        name = "localPluginRepository"
        url = uri("${rootProject.projectDir.absolutePath}/build/local-plugin-repository")
      }
    }
  }
}
