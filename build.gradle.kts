@file:Suppress("DSL_SCOPE_VIOLATION")

import kotlinx.knit.KnitPluginExtension
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
  }

  dependencies {
    classpath(libs.kotlinx.knit)
  }
}

allprojects {
  repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    (project.rootProject.properties["kotlin_repo_url"] as? String)?.also { maven(it) }
  }

  tasks {
    withType<KotlinCompile> {
      kotlinOptions {
        (project.rootProject.properties["kotlin_language_version"] as? String)?.also { languageVersion = it }
        (project.rootProject.properties["kotlin_api_version"] as? String)?.also { apiVersion = it }
      }
    }
  }
}

plugins {
  base
  alias(libs.plugins.dokka)
  alias(libs.plugins.animalSniffer) apply false
  alias(libs.plugins.kotest.multiplatform) apply false
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.kotlinx.serialization) apply false
  alias(libs.plugins.kotlin.binaryCompatibilityValidator)
  alias(libs.plugins.arrowGradleConfig.nexus)
  alias(libs.plugins.spotless) apply false
}

apply(plugin = libs.plugins.kotlinx.knit.get().pluginId)

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
  // commented out projects cause issues in GitHub Actions
  kover(projects.arrowAtomic)
  kover(projects.arrowContinuations)
  kover(projects.arrowCore)
  kover(projects.arrowCoreRetrofit)
  // kover(projects.arrowCoreSerialization)
  kover(projects.arrowFxCoroutines)
  kover(projects.arrowFxStm)
  kover(projects.arrowOptics)
  // kover(projects.arrowOpticsKspPlugin)
  kover(projects.arrowOpticsReflect)
  kover(projects.arrowResilience)
  kover(projects.arrowCollectors)
}

allprojects {
  group = property("projects.group").toString()
}

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

  tasks.withType<AbstractPublishToMaven> {
    dependsOn(tasks.withType<Sign>())
  }
}

tasks {
  val undocumentedProjects =
    listOf(project(":arrow-optics-ksp-plugin"))

  dokkaHtmlMultiModule {
    dependsOn("copyCNameFile")
    removeChildTasks(undocumentedProjects)
  }

  getByName("knitPrepare").dependsOn(getTasksByName("dokka", true))

  withType<DokkaMultiModuleTask>().configureEach {
    outputDirectory.set(file("docs"))
    moduleName.set("Arrow")
  }

  register<Copy>("copyCNameFile") {
    from(layout.projectDirectory.dir("static").file("CNAME"))
    into(layout.projectDirectory.dir("docs"))
  }
}

apiValidation {
  ignoredProjects.add("arrow-optics-ksp-plugin")
}
