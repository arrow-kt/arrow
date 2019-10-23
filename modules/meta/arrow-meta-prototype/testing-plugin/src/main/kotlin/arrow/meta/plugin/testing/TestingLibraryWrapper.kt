package arrow.meta.plugin.testing

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.github.classgraph.ClassGraph
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

private const val DEFAULT_FILENAME = "Example.kt"

internal data class CompilationResult(
  val actualStatus: CompilationStatus,
  val log: String,
  val actualGeneratedFilePath: Path,
  val classesDirectory: File
)

internal fun compile(compilationData: CompilationData): CompilationResult =
  compilationResultFrom(KotlinCompilation().apply {
    sources = listOf(SourceFile.kotlin("Example.kt", compilationData.sourceCode))
    classpaths = compilationData.dependencies.map { classpathOf(it) }
    pluginClasspaths = listOf(classpathOf("compiler-plugin"))
  }.compile())

private fun compilationResultFrom(internalResult: KotlinCompilation.Result): CompilationResult =
  CompilationResult(
    actualStatus = exitStatusFrom(internalResult.exitCode),
    log = internalResult.messages,
    actualGeneratedFilePath = Paths.get(internalResult.outputDirectory.parent, "sources", "$DEFAULT_FILENAME.meta"),
    classesDirectory = internalResult.outputDirectory
  )

private fun exitStatusFrom(exitCode: KotlinCompilation.ExitCode): CompilationStatus =
  when (exitCode) {
    KotlinCompilation.ExitCode.OK -> CompilationStatus.OK
    KotlinCompilation.ExitCode.INTERNAL_ERROR -> CompilationStatus.INTERNAL_ERROR
    KotlinCompilation.ExitCode.COMPILATION_ERROR -> CompilationStatus.COMPILATION_ERROR
    KotlinCompilation.ExitCode.SCRIPT_EXECUTION_ERROR -> CompilationStatus.SCRIPT_EXECUTION_ERROR
  }

private fun classpathOf(dependency: String): File {
  val regex = Regex(".*${dependency.replace(':', '-')}.*")
  return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
}
