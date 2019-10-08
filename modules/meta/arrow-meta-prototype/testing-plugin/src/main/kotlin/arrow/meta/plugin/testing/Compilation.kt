package arrow.meta.plugin.testing

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.nio.file.Paths
import io.github.classgraph.ClassGraph
import java.util.Optional
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
  val generatedFileContent: Optional<String>,
  val generatedClasses: ArrayList<String>,
  val compilationStatus: CompilationStatus
)

data class CompilationResult(
  val classesDirectory: String
)

fun testCompilation(compilationData: CompilationData): Optional<CompilationResult> {
  val kotlinSource = SourceFile.kotlin(compilationData.sourceFileName, compilationData.sourceContent)

  val result = KotlinCompilation().apply {
    sources = listOf(kotlinSource)
    classpaths = listOf(
      // TODO: waiting for the next Arrow release
      // classpathOf("arrow-annotations:x.x.x")
      File("../../arrow-annotations/build/libs/arrow-annotations-0.10.1-SNAPSHOT.jar")
    )
    pluginClasspaths = listOf(classpathOf("compiler-plugin"))
  }.compile()

  assertThat(result.exitCode).isEqualTo(exitCodeFrom(compilationData.compilationStatus))

  if (result.exitCode.equals(KotlinCompilation.ExitCode.OK)) {
    testConditions(compilationData, result)
    return Optional.of(CompilationResult(classesDirectory = result.outputDirectory.absolutePath))
  }
  return Optional.empty()
}

private fun testConditions(compilationData: CompilationData, result: KotlinCompilation.Result): Unit {
  testMetaFile(compilationData, result)
  testGeneratedClasses(compilationData, result)
}

private fun testMetaFile(compilationData: CompilationData, result: KotlinCompilation.Result): Unit {
  if (compilationData.generatedFileContent.isPresent) {
    val actualGeneratedFileContent = Paths.get(result.outputDirectory.parent, "sources", "${compilationData.sourceFileName}.meta").toFile().readText()
    val actualGeneratedFileContentWithoutCommands = removeCommandsFrom(actualGeneratedFileContent)
    val generatedFileContentWithoutCommands = removeCommandsFrom(compilationData.generatedFileContent.get())

    assertThat(actualGeneratedFileContentWithoutCommands).isEqualToIgnoringNewLines(generatedFileContentWithoutCommands)
  }
}

fun removeCommandsFrom(actualGeneratedFileContent: String): String =
  actualGeneratedFileContent.lines().filter { !it.startsWith("//meta") }.joinToString()

private fun testGeneratedClasses(compilationData: CompilationData, result: KotlinCompilation.Result): Unit {
  val actualGeneratedClasses = result.generatedFiles.map { it.name }.filter { it.endsWith(".class") }
  assertThat(actualGeneratedClasses).containsExactlyInAnyOrder(*compilationData.generatedClasses.map { "$it.class" }.toTypedArray())
}

fun contentFromResource(fromClass: Class<Any>, resourceName: String): String =
  fromClass.getResource(resourceName).readText()

fun classpathOf(dependency: String): File {
  val regex = Regex(".*${dependency.replace(':', '-')}.*")
  return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
}

private fun exitCodeFrom(compilationStatus: CompilationStatus): KotlinCompilation.ExitCode =
  when (compilationStatus) {
    CompilationStatus.OK -> KotlinCompilation.ExitCode.OK
    CompilationStatus.INTERNAL_ERROR -> KotlinCompilation.ExitCode.INTERNAL_ERROR
    CompilationStatus.COMPILATION_ERROR -> KotlinCompilation.ExitCode.COMPILATION_ERROR
    CompilationStatus.SCRIPT_EXECUTION_ERROR -> KotlinCompilation.ExitCode.SCRIPT_EXECUTION_ERROR
  }
