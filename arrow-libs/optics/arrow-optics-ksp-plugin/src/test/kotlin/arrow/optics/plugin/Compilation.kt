@file:OptIn(ExperimentalCompilerApi::class)

package arrow.optics.plugin

import com.tschuchort.compiletesting.CompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.github.classgraph.ClassGraph
import io.kotest.assertions.AssertionErrorBuilder.Companion.fail
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.JvmTarget
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths

val arrowVersion = System.getProperty("arrowVersion")
const val SOURCE_FILENAME = "Source.kt"
const val CLASS_FILENAME = "SourceKt"

fun String.failsWith(check: (String) -> Boolean) {
  val compilationResult = compile(this)
  compilationResult.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
  check(compilationResult.messages).shouldBeTrue()
}

fun String.compilationFails() {
  val compilationResult = compile(this)
  compilationResult.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
}

fun String.compilationSucceeds(allWarningsAsErrors: Boolean = false) {
  val compilationResult = compile(this, allWarningsAsErrors = allWarningsAsErrors)
  compilationResult.exitCode.shouldBe(KotlinCompilation.ExitCode.OK, compilationResult.messages)
}

fun String.evals(thing: Pair<String, Any?>) {
  val compilationResult = compile(this)
  compilationResult.exitCode.shouldBe(KotlinCompilation.ExitCode.OK, compilationResult.messages)
  val classesDirectory = compilationResult.outputDirectory
  val (variable, output) = thing
  eval(variable, classesDirectory) shouldBe output
}

// UTILITY FUNCTIONS COPIED FROM META-TEST
// =======================================

internal fun compile(text: String, allWarningsAsErrors: Boolean = false): CompilationResult {
  val compilation = buildCompilation(text, allWarningsAsErrors = allWarningsAsErrors)
  return compilation.compile()
}

fun buildCompilation(text: String, allWarningsAsErrors: Boolean = false) = KotlinCompilation().apply {
  jvmTarget = JvmTarget.JVM_1_8.description
  classpaths = listOf(
    "arrow-annotations:$arrowVersion",
    "arrow-core:$arrowVersion",
    "arrow-optics:$arrowVersion",
  ).map { classpathOf(it) }
  sources = listOf(SourceFile.kotlin(SOURCE_FILENAME, text.trimMargin()))
  verbose = false
  this.allWarningsAsErrors = allWarningsAsErrors
  languageVersion = "2.0"
  configureKsp(useKsp2 = true) {
    withCompilation = true
    symbolProcessorProviders += OpticsProcessorProvider()
  }
}

private fun classpathOf(dependency: String): File {
  val file =
    ClassGraph().classpathFiles.firstOrNull { classpath ->
      dependenciesMatch(classpath, dependency)
    }
  if (file == null) {
    fail("$dependency not found in test runtime. Check your build configuration.")
  }
  return file
}

private fun dependenciesMatch(classpath: File, dependency: String): Boolean {
  val dep = classpath.name
  val dependencyName = sanitizeClassPathFileName(dep)
  val testdep = dependency.substringBefore(":")
  return testdep == dependencyName
}

private fun sanitizeClassPathFileName(dep: String): String = buildList {
  var skip = false
  add(dep.first())
  dep.reduce { a, b ->
    if (a == '-' && b.isDigit()) skip = true
    if (!skip) add(b)
    b
  }
  if (skip) removeLast()
}
  .joinToString("")
  .replace("-jvm.jar", "")
  .replace("-jvm", "")

private fun eval(expression: String, classesDirectory: File): Any? {
  val classLoader = URLClassLoader(arrayOf(classesDirectory.toURI().toURL()))
  val fullClassName = getFullClassName(classesDirectory)
  val field = classLoader.loadClass(fullClassName).getDeclaredField(expression)
  field.isAccessible = true
  return field.get(Any())
}

private fun getFullClassName(classesDirectory: File): String = Files.walk(Paths.get(classesDirectory.toURI()))
  .filter { it.toFile().name == "$CLASS_FILENAME.class" }
  .toArray()[0]
  .toString()
  .removePrefix(classesDirectory.absolutePath + File.separator)
  .removeSuffix(".class")
  .replace(File.separator, ".")
