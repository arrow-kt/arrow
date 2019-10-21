package arrow.meta.plugin.testing

import arrow.meta.plugin.testing.Check.ExpectedCompilationError
import arrow.meta.plugin.testing.Check.ExpectedGeneratedClasses
import arrow.meta.plugin.testing.Check.ExpectedExecutionResult
import arrow.meta.plugin.testing.Check.ExpectedGeneratedSourceCode
import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ListAssert
import java.io.File
import java.net.URLClassLoader

private const val META_PREFIX = "//meta"
private const val EXPRESSION_PATTERN = "^[^:]+::[^(]+\\(\\)(\\.\\S+)?\$"

private data class ExpressionParts(
  val classname: String,
  val method: String,
  val property: String? = null
)

fun assertThis(compilationData: CompilationData): Unit {
  val compilationResult = compile(compilationData)

  assertThat(compilationResult.actualStatus).isEqualTo(compilationData.expectedStatus)
  compilationData.checks.forEach {
    when (it) {
      is ExpectedCompilationError -> checkCompilationResult(compilationResult, it)
      is ExpectedGeneratedClasses -> checkExpectedGeneratedClasses(compilationResult, it)
      is ExpectedExecutionResult -> checkExpectedExecutionResult(compilationResult, it)
      is ExpectedGeneratedSourceCode -> checkExpectedGeneratedSourceCode(compilationResult, it)
    }
  }
}

private fun checkCompilationResult(compilationResult: CompilationResult, expectedCompilationError: ExpectedCompilationError): AbstractStringAssert<*>? =
  assertThat(compilationResult.log).containsIgnoringCase(expectedCompilationError.partialMessage)

private fun checkExpectedGeneratedClasses(compilationResult: CompilationResult, expectedGeneratedClasses: ExpectedGeneratedClasses): ListAssert<String>? =
  assertThat(compilationResult.actualGeneratedClasses).containsExactlyInAnyOrder(*expectedGeneratedClasses.filenamesWithoutExt.toTypedArray())

private fun checkExpectedExecutionResult(compilationResult: CompilationResult, expectedExecutionResult: ExpectedExecutionResult) {
  assertThat(expectedExecutionResult.expression).matches(EXPRESSION_PATTERN)
  assertThat(call(expectedExecutionResult.expression, compilationResult.classesDirectory)).isEqualTo(expectedExecutionResult.output)
}

private fun checkExpectedGeneratedSourceCode(compilationResult: CompilationResult, expectedGeneratedSourceCode: ExpectedGeneratedSourceCode) {
  val actualGeneratedFileContent = compilationResult.actualGeneratedFilePath.toFile().readText()
  val actualGeneratedFileContentWithoutCommands = removeCommands(actualGeneratedFileContent)
  val expectedGeneratedFileContentWithoutCommands = removeCommands(expectedGeneratedSourceCode.code)

  assertThat(actualGeneratedFileContentWithoutCommands).isEqualToIgnoringWhitespace(expectedGeneratedFileContentWithoutCommands)
}

private fun removeCommands(actualGeneratedFileContent: String): String =
  actualGeneratedFileContent.lines().filter { !it.trimStart().startsWith(META_PREFIX) }.joinToString(separator = "")

private fun eval(expression: String): ExpressionParts {
  val mainParts = expression.split("::")
  val secondaryParts = mainParts[1].split("()")
  return when {
    secondaryParts.size > 1 -> ExpressionParts(classname = mainParts[0], method = secondaryParts[0], property = secondaryParts[1].removePrefix("."))
    else -> ExpressionParts(classname = mainParts[0], method = secondaryParts[0])
  }
}

private fun call(expression: String, classesDirectory: File): String {
  val classLoader = URLClassLoader(arrayOf(classesDirectory.toURI().toURL()))
  val expressionParts = eval(expression)

  val resultForMethodCall = classLoader.loadClass(expressionParts.classname).getMethod(expressionParts.method).invoke(null)
  return when {
      expressionParts.property.isNullOrBlank() -> resultForMethodCall.toString()
      else -> resultForMethodCall.javaClass.getField(expressionParts.property).get(resultForMethodCall).toString()
  }
}
