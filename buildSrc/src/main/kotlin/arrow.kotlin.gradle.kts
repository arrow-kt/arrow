import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.net.URI
import java.time.Duration
import groovy.util.Node
import groovy.util.NodeList
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

repositories {
  mavenCentral()
  providers.gradleProperty("kotlin_repo_url").orNull?.also { maven(it) }
  google()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

group = property("projects.group").toString()
val projectNameWithDots = project.name.replace('-', '.')

val Project.withoutAndroid
  get() = project.name == "suspendapp"

val Project.isKotlinJvm: Boolean
  get() = pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")

val Project.isKotlinMultiplatform: Boolean
  get() = pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")

if (!isKotlinJvm) {
  plugins.apply("org.jetbrains.kotlin.multiplatform")
  if (!withoutAndroid) plugins.apply("com.android.library")
}
plugins.apply("com.diffplug.spotless")
plugins.apply("ru.vyarus.animalsniffer")
plugins.apply("org.jetbrains.dokka")
plugins.apply("org.jetbrains.kotlinx.kover")
plugins.apply("signing")
plugins.apply("maven-publish")

val doNotPublish = listOf("arrow-raise-ktor-server")
if (project.name !in doNotPublish)
  plugins.apply("com.vanniktech.maven.publish")

tasks {
  withType<Test>().configureEach {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
    useJUnitPlatform()
    testLogging {
      setExceptionFormat("full")
      setEvents(listOf("passed", "skipped", "failed", "standardOut", "standardError"))
    }
  }
}

configure<KotlinProjectExtension> {
  explicitApi()
}

configure<JavaPluginExtension> {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

fun Provider<String>.ifAvailable(block: (String) -> Unit) =
  orNull?.takeIf(String::isNotBlank)?.also(block)

fun KotlinCommonCompilerOptions.commonCompilerOptions() {
  // required to be part of the Kotlin User Projects repository
  providers.gradleProperty("kotlin_language_version").ifAvailable { languageVersion = KotlinVersion.fromVersion(it) }
  providers.gradleProperty("kotlin_api_version").ifAvailable { apiVersion = KotlinVersion.fromVersion(it) }
  providers.gradleProperty("kotlin_additional_cli_options").ifAvailable { freeCompilerArgs.addAll(it.split(" ")) }
  freeCompilerArgs.addAll(
    "-Xreport-all-warnings",
    "-Xrender-internal-diagnostic-names",
    "-Wextra",
    "-Xuse-fir-experimental-checkers",
  )
}

if (isKotlinMultiplatform) {
  configure<KotlinMultiplatformExtension> {
    compilerOptions { commonCompilerOptions() }

    jvmToolchain { languageVersion.set(JavaLanguageVersion.of(11)) }

    jvm {
      compilerOptions {
        jvmTarget = JvmTarget.JVM_11
      }
      tasks.named<Jar>("jvmJar") {
        manifest {
          attributes["Automatic-Module-Name"] = projectNameWithDots
        }
      }
    }

    js(IR) {
      nodejs {
        testTask {
          useMocha {
            timeout = "300s"
          }
        }
      }
      browser {
        testTask {
          useKarma {
            useChromeHeadless()
            timeout.set(Duration.ofMinutes(5))
          }
        }
      }
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
      browser()
      nodejs()
      d8 {
        testTask {
          timeout.set(Duration.ofMinutes(5))
        }
      }
    }

    if (!withoutAndroid) androidTarget()

    // Native: https://kotlinlang.org/docs/native-target-support.html
    // -- Tier 1 --
    linuxX64()
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()
    // -- Tier 2 --
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    if (project.name != "arrow-cache4k") watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()
    // -- Tier 3 --
    mingwX64()
    // Android Native and watchOS not included

    applyDefaultHierarchyTemplate()

    sourceSets {
      val nonJvmMain by creating { dependsOn(commonMain.get()) }
      val nonJvmTest by creating { dependsOn(commonTest.get()) }

      nativeMain.get().dependsOn(nonJvmMain)
      nativeTest.get().dependsOn(nonJvmTest)

      jsMain.get().dependsOn(nonJvmMain)
      jsTest.get().dependsOn(nonJvmTest)

      wasmJsMain.get().dependsOn(nonJvmMain)
      wasmJsTest.get().dependsOn(nonJvmTest)

      if (!withoutAndroid) {
        val androidAndJvmMain by creating { dependsOn(commonMain.get()) }
        jvmMain.get().dependsOn(androidAndJvmMain)
        androidMain.get().dependsOn(androidAndJvmMain)
      }

      commonMain {
        dependencies {
          implementation(kotlin("stdlib"))
        }
      }

      commonTest {
        dependencies {
          implementation(kotlin("test"))
        }
      }
    }
  }
}

if (isKotlinJvm) {
  configurations.all { resolutionStrategy.cacheChangingModulesFor(0, "seconds") }

  tasks.named<Jar>("jar") {
    manifest {
      attributes["Automatic-Module-Name"] = projectNameWithDots
    }
  }

  configure<KotlinJvmExtension> {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_11
      commonCompilerOptions()
    }
  }
}

if (pluginManager.hasPlugin("com.android.library")) {
  configure<com.android.build.gradle.LibraryExtension> {
    namespace = projectNameWithDots
    compileSdk = 35
    defaultConfig {
      minSdk = 24
    }
  }
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
  extensions.findByType<KotlinProjectExtension>()?.sourceSets?.forEach { kotlinSourceSet ->
    dokkaSourceSets.named(kotlinSourceSet.name) {
      perPackageOption {
        matchingRegex.set(".*\\.internal.*")
        suppress.set(true)
      }
      externalDocumentationLink {
        url.set(URI("https://kotlinlang.org/api/kotlinx.serialization/").toURL())
      }
      externalDocumentationLink {
        url.set(URI("https://kotlinlang.org/api/kotlinx.coroutines/").toURL())
      }
      skipDeprecated.set(true)
      reportUndocumented.set(false)

      kotlinSourceSet.kotlin.srcDirs.filter { it.exists() }.forEach { srcDir ->
        sourceLink {
          localDirectory.set(srcDir)
          remoteUrl.set(URI("https://github.com/arrow-kt/arrow/blob/main/${srcDir.relativeTo(rootProject.rootDir)}").toURL())
          remoteLineSuffix.set("#L")
        }
      }
    }
  }
}

configure<ru.vyarus.gradle.plugin.animalsniffer.AnimalSnifferExtension> {
  // ignore("java.lang.*")
}

val signature by configurations.getting
dependencies {
  signature("org.codehaus.mojo.signature:java18:1.0@signature")
  if (isKotlinMultiplatform && !withoutAndroid) {
    signature("net.sf.androidscents.signature:android-api-level-24:7.0_r2@signature")
  }
}

val publications = extensions.findByType(PublishingExtension::class.java)?.publications

configure<SigningExtension> {
  val signingKeyId = project.getVariable("signingInMemoryKeyId", "SONATYPE_SIGNING_KEY_ID")
  val signingKey = project.getVariable("signingInMemoryKey", "SONATYPE_SIGNING_KEY")
  val signingPassword = project.getVariable("signingInMemoryKeyPassword", "SONATYPE_SIGNING_KEY_PASSPHRASE")
  useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
  sign(publications)
}

afterEvaluate {
  val publications = publications ?: return@afterEvaluate
  val platformPublication: MavenPublication? = publications.findByName("jvm") as? MavenPublication

  if (platformPublication != null && isKotlinMultiplatform) {
    lateinit var platformXml: XmlProvider
    platformPublication.pom?.withXml { platformXml = this }

    (publications.findByName("kotlinMultiplatform") as? MavenPublication)?.run {
      // replace pom
      pom.withXml {
        val xmlProvider = this
        val root = xmlProvider.asNode()
        // Remove the original content and add the content from the platform POM:
        root.children().toList().forEach { root.remove(it as Node) }
        platformXml.asNode().children().forEach { root.append(it as Node) }

        // Adjust the self artifact ID, as it should match the root module's coordinates:
        ((root.get("artifactId") as NodeList).get(0) as Node).setValue(artifactId)

        // Set packaging to POM to indicate that there's no artifact:
        root.appendNode("packaging", "pom")

        // Remove the original platform dependencies and add a single dependency on the platform
        // module:
        val dependencies = (root.get("dependencies") as NodeList).get(0) as Node
        dependencies.children().toList().forEach { dependencies.remove(it as Node) }
        val singleDependency = dependencies.appendNode("dependency")
        singleDependency.appendNode("groupId", platformPublication.groupId)
        singleDependency.appendNode("artifactId", platformPublication.artifactId)
        singleDependency.appendNode("version", platformPublication.version)
        singleDependency.appendNode("scope", "compile")
      }
    }

    tasks
      .matching { it.name == "generatePomFileForKotlinMultiplatformPublication" }
      .configureEach {
        dependsOn(
          "generatePomFileFor${platformPublication.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}Publication"
        )
      }
  }
}

internal fun Project.getVariable(propertyName: String, environmentVariableName: String): String? {
  val property: String? = project.properties[propertyName]?.toString()
  val environmentVariable: String? = System.getenv(environmentVariableName)
  val isPublish: Boolean = gradle.startParameter.taskNames.any { it.startsWith("publish") }
  require(!property.isNullOrBlank() || !environmentVariable.isNullOrBlank() || !isPublish)
  return property ?: environmentVariable
}

