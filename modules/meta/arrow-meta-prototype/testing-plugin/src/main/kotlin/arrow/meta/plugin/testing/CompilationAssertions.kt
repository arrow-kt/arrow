package arrow.meta.plugin.testing

import arrow.meta.plugin.testing.Check.ExpectedCompilationError
import arrow.meta.plugin.testing.Check.ExpectedGeneratedSourceCode
import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.net.URLClassLoader

private const val META_PREFIX = "//meta"
private const val EXPRESSION_PATTERN = "^[^(]+\\(\\)(\\.\\S+)?\$"
private const val DEFAULT_CLASSNAME = "ExampleKt"

private data class ExpressionParts(
  val method: String,
  val property: String? = null
)

internal fun assertThis(compilationData: CompilationData): Result {
  val compilationResult = compile(compilationData)

  assertThat(compilationResult.actualStatus).isEqualTo(compilationData.expectedStatus)
  compilationData.checks.forEach {
    when (it) {
      is ExpectedCompilationError -> checkCompilationResult(compilationResult, it)
      is ExpectedGeneratedSourceCode -> checkExpectedGeneratedSourceCode(compilationResult, it)
    }
  }
  return Result(classesDirectory = compilationResult.classesDirectory)
}

internal fun assertThis(executionEnv: ExecutionEnv, result: String): Unit {
  assertThat(executionEnv.expression).matches(EXPRESSION_PATTERN)
  assertThat(call(executionEnv.expression, executionEnv.classesDirectory)).isEqualTo(result)
}

private fun checkCompilationResult(compilationResult: CompilationResult, expectedCompilationError: ExpectedCompilationError): AbstractStringAssert<*>? =
  assertThat(compilationResult.log).containsIgnoringCase(expectedCompilationError.partialMessage)

private fun checkExpectedGeneratedSourceCode(compilationResult: CompilationResult, expectedGeneratedSourceCode: ExpectedGeneratedSourceCode): Unit {
  val actualGeneratedFileContent = compilationResult.actualGeneratedFilePath.toFile().readText()
  val actualGeneratedFileContentWithoutCommands = removeCommands(actualGeneratedFileContent)
  val expectedGeneratedFileContentWithoutCommands = removeCommands(expectedGeneratedSourceCode.code)

  assertThat(actualGeneratedFileContentWithoutCommands)
    .`as`("EXPECTED:${expectedGeneratedSourceCode.code}\nACTUAL:$actualGeneratedFileContent\nNOTE: Meta commands are skipped in the comparison")
    .isEqualToIgnoringWhitespace(expectedGeneratedFileContentWithoutCommands)
}

private fun removeCommands(actualGeneratedFileContent: String): String =
  actualGeneratedFileContent.lines().filter { !it.trimStart().startsWith(META_PREFIX) }.joinToString(separator = "")

private fun eval(expression: String): ExpressionParts {
  val parts = expression.split("()")
  return when {
    parts.size > 1 -> ExpressionParts(method = parts[0], property = parts[1].removePrefix("."))
    else -> ExpressionParts(method = parts[0])
  }
}

private fun call(expression: String, classesDirectory: File): String {
  val classLoader = URLClassLoader(arrayOf(classesDirectory.toURI().toURL()))
  val expressionParts = eval(expression)

  val resultForMethodCall = classLoader.loadClass(DEFAULT_CLASSNAME).getMethod(expressionParts.method).invoke(null)
  return when {
      expressionParts.property.isNullOrBlank() -> resultForMethodCall.toString()
      else -> resultForMethodCall.javaClass.getField(expressionParts.property).get(resultForMethodCall).toString()
  }
}
