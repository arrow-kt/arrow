package arrow.meta.plugin.testing

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.github.classgraph.ClassGraph
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.nio.file.Paths

fun assertThis(compilationData: CompilationData): Unit {

  val sourceFilename = compilationData.sourceFilename

  val compilationResult = KotlinCompilation().apply {
    sources = listOf(SourceFile.kotlin(sourceFilename, compilationData.sourceCode))
    classpaths = compilationData.dependencies.map { classpathOf(it) }
    pluginClasspaths = listOf(classpathOf("compiler-plugin"))
  }.compile()

  assertThat(exitStatusFrom(compilationResult.exitCode)).isEqualTo(compilationData.compilationStatus)
  compilationData.checks.forEach {
    when (it) {
      is Check.CompilationError -> assertThat(compilationResult.messages).containsIgnoringCase(it.partialMessage)
      is Check.GeneratedClasses -> assertGeneratedClasses(it, compilationResult)
      is Check.GeneratedSourceCode -> {
        val actualGeneratedFileContent = getGeneratedFileContentFrom(compilationResult.outputDirectory, sourceFilename)
        val actualGeneratedFileContentWithoutCommands = removeCommands(actualGeneratedFileContent)
        val expectedGeneratedFileContentWithoutCommands = removeCommands(it.code)

        assertThat(actualGeneratedFileContentWithoutCommands).
          isEqualToIgnoringWhitespace(expectedGeneratedFileContentWithoutCommands)
      }
    }
  }
}

fun contentFromResource(fromClass: Class<Any>, resourceName: String): String =
  fromClass.getResource(resourceName).readText()

private fun assertGeneratedClasses(generatedClasses: Check.GeneratedClasses, compilationResult: KotlinCompilation.Result) {
  val actualGeneratedClasses = classFilenamesFrom(compilationResult.generatedFiles)
  val expectedGeneratedClasses = createClassFilenamesFrom(generatedClasses.filenamesWithoutExt)

  assertThat(actualGeneratedClasses).containsExactlyInAnyOrder(*expectedGeneratedClasses.toTypedArray())
}

private fun getGeneratedFileContentFrom(outputDirectory: File, sourceFilename: String): String =
  Paths.get(outputDirectory.parent, "sources", "$sourceFilename.meta").toFile().readText()

private fun createClassFilenamesFrom(filenamesWithoutExt: List<String>): List<String> =
  filenamesWithoutExt.map { "$it.class" }

private fun classFilenamesFrom(generatedFiles: Collection<File>): List<String> =
  generatedFiles.map { it.name }.filter { it.endsWith(".class") }

private fun classpathOf(dependency: String): File {
  val regex = Regex(".*${dependency.replace(':', '-')}.*")
  return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
}

private fun removeCommands(actualGeneratedFileContent: String): String =
  actualGeneratedFileContent.lines().filter { !it.trimStart().startsWith(META_PREFIX) }.joinToString(separator = "")

private fun exitStatusFrom(exitCode: KotlinCompilation.ExitCode): CompilationStatus =
  when (exitCode) {
    KotlinCompilation.ExitCode.OK -> CompilationStatus.OK
    KotlinCompilation.ExitCode.INTERNAL_ERROR -> CompilationStatus.INTERNAL_ERROR
    KotlinCompilation.ExitCode.COMPILATION_ERROR -> CompilationStatus.COMPILATION_ERROR
    KotlinCompilation.ExitCode.SCRIPT_EXECUTION_ERROR -> CompilationStatus.SCRIPT_EXECUTION_ERROR
  }
