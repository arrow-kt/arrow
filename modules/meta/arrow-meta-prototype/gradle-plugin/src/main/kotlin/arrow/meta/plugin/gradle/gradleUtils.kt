package arrow.meta.plugin.gradle

import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.Task

fun getCompileKotlinTaskName(project: Project, compilation: Named): Task? {
  val compilationClass = compilation.javaClass
  val getCompileKotlinTaskName = compilationClass.getMethodOrNull("getCompileKotlinTaskName") ?: return null
  @Suppress("UNCHECKED_CAST")
  val compileKotlinTaskName = (getCompileKotlinTaskName(compilation) as? String) ?: return null
  return project.tasks.findByName(compileKotlinTaskName) ?: return null
}

fun Class<*>.getMethodOrNull(name: String, vararg parameterTypes: Class<*>) =
  try {
    getMethod(name, *parameterTypes)
  } catch (e: Exception) {
    null
  }
