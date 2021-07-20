package arrow.ank

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import java.util.Properties

public class AnkPlugin : Plugin<Project> {

  public companion object {
    private const val EXTENSION_NAME = "ank"
    private const val TASK_NAME = "runAnk"
  }

  @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
  override fun apply(target: Project) {
    val properties = Properties()
    properties.load(this.javaClass.getResourceAsStream("plugin.properties"))
    val extension = AnkExtension()
    target.extensions.add(EXTENSION_NAME, extension)
    target.afterEvaluate {
      target.dependencies.add("runtimeOnly", "io.arrow-kt:arrow-ank:${properties.getProperty("CURRENT_VERSION")}")
      target.tasks.create(TASK_NAME, JavaExec::class.java).apply {
        classpath = extension.classpath
        main = "arrow.ank.main"
      }.args = mutableListOf(
        extension.source!!.absolutePath,
        extension.target!!.absolutePath,
        *extension.classpath!!.map { it.toURI().toURL().toString() }.toTypedArray()
      )
    }
  }
}
