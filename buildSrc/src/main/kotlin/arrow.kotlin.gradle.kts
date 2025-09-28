import com.android.build.api.dsl.androidLibrary
import groovy.util.Node
import groovy.util.NodeList
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationVariantSpec
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import java.time.Duration

repositories {
  mavenCentral()
  providers.gradleProperty("kotlin_repo_url").orNull?.also { maven(it) }
  google()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

group = property("projects.group").toString()
val projectNameWithDots = project.name.replace('-', '.')

val Project.multiplatformWithAndroid
  get() = !project.name.startsWith("suspendapp")

val Project.needsAndroidCoreLibraryDesugaring
  get() = project.name == "arrow-collectors"

val Project.needsJava11
  get() = project.name.endsWith("-compose") || project.name.endsWith("-result4k")

val Project.needsAbiValidation
  get() = project.name !in listOf(
    "arrow-optics-ksp-plugin",
    "suspendapp-test-app",
    "suspendapp-test-runner",
  )

val Project.isKotlinJvm: Boolean
  get() = pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")

val Project.isKotlinMultiplatform: Boolean
  get() = pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")

if (!isKotlinJvm) {
  plugins.apply("org.jetbrains.kotlin.multiplatform")
  if (multiplatformWithAndroid) plugins.apply("com.android.kotlin.multiplatform.library")
}
plugins.apply("com.diffplug.spotless")
plugins.apply("ru.vyarus.animalsniffer")
plugins.apply("org.jetbrains.dokka")
plugins.apply("org.jetbrains.kotlinx.kover")
plugins.apply("com.vanniktech.maven.publish.base")

val javaToolchains  = project.extensions.getByType<JavaToolchainService>()
tasks {
  withType<Test>().configureEach {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
    // always use Java 11, because Kotest requires it
    javaLauncher.set(javaToolchains.launcherFor {
      languageVersion.set(JavaLanguageVersion.of(11))
    })
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
    languageVersion.set(JavaLanguageVersion.of(8))
    targetCompatibility = if (needsJava11) JavaVersion.VERSION_11 else JavaVersion.VERSION_1_8
  }
}

fun Provider<String>.ifAvailable(block: (String) -> Unit) =
  orNull?.takeIf(String::isNotBlank)?.also(block)

fun KotlinCommonCompilerOptions.commonCompilerOptions() {
  apiVersion = KotlinVersion.KOTLIN_2_0
  languageVersion = KotlinVersion.KOTLIN_2_0
  freeCompilerArgs.addAll(
    "-Xreport-all-warnings",
    "-Xrender-internal-diagnostic-names",
  )
  // required to be part of the Kotlin User Projects repository
  providers.gradleProperty("kotlin_language_version").ifAvailable { languageVersion = KotlinVersion.fromVersion(it) }
  providers.gradleProperty("kotlin_api_version").ifAvailable { apiVersion = KotlinVersion.fromVersion(it) }
  providers.gradleProperty("kotlin_additional_cli_options").ifAvailable { freeCompilerArgs.addAll(it.split(" ")) }
}

@OptIn(ExperimentalAbiValidation::class)
fun AbiValidationVariantSpec.commonValidationOptions() {
  filters {
    excluded {
      annotatedWith.addAll(
        "arrow.fx.coroutines.await.ExperimentalAwaitAllApi",
        "arrow.core.raise.ExperimentalRaiseAccumulateApi",
        "arrow.core.raise.ExperimentalTraceApi",
      )
    }
  }
}

if (isKotlinMultiplatform) {
  configure<KotlinMultiplatformExtension> {
    compilerOptions { commonCompilerOptions() }

    @OptIn(ExperimentalAbiValidation::class)
    if (needsAbiValidation) {
      extensions.configure<AbiValidationMultiplatformExtension>("abiValidation") {
        enabled.set(true)
        klib.enabled.set(true)
        commonValidationOptions()
      }
    }

    jvm {
      compilerOptions {
        jvmTarget = if (needsJava11) JvmTarget.JVM_11 else JvmTarget.JVM_1_8
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

    if (multiplatformWithAndroid) {
      androidLibrary {
        namespace = projectNameWithDots
        compileSdk = 36
        minSdk = 21
        compilerOptions {
          jvmTarget = if (needsJava11) JvmTarget.JVM_11 else JvmTarget.JVM_1_8
        }
        withHostTestBuilder {}
      }
    }

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

      if (multiplatformWithAndroid) {
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
      jvmTarget = if (needsJava11) JvmTarget.JVM_11 else JvmTarget.JVM_1_8
      commonCompilerOptions()
    }
  }

  configure<KotlinJvmProjectExtension> {
    @OptIn(ExperimentalAbiValidation::class)
    if (needsAbiValidation) {
      extensions.configure<AbiValidationExtension>("abiValidation") {
        enabled.set(true)
        commonValidationOptions()
      }
    }
  }
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}


configure<org.jetbrains.dokka.gradle.DokkaExtension> {
  extensions.findByType<KotlinProjectExtension>()?.sourceSets?.forEach { kotlinSourceSet ->
    dokkaSourceSets.named(kotlinSourceSet.name) {
      if ("androidAndJvm" in kotlinSourceSet.name) {
        analysisPlatform.set(org.jetbrains.dokka.gradle.engine.parameters.KotlinPlatform.JVM)
      }
      perPackageOption {
        matchingRegex.set(".*\\.internal.*")
        suppress.set(true)
      }
      externalDocumentationLinks.register("kotlinx.serialization") {
        url("https://kotlinlang.org/api/kotlinx.serialization/")
      }
      externalDocumentationLinks.register("kotlinx.coroutines") {
        url("https://kotlinlang.org/api/kotlinx.coroutines/")
      }
      skipDeprecated.set(true)
      reportUndocumented.set(false)

      kotlinSourceSet.kotlin.srcDirs.filter { it.exists() }.forEach { srcDir ->
        sourceLink {
          localDirectory.set(srcDir)
          remoteUrl("https://github.com/arrow-kt/arrow/blob/main/${srcDir.relativeTo(rootProject.rootDir)}")
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
  when {
    !isKotlinMultiplatform || !multiplatformWithAndroid -> { }
    needsAndroidCoreLibraryDesugaring ->
      signature("com.toasttab.android:gummy-bears-api-21:0.12.0:coreLib2@signature")
    else ->
      signature("com.toasttab.android:gummy-bears-api-21:0.12.0@signature")
  }
}

configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
  configureBasedOnAppliedPlugins()
  pomFromGradleProperties()
  publishToMavenCentral(automaticRelease = true)
  signAllPublications()
}

afterEvaluate {
  val publications = extensions.findByType(PublishingExtension::class.java)?.publications ?: return@afterEvaluate
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
