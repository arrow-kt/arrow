@file:Suppress("DSL_SCOPE_VIOLATION")

import kotlinx.knit.KnitPluginExtension
import kotlinx.validation.ExperimentalBCVApi
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

allprojects {
  if (property("version") == "unspecified") {
    setProperty("version", "2.0.0-SNAPSHOT")
  }
}

buildscript {
  repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }

  dependencies {
    classpath(libs.kotlinx.knit)
  }
}

allprojects {
  repositories {
    mavenCentral()
    (project.rootProject.properties["kotlin_repo_url"] as? String)?.also { maven(it) }
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
}

plugins {
  base
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.dokka)
  alias(libs.plugins.animalSniffer) apply false
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.kotlinx.serialization) apply false
  alias(libs.plugins.kotlin.binaryCompatibilityValidator)
  alias(libs.plugins.spotless) apply false
  alias(libs.plugins.publish) apply false
  alias(libs.plugins.compose.jetbrains) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.kotlinx.knit)
}

configure<KnitPluginExtension> {
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

dependencies {
  kover(projects.arrowAtomic)
  kover(projects.arrowAutoclose)
  kover(projects.arrowCore)
  kover(projects.arrowCoreHighArity)
  kover(projects.arrowCoreRetrofit)
  kover(projects.arrowCoreSerialization)
  kover(projects.arrowCache4k)
  kover(projects.arrowFunctions)
  kover(projects.arrowFxCoroutines)
  kover(projects.arrowFxStm)
  kover(projects.arrowOptics)
  kover(projects.arrowOpticsKspPlugin)
  kover(projects.arrowOpticsReflect)
  kover(projects.arrowOpticsCompose)
  kover(projects.arrowResilience)
  kover(projects.arrowCollectors)
  kover(projects.arrowEval)
}

allprojects {
  group = property("projects.group").toString()
}

private val kotlinXUpstream =
  setOf(
    "arrow-fx-coroutines",
    "arrow-resilience",
    "arrow-fx-stm",
    "arrow-collectors"
  )

subprojects {
  plugins.apply("org.jetbrains.dokka")

  tasks.withType<DokkaTaskPartial>().configureEach {
    extensions.findByType<KotlinProjectExtension>()?.sourceSets?.forEach { kotlinSourceSet ->
      dokkaSourceSets.named(kotlinSourceSet.name) {
        perPackageOption {
          matchingRegex.set(".*\\.internal.*")
          suppress.set(true)
        }
        if (project.name in kotlinXUpstream) externalDocumentationLink("https://kotlinlang.org/api/kotlinx.coroutines/")
        skipDeprecated.set(true)
        reportUndocumented.set(false)

        kotlinSourceSet.kotlin.srcDirs.filter { it.exists() }.forEach { srcDir ->
          sourceLink {
            localDirectory.set(srcDir)
remoteUrl.set(uri("https://github.com/arrow-kt/arrow/blob/main/${srcDir.relativeTo(rootProject.rootDir)}").toURL())
            remoteLineSuffix.set("#L")
          }
        }
      }
    }
  }
}

tasks {
  val undocumentedProjects =
    listOf(project(":arrow-optics-ksp-plugin"))

  val copyCNameFile = register<Copy>("copyCNameFile") {
    from(layout.projectDirectory.dir("static").file("CNAME"))
    into(layout.projectDirectory.dir("docs"))
  }

  dokkaHtmlMultiModule {
    dependsOn(copyCNameFile)
    removeChildTasks(undocumentedProjects)
  }

  getByName("knitPrepare").dependsOn(getTasksByName("dokka", true))

  withType<DokkaMultiModuleTask>().configureEach {
    outputDirectory.set(file("docs"))
    moduleName.set("Arrow")
  }
}

apiValidation {
  ignoredProjects.add("arrow-optics-ksp-plugin")
  @OptIn(ExperimentalBCVApi::class)
  klib.enabled = true
}
