package arrow.optics.plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.github.classgraph.ClassGraph
import org.assertj.core.api.Assertions
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths

val arrowVersion = System.getProperty("arrowVersion")
const val SOURCE_FILENAME = "Source.kt"
const val CLASS_FILENAME = "SourceKt"

fun String.failsWith(check: (String) -> Boolean) {
  val compilationResult = compile(this)
  Assertions.assertThat(compilationResult.exitCode).isNotEqualTo(KotlinCompilation.ExitCode.OK)
  Assertions.assertThat(check(compilationResult.messages)).isTrue
}

fun String.compilationFails() {
  val compilationResult = compile(this)
  Assertions.assertThat(compilationResult.exitCode).isNotEqualTo(KotlinCompilation.ExitCode.OK)
}

fun String.compilationSucceeds(allWarningsAsErrors: Boolean = false) {
  val compilationResult = compile(this, allWarningsAsErrors = allWarningsAsErrors)
  Assertions.assertThat(compilationResult.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
}

fun String.evals(thing: Pair<String, Any?>) {
  val compilationResult = compile(this)
  Assertions.assertThat(compilationResult.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
  val classesDirectory = compilationResult.outputDirectory
  val (variable, output) = thing
  Assertions.assertThat(eval(variable, classesDirectory)).isEqualTo(output)
}

// UTILITY FUNCTIONS COPIED FROM META-TEST
// =======================================

internal fun compile(text: String, allWarningsAsErrors: Boolean = false): KotlinCompilation.Result {
  val compilation = buildCompilation(text, allWarningsAsErrors = allWarningsAsErrors)
  // fix problems with double compilation and KSP
  // as stated in https://github.com/tschuchortdev/kotlin-compile-testing/issues/72
  val pass1 = compilation.compile()
  // if the first pass was unsuccessful, return it
  if (pass1.exitCode != KotlinCompilation.ExitCode.OK) return pass1
  // return the results of second pass
  return buildCompilation(text)
    .apply {
      sources = compilation.sources + compilation.kspGeneratedSourceFiles
      symbolProcessorProviders = emptyList()
    }
    .compile()
}

fun buildCompilation(text: String, allWarningsAsErrors: Boolean = false) = KotlinCompilation().apply {
  classpaths = listOf(
    "arrow-annotations:$arrowVersion",
    "arrow-core:$arrowVersion",
    "arrow-optics:$arrowVersion"
  ).map { classpathOf(it) }
  symbolProcessorProviders = listOf(OpticsProcessorProvider())
  sources = listOf(SourceFile.kotlin(SOURCE_FILENAME, text.trimMargin()))
  verbose = false
  this.allWarningsAsErrors = allWarningsAsErrors
}

private fun classpathOf(dependency: String): File {
  val file =
    ClassGraph().classpathFiles.firstOrNull { classpath ->
      dependenciesMatch(classpath, dependency)
    }
//  println("classpath: ${ClassGraph().classpathFiles}")
  Assertions.assertThat(file)
    .`as`("$dependency not found in test runtime. Check your build configuration.")
    .isNotNull
  return file!!
}

private fun dependenciesMatch(classpath: File, dependency: String): Boolean {
  val dep = classpath.name
  val dependencyName = sanitizeClassPathFileName(dep)
  val testdep = dependency.substringBefore(":")
  return testdep == dependencyName
}

private fun sanitizeClassPathFileName(dep: String): String =
  buildList<Char> {
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

private val KotlinCompilation.kspGeneratedSourceFiles: List<SourceFile>
  get() =
    kspSourcesDir
      .resolve("kotlin")
      .walk()
      .filter { it.isFile }
      .map { SourceFile.fromPath(it.absoluteFile) }
      .toList()

private fun eval(expression: String, classesDirectory: File): Any? {
  val classLoader = URLClassLoader(arrayOf(classesDirectory.toURI().toURL()))
  val fullClassName = getFullClassName(classesDirectory, CLASS_FILENAME)
  val field = classLoader.loadClass(fullClassName).getDeclaredField(expression)
  field.isAccessible = true
  return field.get(Object())
}

private fun getFullClassName(classesDirectory: File, className: String): String =
  Files.walk(Paths.get(classesDirectory.toURI()))
    .filter { it.toFile().name == "$className.class" }
    .toArray()[0]
    .toString()
    .removePrefix(classesDirectory.absolutePath + File.separator)
    .removeSuffix(".class")
    .replace(File.separator, ".")
