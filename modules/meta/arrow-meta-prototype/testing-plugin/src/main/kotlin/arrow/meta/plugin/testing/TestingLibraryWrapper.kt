package arrow.meta.plugin.testing

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.github.classgraph.ClassGraph
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

private const val CLASS_EXTENSION = ".class"

internal data class CompilationResult(
  val actualGeneratedClasses: List<String>,
  val actualStatus: CompilationStatus,
  val log: String,
  val sourcesDirectory: Path
)

internal fun compile(compilationData: CompilationData): CompilationResult =
  compilationResultFrom(KotlinCompilation().apply {
    sources = listOf(SourceFile.kotlin(compilationData.sourceFilename, compilationData.sourceCode))
    classpaths = compilationData.dependencies.map { classpathOf(it) }
    pluginClasspaths = listOf(classpathOf("compiler-plugin"))
  }.compile())

private fun compilationResultFrom(internalResult: KotlinCompilation.Result) =
  CompilationResult(
    actualGeneratedClasses = classFilenamesFrom(internalResult.generatedFiles),
    actualStatus = exitStatusFrom(internalResult.exitCode),
    log = internalResult.messages,
    sourcesDirectory = Paths.get(internalResult.outputDirectory.parent, "sources")
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

private fun classFilenamesFrom(generatedFiles: Collection<File>): List<String> =
  generatedFiles.map { it.name }.filter { it.endsWith(CLASS_EXTENSION) }.map { it.removeSuffix(CLASS_EXTENSION) }
