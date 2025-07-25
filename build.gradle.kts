import kotlinx.knit.KnitPluginExtension
import kotlinx.validation.ExperimentalBCVApi
import org.gradle.internal.classpath.Instrumented.systemProperty

allprojects {
  val version = (property("version") as? String).let { version ->
    if (version == null || version == "unspecified") "2.2.0-SNAPSHOT"
    else version
  }
  setProperty("version", version)
  systemProperty("arrowVersion", version)
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

plugins {
  base
  id(libs.plugins.kotlin.multiplatform.get().pluginId) apply false
  id(libs.plugins.android.kmp.get().pluginId) apply false
  id(libs.plugins.spotless.get().pluginId) apply false
  id(libs.plugins.animalSniffer.get().pluginId) apply false
  id(libs.plugins.dokka.get().pluginId)
  alias(libs.plugins.kotlinx.serialization) apply false
  alias(libs.plugins.publish) apply false
  alias(libs.plugins.compose.jetbrains) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.kotlinx.knit)
  id(libs.plugins.kotlinx.kover.get().pluginId)
  alias(libs.plugins.kotlin.binaryCompatibilityValidator)
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

tasks.getByName("knitPrepare") {
  dependsOn(tasks.dokkaGenerate)
}

dependencies {
  kover(projects.arrowAtomic)
  kover(projects.arrowAutoclose)
  kover(projects.arrowCore)
  kover(projects.arrowCoreHighArity)
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
  kover(projects.arrowCoreJackson)
  kover(projects.arrowCoreRetrofit)
  kover(projects.arrowCoreSerialization)
  kover(projects.arrowResilienceKtorClient)
  kover(projects.arrowRaiseKtorServer)
  kover(projects.suspendapp)
  kover(projects.suspendappKtor)
}

dependencies {
  dokka(projects.arrowAnnotations)
  dokka(projects.arrowAtomic)
  dokka(projects.arrowAutoclose)
  dokka(projects.arrowCore)
  dokka(projects.arrowCoreHighArity)
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
  dokka(projects.arrowCoreJackson)
  dokka(projects.arrowCoreRetrofit)
  dokka(projects.arrowCoreSerialization)
  dokka(projects.arrowResilienceKtorClient)
  dokka(projects.arrowRaiseKtorServer)
  dokka(projects.suspendapp)
  dokka(projects.suspendappKtor)
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

apiValidation {
  ignoredProjects.addAll(listOf(
    "arrow-optics-ksp-plugin",
    "suspendapp-test-app",
    "suspendapp-test-runner",
  ))
  @OptIn(ExperimentalBCVApi::class)
  klib.enabled = true
}
