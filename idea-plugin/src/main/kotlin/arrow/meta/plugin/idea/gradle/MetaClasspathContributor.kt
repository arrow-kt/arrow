package arrow.meta.plugin.idea.gradle

import arrow.meta.MetaCliProcessor
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.kotlin.cli.common.arguments.CommonCompilerArguments
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import java.io.File
import java.io.FileInputStream
import java.util.jar.JarInputStream

internal object MetaClasspathContributor {
  /**
   * The path to the IntelliJ compatible compiler plugin version.
   */
  private val PLUGIN_JPS_JAR: String?
    get() = PathManager.getJarPathForClass(MetaCliProcessor::class.java)

  /**
   * The [Logger] instance for this class.
   */
  private val logger = Logger.getInstance(MetaClasspathContributor::class.java)

  /**
   * Modify the compiler arguments for the specified [KotlinFacet] to remove the incompatible compiler plugin from
   * the classpath and replace it with an IntelliJ compatible version.
   *
   * @param facet The facet to modify.
   * @param buildSystemPluginJar The name of the jar file to remove rom the classpath.
   */
  fun modifyCompilerArguments(facet: KotlinFacet, buildSystemPluginJar: String) {
    logger.info("Probing for Gradle plugin")

    val facetSettings = facet.configuration.settings
    val commonArguments = facetSettings.compilerArguments ?: CommonCompilerArguments.DummyImpl()
    val regex = ".*\\${File.separator}?$buildSystemPluginJar-.*\\.jar".toRegex()

    val oldPluginClasspaths =
      (commonArguments.pluginClasspaths ?: emptyArray()).filterTo(mutableListOf()) {
        val match = regex.matches(it) && validateJar(it)
        !match
      }

    // Add the compatible compiler plugin version to the classpath if available and is enabled in Gradle
    val newPluginClasspaths = oldPluginClasspaths + PLUGIN_JPS_JAR

    println("newPluginClasspaths: $newPluginClasspaths")

    commonArguments.apply {
      pluginClasspaths = newPluginClasspaths.filterNotNull().toTypedArray()
      listPhases = true
    }

    facetSettings.compilerArguments = commonArguments
  }

  /**
   * Validate whether the specified jar file is actually our compiler plugin.
   *
   * We need to perform this rather ugly check, because the artifact id of the Gradle plugin is not unique and rather
   * general (`plugin-gradle`). We therefore check whether the manifest contains references to this project.
   */
  private fun validateJar(path: String): Boolean {
    return try {
      val jar = JarInputStream(FileInputStream(path))
      val manifest = jar.manifest
      manifest.mainAttributes.getValue("Implementation-Title")
        .startsWith("arrow.meta.plugin.gradle")
    } catch (_: Exception) {
      false
    }
  }
}
