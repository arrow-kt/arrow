package arrow.gradle

import java.io.File
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.GradleDokkaSourceSetBuilder

class DocumentationPlugin : Plugin<Project> {
  override fun apply(target: Project) {}
}

fun Project.setupDokka(
  outputDirectory: File,
  baseUrl: String,
  name: String? = null,
  paths: List<String> = emptyList(),
  noInternal: Boolean = true,
  noDeprecated: Boolean = true
) {
  tasks.withType<DokkaTask>().configureEach {
    this@configureEach.outputDirectory by outputDirectory
    moduleName by (name ?: this@setupDokka.name)

    dokkaSourceSets.apply {
      if (file("src/commonMain/kotlin").exists()) {
        setup(this@setupDokka, "commonMain", baseUrl, paths, noInternal, noDeprecated)
      } else if (file("src/main/kotlin").exists()) {
        setup(this@setupDokka, "main", baseUrl, paths, noInternal, noDeprecated)
      }
      if (file("src/jvmMain/kotlin").exists()) {
        setup(this@setupDokka, "jvmMain", baseUrl, paths, noInternal, noDeprecated)
      }
      if (file("src/jsMain/kotlin").exists()) {
        setup(this@setupDokka, "jsMain", baseUrl, paths, noInternal, noDeprecated)
      }
      if (file("src/nativeMain/kotlin").exists()) {
        setup(this@setupDokka, "nativeMain", baseUrl, paths, noInternal, noDeprecated)
      }
      // Should we add whole matrix of targets for native here too ???
      // Or can we figure out an easier way to do it by iterating over the targets?
    }
  }
}

fun NamedDomainObjectContainer<GradleDokkaSourceSetBuilder>.setup(
  project: Project,
  mainDir: String,
  baseUrl: String,
  paths: List<String>,
  noInternal: Boolean,
  noDeprecated: Boolean
) {
  named(mainDir) {
    perPackageOption {
      // will match all .internal packages and sub-packages
      matchingRegex.set(".*\\.internal.*")
      suppress by !noInternal
    }
    skipDeprecated by noDeprecated
    includes.from(paths)
    sourceLink {
      localDirectory.set(project.file("src/$mainDir/kotlin"))
      remoteUrl.set(project.uri("$baseUrl/${project.relativeProjectPath("src/$mainDir/kotlin")}").toURL())
      remoteLineSuffix.set("#L")
    }
  }
}
