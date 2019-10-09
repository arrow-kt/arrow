package arrow.meta.plugin.testing

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.nio.file.Paths
import io.github.classgraph.ClassGraph
import java.net.URLClassLoader
import kotlin.collections.ArrayList

enum class CompilationStatus {
  OK,
  INTERNAL_ERROR,
  COMPILATION_ERROR,
  SCRIPT_EXECUTION_ERROR
}

data class CompilationData(
  val sourceFileName: String,
  val sourceContent: String,
  val generatedFileContent: String?,
  val generatedClasses: ArrayList<String>,
  val compilationStatus: CompilationStatus
)

data class CompilationResult(
  val classesDirectory: String
)

data class InvocationData(
  val classesDirectory: String?,
  val className: String,
  val methodName: String
)

fun assertCompilation(compilationData: CompilationData): CompilationResult? {
  val kotlinSource = SourceFile.kotlin(compilationData.sourceFileName, compilationData.sourceContent)

  val result = KotlinCompilation().apply {
    sources = listOf(kotlinSource)
    //
    // TODO: waiting for the arrow-annotations release which contains higherkind annotation
    //    classpaths = listOf(classpathOf("arrow-annotations:x.x.x"))
    //
    classpaths = listOf(classpathOf("arrow-annotations:rr-meta-prototype-integration-SNAPSHOT"))
    pluginClasspaths = listOf(classpathOf("compiler-plugin"))
  }.compile()

  assertThat(result.exitCode).isEqualTo(exitCodeFrom(compilationData.compilationStatus))

  if (result.exitCode == KotlinCompilation.ExitCode.OK) {
    testConditions(compilationData, result)
    return CompilationResult(classesDirectory = result.outputDirectory.absolutePath)
  }
  return null
}

fun contentFromResource(fromClass: Class<Any>, resourceName: String): String =
  fromClass.getResource(resourceName).readText()

fun invoke(invocationData: InvocationData): Any =
  getClassLoaderForGeneratedClasses(invocationData.classesDirectory).loadClass(invocationData.className).getMethod(invocationData.methodName).invoke(null)

fun getFieldFrom(result: Any, fieldName: String): Any =
  result.javaClass.getField(fieldName).get(result)

private fun classpathOf(dependency: String): File {
  val regex = Regex(".*${dependency.replace(':', '-')}.*")
  return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
}

private fun testConditions(compilationData: CompilationData, result: KotlinCompilation.Result): Unit {
  testMetaFile(compilationData, result)
  testGeneratedClasses(compilationData, result)
}

private fun testMetaFile(compilationData: CompilationData, result: KotlinCompilation.Result): Unit {
  if (!compilationData.generatedFileContent.isNullOrEmpty()) {
    val actualGeneratedFileContent = Paths.get(result.outputDirectory.parent, "sources", "${compilationData.sourceFileName}.meta").toFile().readText()
    val actualGeneratedFileContentWithoutCommands = removeCommandsFrom(actualGeneratedFileContent)
    val generatedFileContentWithoutCommands = removeCommandsFrom(compilationData.generatedFileContent)

    assertThat(actualGeneratedFileContentWithoutCommands).isEqualToIgnoringNewLines(generatedFileContentWithoutCommands)
  }
}

private fun removeCommandsFrom(actualGeneratedFileContent: String): String =
  actualGeneratedFileContent.lines().filter { !it.startsWith("//meta") }.joinToString()

private fun testGeneratedClasses(compilationData: CompilationData, result: KotlinCompilation.Result): Unit {
  val actualGeneratedClasses = result.generatedFiles.map { it.name }.filter { it.endsWith(".class") }
  assertThat(actualGeneratedClasses).containsExactlyInAnyOrder(*compilationData.generatedClasses.map { "$it.class" }.toTypedArray())
}

private fun exitCodeFrom(compilationStatus: CompilationStatus): KotlinCompilation.ExitCode =
  when (compilationStatus) {
    CompilationStatus.OK -> KotlinCompilation.ExitCode.OK
    CompilationStatus.INTERNAL_ERROR -> KotlinCompilation.ExitCode.INTERNAL_ERROR
    CompilationStatus.COMPILATION_ERROR -> KotlinCompilation.ExitCode.COMPILATION_ERROR
    CompilationStatus.SCRIPT_EXECUTION_ERROR -> KotlinCompilation.ExitCode.SCRIPT_EXECUTION_ERROR
  }

private fun getClassLoaderForGeneratedClasses(classesDirectory: String?): ClassLoader =
  URLClassLoader(arrayOf(File(classesDirectory).toURI().toURL()))
