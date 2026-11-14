import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  pluginDevKit("compiler-plugin")
  id("com.diffplug.spotless")
  id("com.vanniktech.maven.publish.base")
}

group = property("projects.group").toString()

pluginDevKit {
  // testDataLibraries { /* `register`ing conditional libraries */ }
  componentRegistrar = "arrow.optics.plugin.fir.OpticsPluginComponentRegistrar"
  commandLineProcessor = "arrow.optics.plugin.fir.OpticsCommandLineProcessor"
  generateTestsEnabled = false

  pluginPackage = "arrow.optics.plugin"

  defaultTestVersion("2.4.21")
  cliVersions(min = "2.4.0", max = null, org.jetbrains.kotlin.compiler.plugin.devkit.BetaAndRc.LATEST)
  ideaVersions(min = "261", max = null, includeRc = true, includeEap = true)
}

kotlin {
  sourceSets {
    commonMain {
      kotlin.setSrcDirs(listOf("src/main"))
    }
    commonTest {
      kotlin.setSrcDirs(listOf("src/test"))

      dependencies {
        implementation(kotlin("test"))
        implementation(libs.kotest.assertionsCore)
        implementation(libs.classgraph)
        implementation(kotlin("serialization-compiler-plugin-embeddable"))
        implementation(libs.kotlinCompileTesting.get().toString()) {
          exclude(
            group = libs.classgraph.get().module.group,
            module = libs.classgraph.get().module.name
          )
          exclude(
            group = "org.jetbrains.kotlin",
            module = "kotlin-stdlib"
          )
        }
        runtimeOnly(projects.arrowAnnotations)
        runtimeOnly(projects.arrowOptics)
        runtimeOnly(projects.arrowCore)
      }
    }
  }

  compilerOptions {
    apiVersion = KotlinVersion.KOTLIN_2_2
    languageVersion = KotlinVersion.KOTLIN_2_2
    freeCompilerArgs.addAll(
      "-Xreport-all-warnings",
      "-Xrender-internal-diagnostic-names",
      "-Xreturn-value-checker=full",
      "-Xexpect-actual-classes",
      "-Xcontext-parameters",
      "-Xallow-holdsin-contract",
      "-Xcollection-literals",
      "-Xwarning-level=ERROR_SUPPRESSION:disabled",
      "-Xwarning-level=NOTHING_TO_INLINE:disabled",
      "-Xwarning-level=DSL_MARKER_APPLIED_TO_WRONG_TARGET:disabled",
      "-Xwarning-level=RETURN_VALUE_NOT_USED_COERCION:disabled",
    )
    // required to be part of the Kotlin User Projects repository
    providers.gradleProperty("kotlin_language_version").ifAvailable { languageVersion = KotlinVersion.fromVersion(it) }
    providers.gradleProperty("kotlin_api_version").ifAvailable { apiVersion = KotlinVersion.fromVersion(it) }
    providers.gradleProperty("kotlin_additional_cli_options").ifAvailable { freeCompilerArgs.addAll(it.split(" ")) }
  }
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
  kotlin {
    ktlint().editorConfigOverride(mapOf(
      "ktlint_standard_filename" to "disabled",
      "ktlint_standard_backing-property-naming" to "disabled",
      "ktlint_standard_property-naming" to "disabled"
    ))
  }
}

configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
  configureBasedOnAppliedPlugins()
  pomFromGradleProperties()
  if (project.findProperty("onlyLocal")?.toString()?.toBooleanStrict() != true) {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
  }
}

if (project.findProperty("onlyLocal")?.toString()?.toBooleanStrict() == true) {
  configure<PublishingExtension> {
    repositories {
      maven {
        name = "localPluginRepository"
        url = uri("${rootProject.projectDir.absolutePath}/build/local-plugin-repository")
      }
    }
  }
}

val javaToolchains  = project.extensions.getByType<JavaToolchainService>()
tasks.withType<Test>().configureEach {
  maxParallelForks = 1
  useJUnitPlatform()
  systemProperty("arrowVersion", project(":arrow-optics").version.toString())
}

fun Provider<String>.ifAvailable(block: (String) -> Unit) =
  orNull?.takeIf(String::isNotBlank)?.also(block)
