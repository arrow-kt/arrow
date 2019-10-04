package arrow.meta.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * The project-level Gradle plugin behavior that is used specifying the plugin's configuration through the
 * [ArrowExtension] class.
 * revisit [org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension] and [MultiplatformPlugin] from Spek to move forward for Mpp
 */
class ArrowGradlePlugin : Plugin<Project> {
  companion object {
    fun isEnabled(project: Project): Boolean = project.plugins.findPlugin(ArrowGradlePlugin::class.java) != null

    fun getArrowExtension(project: Project): ArrowExtension {
      return project.extensions.getByType(ArrowExtension::class.java)
    }
  }

  override fun apply(project: Project): Unit {
    project.extensions.create("arrow", ArrowExtension::class.java)
    project.afterEvaluate { p ->
      p.tasks.withType(KotlinCompile::class.java).configureEach {
        it.kotlinOptions.freeCompilerArgs += "-Xplugin=${project.rootDir}/modules/meta/arrow-meta-prototype/compiler-plugin/build/libs/compiler-plugin.jar"
      }
    }
  }
}
