plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id("java-gradle-plugin")
  id("com.github.gmazzo.buildconfig") version "5.7.1"
  id("com.diffplug.spotless")
  // id("com.vanniktech.maven.publish.base")
  id("com.gradle.plugin-publish") version "2.0.0"
}

group = property("projects.group").toString()

repositories {
  gradlePluginPortal()
}

kotlin {
  explicitApi()
  compilerOptions {
    optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
  }
}

dependencies {
  compileOnly(kotlin("compiler"))
  implementation(kotlin("gradle-plugin-api"))
  implementation(kotlin("gradle-plugin"))
  implementation(projects.arrowOpticsKspPlugin)
  implementation(projects.arrowOpticsCompilerPlugin)
  implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${libs.versions.kspVersion.get()}")
}

buildConfig {
  packageName("arrow.optics.plugin")

  val compilerPluginProject = project(":arrow-optics-compiler-plugin")
  buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${compilerPluginProject.group}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${compilerPluginProject.name}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${compilerPluginProject.version}\"")

  val kspPluginProject = project(":arrow-optics-ksp-plugin")
  buildConfigField(
    type = "String",
    name = "KSP_PLUGIN_LIBRARY_COORDINATES",
    expression = "\"${kspPluginProject.group}:${kspPluginProject.name}:${kspPluginProject.version}\""
  )

  val annotationsProject = project(":arrow-annotations")
  buildConfigField(
    type = "String",
    name = "ANNOTATIONS_LIBRARY_COORDINATES",
    expression = "\"${annotationsProject.group}:${annotationsProject.name}:${annotationsProject.version}\""
  )

  val opticsProject = project(":arrow-optics")
  buildConfigField(
    type = "String",
    name = "OPTICS_LIBRARY_COORDINATES",
    expression = "\"${opticsProject.group}:${opticsProject.name}:${opticsProject.version}\""
  )
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
