package arrow.meta.plugin.testing

import org.assertj.core.api.Assertions.assertThat
import java.nio.file.Path

private const val META_PREFIX = "//meta"

fun assertThis(compilationData: CompilationData): Unit {
  val compilationResult = compile(compilationData)

  assertThat(compilationResult.actualStatus).isEqualTo(compilationData.expectedStatus)
  compilationData.checks.forEach {
    when (it) {
      is Check.ExpectedCompilationError ->
        assertThat(compilationResult.log).containsIgnoringCase(it.partialMessage)
      is Check.ExpectedGeneratedClasses ->
        assertThat(compilationResult.actualGeneratedClasses).containsExactlyInAnyOrder(*it.filenamesWithoutExt.toTypedArray())
      is Check.ExpectedGeneratedSourceCode -> {
        val actualGeneratedFileContent = getGeneratedFileContentFrom(compilationResult.sourcesDirectory, compilationData.sourceFilename)
        val actualGeneratedFileContentWithoutCommands = removeCommands(actualGeneratedFileContent)
        val expectedGeneratedFileContentWithoutCommands = removeCommands(it.code)

        assertThat(actualGeneratedFileContentWithoutCommands).
          isEqualToIgnoringWhitespace(expectedGeneratedFileContentWithoutCommands)
      }
    }
  }
}

private fun getGeneratedFileContentFrom(sourcesDirectory: Path, sourceFilename: String): String =
  sourcesDirectory.resolve("$sourceFilename.meta").toFile().readText()

private fun removeCommands(actualGeneratedFileContent: String): String =
  actualGeneratedFileContent.lines().filter { !it.trimStart().startsWith(META_PREFIX) }.joinToString(separator = "")
