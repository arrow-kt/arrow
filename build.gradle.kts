@file:Suppress("DSL_SCOPE_VIOLATION")

import kotlinx.knit.KnitPluginExtension
import kotlinx.validation.ExperimentalBCVApi
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.net.URL

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
  id(libs.plugins.kotlin.multiplatform.get().pluginId) apply false
  id(libs.plugins.android.library.get().pluginId) apply false
  alias(libs.plugins.dokka)
  alias(libs.plugins.animalSniffer) apply false
  alias(libs.plugins.kotlinx.kover)
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

dependencies {
  dokka(projects.arrowAnnotations)
  dokka(projects.arrowAtomic)
  dokka(projects.arrowAutoclose)
  dokka(projects.arrowCore)
  dokka(projects.arrowCoreHighArity)
  dokka(projects.arrowCoreRetrofit)
  dokka(projects.arrowCoreSerialization)
  dokka(projects.arrowCache4k)
  dokka(projects.arrowEval)
  dokka(projects.arrowFunctions)
  dokka(projects.arrowPlatform)
  dokka(projects.arrowFxCoroutines)
  dokka(projects.arrowFxStm)
  dokka(projects.arrowCollectors)
  dokka(projects.arrowOptics)
  dokka(projects.arrowOpticsReflect)
  dokka(projects.arrowOpticsCompose)
  dokka(projects.arrowResilience)
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
            remoteUrl.set(URL("https://github.com/arrow-kt/arrow/blob/main/${srcDir.relativeTo(rootProject.rootDir)}"))
            remoteLineSuffix.set("#L")
          }
        }
      }
    }
  }
}

dokka {
  dokkaPublications.html {
    outputDirectory.set(layout.projectDirectory.dir("docs"))
  }
  moduleName.set("Arrow")
  pluginsConfiguration.html {
    customAssets.from("static/img/logo/logo-icon.svg")
    footerMessage.set("© Arrow Contributors")
  }
}

tasks.getByName("knitPrepare") {
  dependsOn(tasks.dokkaGenerate)
}

apiValidation {
  ignoredProjects.add("arrow-optics-ksp-plugin")
  @OptIn(ExperimentalBCVApi::class)
  klib.enabled = true
}
