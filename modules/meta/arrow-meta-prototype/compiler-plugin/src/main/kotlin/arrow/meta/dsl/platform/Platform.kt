package arrow.meta.dsl.platform

import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.com.intellij.openapi.project.Project

private val isIde: Boolean = Project::class.java.name == "com.intellij.openapi.project.Project"

private val isCli: Boolean = !isIde && Project::class.java.name == "org.jetbrains.kotlin.com.intellij.openapi.project.Project"

fun <A> cli(f: () -> A): A? =
  if (isCli) f() else null

fun <A> ide(f: () -> A): A? =
  if (isIde) f() else null

fun <A> ideRegistry(f: () -> A): ExtensionPhase =
  if (isIde) f().run { ExtensionPhase.Empty } else ExtensionPhase.Empty
