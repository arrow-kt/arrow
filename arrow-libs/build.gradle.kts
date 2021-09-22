import arrow.gradle.setupDokka
import arrow.gradle.setupPublishing

buildscript {
  apply(from = "gradle/setup.gradle")
  repositories {
    mavenCentral()
  }
}

plugins {
  id("org.jlleitschuh.gradle.ktlint") version "10.1.0" apply false
  id("ru.vyarus.animalsniffer") version "1.5.0" apply false
  id("io.kotest.multiplatform") version "5.0.0.5" apply false
  id("org.jetbrains.kotlin.multiplatform") version "1.5.31" apply false
  id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.7.1"

  id("documentation")
  id("org.jetbrains.dokka") version "1.5.0" apply false

  id("mpp-publish")
  id("maven-publish")
  id("signing")
}

apply(from = "gradle/main.gradle")

tasks {
  val generateDoc by creating(Exec::class) {
    group = "documentation"
    commandLine("sh", "gradlew", "dokkaGfm")
  }
  val runValidation by creating(Exec::class) {
    group = "documentation"
    workingDir = file("../arrow-site")
    commandLine("sh", "gradlew", "dokkaGfm")
  }
  val buildDoc by creating(Exec::class) {
    group = "documentation"
    description = "Generates and validates the documentation"
    dependsOn(generateDoc)
    dependsOn(runValidation)
  }

  runValidation.mustRunAfter(generateDoc)
}

apiValidation {
  ignoredProjects.add("jekyll")
}

val excludeDocs = setOf(
  "arrow-ank-gradle",
  "arrow-core-test",
  "arrow-fx-coroutines-test",
  "arrow-meta",
  "arrow-optics-test",
  "examples",
  "jekyll",
  "arrow-meta-test-models",
)

configure(
  subprojects.filter { it.name !in excludeDocs }
) {
  apply(plugin = "org.jetbrains.dokka")

  setupDokka(
    outputDirectory = file("${rootDir}/../arrow-site/docs/apidocs"),
    baseUrl = "https://github.com/arrow-kt/arrow/blob/main"
  )

  dependencies {
    // Really Gradle !?
    "dokkaGfmPlugin"(project(":jekyll"))
  }
}

val excludeMppPublish = setOf(
  "arrow-ank",
  "arrow-ank-gradle",
  "arrow-core-retrofit",
  "arrow-meta",
  "examples",
  "jekyll",
  "arrow-meta-test-models",
)

val POM_DEVELOPER_ID: String by project
val POM_DEVELOPER_NAME: String by project
val POM_DESCRIPTION: String by project
val POM_URL: String by project
val SNAPSHOT_REPOSITORY: String by project
val RELEASE_REPOSITORY: String by project

configure(
  subprojects.filter { it.name !in excludeMppPublish }
) {
  apply(plugin = "maven-publish")
  apply(plugin = "signing")

  setupPublishing(
    pomDevId = POM_DEVELOPER_ID,
    pomDevName = POM_DEVELOPER_NAME,
    releaseRepo = uri(RELEASE_REPOSITORY),
    snapshotRepo = uri(SNAPSHOT_REPOSITORY),
    projectUrl = POM_URL,
    projectDesc = POM_DESCRIPTION,
    sonatypeUsername = System.getenv("SONATYPE_USER"),
    sonatypePassword = System.getenv("SONATYPE_PWD"),
    key = findProperty("signingKey") as? String,
    pass = findProperty("signingPassword") as? String
  )
}
