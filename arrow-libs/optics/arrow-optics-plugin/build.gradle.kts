plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id("java-gradle-plugin")
  id("com.github.gmazzo.buildconfig") version "5.7.0"
  id("com.diffplug.spotless")
  // id("com.vanniktech.maven.publish.base")
  id("com.gradle.plugin-publish") version "2.0.0"
}

group = property("projects.group").toString()

repositories {
  gradlePluginPortal()
}

kotlin {
  explicitApi = null
  compilerOptions {
    optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
  }
}

dependencies {
  compileOnly(kotlin("compiler"))
  implementation(kotlin("gradle-plugin-api"))
  implementation(kotlin("gradle-plugin"))
  implementation(projects.arrowOpticsKspPlugin)
  implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${libs.versions.kspVersion.get()}")
}

buildConfig {
  packageName("arrow.optics.plugin")

  buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"arrow.optics.plugin\"")

  buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${project.group}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${project.name}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${project.version}\"")

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
      id = "arrow.optics.plugin"
      displayName = "ArrowOpticsPlugin"
      description = "Arrow Optics for Kotlin (Multiplatform)"
      implementationClass = "arrow.optics.plugin.ArrowOpticsPlugin"
      tags = listOf("arrow", "optics", "kotlin")
    }
  }
}

/*
mavenPublishing {
  configureBasedOnAppliedPlugins()
  pomFromGradleProperties()
  if (project.findProperty("onlyLocal")?.toString()?.toBooleanStrict() != true) {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
  }
}

 */

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
