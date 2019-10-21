package arrow.meta.plugin.testing

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.net.URLClassLoader

private const val META_PREFIX = "//meta"
private const val EXPRESSION_PATTERN = "^[^:]+::[^(]+\\(\\)(\\.\\S+)?\$"

private data class Expression(
  val classname: String,
  val method: String,
  val property: String? = null
)

fun assertThis(compilationData: CompilationData): Unit {
  val compilationResult = compile(compilationData)

  assertThat(compilationResult.actualStatus).isEqualTo(compilationData.expectedStatus)
  compilationData.checks.forEach {
    when (it) {
      is Check.ExpectedCompilationError ->
        assertThat(compilationResult.log).containsIgnoringCase(it.partialMessage)
      is Check.ExpectedGeneratedClasses ->
        assertThat(compilationResult.actualGeneratedClasses).containsExactlyInAnyOrder(*it.filenamesWithoutExt.toTypedArray())
      is Check.ExpectedExecutionResult -> {
        assertThat(Regex(EXPRESSION_PATTERN).matches(it.expression)).`as`("\"${it.expression}\" doesn't match with $EXPRESSION_PATTERN").isTrue()
        assertThat(call(it.expression, compilationResult.classesDirectory)).isEqualTo(it.output)
      }
      is Check.ExpectedGeneratedSourceCode -> {
        val actualGeneratedFileContent = compilationResult.actualGeneratedFilePath.toFile().readText()
        val actualGeneratedFileContentWithoutCommands = removeCommands(actualGeneratedFileContent)
        val expectedGeneratedFileContentWithoutCommands = removeCommands(it.code)

        assertThat(actualGeneratedFileContentWithoutCommands).isEqualToIgnoringWhitespace(expectedGeneratedFileContentWithoutCommands)
      }
    }
  }
}

private fun removeCommands(actualGeneratedFileContent: String): String =
  actualGeneratedFileContent.lines().filter { !it.trimStart().startsWith(META_PREFIX) }.joinToString(separator = "")

private fun eval(call: String): Expression {
  val mainParts = call.split("::")
  val secondaryParts = mainParts[1].split("()")
  return when {
    secondaryParts.size > 1 -> Expression(classname = mainParts[0], method = secondaryParts[0], property = secondaryParts[1].removePrefix("."))
    else -> Expression(classname = mainParts[0], method = secondaryParts[0])
  }
}

private fun call(call: String, classesDirectory: File): String {
  val classLoader = URLClassLoader(arrayOf(classesDirectory.toURI().toURL()))
  val expression = eval(call)

  val resultForTest = classLoader.loadClass(expression.classname).getMethod(expression.method).invoke(null)
  return when {
      expression.property.isNullOrBlank() -> resultForTest.toString()
      else -> resultForTest.javaClass.getField(expression.property).get(resultForTest).toString()
  }
}
