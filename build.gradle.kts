@file:Suppress("DSL_SCOPE_VIOLATION")

import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

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
  alias(libs.plugins.kotlinx.kover)
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

koverMerged {
  enable()
  filters {
    projects {
      excludes.addAll(
        listOf(
          ":arrow-annotations",
          ":arrow-site",
          ":arrow-stack",
          ":arrow-optics-ksp-plugin"
        )
      )
    }
  }
}

allprojects {
  group = property("projects.group").toString()
}

val enableCompatibilityMetadataVariant =
  providers.gradleProperty("kotlin.mpp.enableCompatibilityMetadataVariant")
    .orNull?.toBoolean() == true

subprojects {
  this@subprojects.tasks.withType<DokkaTaskPartial>().configureEach {
    this@subprojects.extensions.findByType<KotlinProjectExtension>()?.sourceSets?.forEach { kotlinSourceSet ->
      dokkaSourceSets.named(kotlinSourceSet.name) {
        perPackageOption {
          matchingRegex.set(".*\\.internal.*")
          suppress.set(true)
        }
        if (project.name == "arrow-fx-coroutines") externalDocumentationLink("https://kotlinlang.org/api/kotlinx.coroutines/")
        skipDeprecated.set(true)
        reportUndocumented.set(false)
        val baseUrl: String = checkNotNull(properties["pom.smc.url"]?.toString())

        kotlinSourceSet.kotlin.srcDirs.filter { it.exists() }.forEach { srcDir ->
          sourceLink {
            localDirectory.set(srcDir)
            remoteUrl.set(uri("$baseUrl/blob/main/${srcDir.relativeTo(rootProject.rootDir)}").toURL())
            remoteLineSuffix.set("#L")
          }
        }
      }
    }
  }
}

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

  val undocumentedProjects =
    listOf(project(":arrow-optics-ksp-plugin"))

  dokkaGfmMultiModule {
    dependsOn("copyCNameFile")
    removeChildTasks(undocumentedProjects)
  }
  dokkaHtmlMultiModule {
    dependsOn("copyCNameFile")
    removeChildTasks(undocumentedProjects)
  }
  dokkaJekyllMultiModule {
    dependsOn("copyCNameFile")
    removeChildTasks(undocumentedProjects)
  }

  getByName("knitPrepare").dependsOn(getTasksByName("dokka", true))

  withType<DokkaMultiModuleTask>().configureEach {
    outputDirectory.set(docFolder())
    moduleName.set("Arrow")
  }

  register<Delete>("cleanDocs") {
    val folder = docFolder()
    val content = folder.listFiles()?.filter { it != folder }
    delete(content)
  }

  register<Copy>("copyCNameFile") {
    from(layout.projectDirectory.dir("static").file("CNAME"))
    into(layout.projectDirectory.dir("docs"))
  }
}

fun docFolder(): File =
  project.properties["githubpages"]?.let { file("docs").also { it.mkdir() } } ?: rootDir.resolve("arrow-site/docs/apidocs")

apiValidation {
  val ignoreApiValidation = if (!enableCompatibilityMetadataVariant) {
    listOf("arrow-optics-ksp-plugin", "arrow-site")
  } else {
    listOf("arrow-optics-ksp-plugin")
  }

  ignoredProjects.addAll(ignoreApiValidation)
}

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
  rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().apply {
    versions.webpackDevServer.version = "4.11.1"
    versions.webpack.version = "5.75.0"
    versions.webpackCli.version = "4.10.0"
    versions.karma.version = "6.4.1"
    versions.mocha.version = "10.2.0"
  }
}
