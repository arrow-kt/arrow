package arrow.meta.utils

import org.jetbrains.kotlin.com.intellij.openapi.project.Project

private val isIde: Boolean = Project::class.java.name == "com.intellij.openapi.project.Project"

private val isCli: Boolean = !isIde && Project::class.java.name == "org.jetbrains.kotlin.com.intellij.openapi.project.Project"

fun <A> foldRuntime(onIde: () -> A? = { null }, onCli: () -> A? = { null }): A? =
  if (isIde) onIde() else if (isCli) onCli() else null

fun <A> cli(f: () -> A) : A? =
  if (isCli) f() else null

fun <A> ide(f: () -> A) : A? =
  if (isIde) f() else null