package arrow.meta.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool
import java.io.File
import java.util.*


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
    project.tasks.withType(AbstractKotlinCompileTool::class.java).all {
      (it.defaultSerializedCompilerArguments as ArrayList) + File("")
      println(it.compilerClasspath)
    }

    /*project.afterEvaluate { p ->
      if (isEnabled(p)) {
        project.co
        tasks.withType(JavaExec::class.java).all {
          it.setArgsString("-Xplugin=${project.rootDir}/modules/meta/arrow-meta-prototype/compiler-plugin/build/libs/compiler-plugin.jar")
        }
      }
    }*/
  }
  /*project.pluginManager.withPlugin("arrow.meta.gradle.plugin") {
    val kExtension = checkNotNull(project.extensions.findByType(KotlinJvmProjectExtension::class.java))
    kExtension.target {

    }
  }
  project.tasks.withType(AbstractKotlinCompile::class.java).all {
  }
  project.tasks.withType(KotlinCompile::class.java).all {
    val mutableList = mutableListOf(it.kotlinOptions.freeCompilerArgs, listOf())
    //it.kotlinOptions.freeCompilerArgs.apply {
    //val a = this + "-Xplugin=${project.rootDir}/modules/meta/arrow-meta-prototype/compiler-plugin/build/libs/compiler-plugin.jar"
    //it.kotlinOptions.freeCompilerArgs + "BLA"
    //a
    it.kotlinOptions.copyFreeCompilerArgsToArgs(
      objec
    )
    println(it.kotlinOptions.freeCompilerArgs)
  }*/

  /*project.afterEvaluate { p ->
    if (isEnabled(p)) {
      println("Add compiler-plugin to freeCompilerArgs")
      p.tasks.withType(KotlinCompile::class.java).all {
        it.kotlinOptions.freeCompilerArgs.run {
          this + "-Xplugin=${project.rootDir.toString() + "/modules/meta/arrow-meta-prototype/compiler-plugin/build/libs/compiler-plugin.jar"}"
        }
      }
    }
  }*/
/*project.pluginManager.withPlugin("arrow.meta.plugin.gradle") {
  val kExtension: KotlinProjectExtension = checkNotNull(project.extensions.findByType(KotlinProjectExtension::class.java))
  val gradlePlugin: ArrowGradlePlugin = project.extensions.create("Arrow_Plugin", ArrowGradlePlugin::class.java, project.objects)
  project.configureDefaults(gradlePlugin, kExtension)
}*/
// println("Try add the Plugin")
// project.addGradleDependency(PLUGIN_CLASSPATH_CONFIGURATION_NAME, ArrowKotlinGradleSubplugin().getPluginArtifact())
//}
}

/*fun Project.configureDefaults(gradlePlugin: ArrowGradlePlugin, kExtension: KotlinProjectExtension): Unit? =
  !gradlePlugin.en*/

/*fun Project.addGradleDependency(configuration: String, artifact: SubpluginArtifact): Unit {
  val artifactVersion = artifact.version ?: "0.0.1"
  val gradleCoordinate = "${artifact.groupId}:${artifact.artifactId}:$artifactVersion"
  project.dependencies.add(configuration, gradleCoordinate)
}*/
