package arrow.meta.plugin.testing

import arrow.meta.plugin.testing.Check.ExpectedCompilationError
import arrow.meta.plugin.testing.Check.ExpectedGeneratedSourceCode

interface CompilationTest {

  infix fun String.withDependencies(dependencies: List<String>): PartialCompilationData =
    PartialCompilationData(
      dependencies = dependencies,
      sourceCode = this.trimMargin()
    )

  infix fun PartialCompilationData.compilesTo(result: String): Result =
    assertThis(CompilationData(
      dependencies = dependencies,
      sourceCode = sourceCode,
      expectedStatus = CompilationStatus.OK,
      checks = listOf(ExpectedGeneratedSourceCode(code = result.trimMargin())))
    )

  infix fun String.compilesTo(result: String): Result =
    assertThis(CompilationData(
      sourceCode = trimMargin(),
      expectedStatus = CompilationStatus.OK,
      checks = listOf(ExpectedGeneratedSourceCode(code = result.trimMargin())))
    )

  infix fun String.emitErrorDiagnostic(partialMessage: String): Unit {
    assertThis(CompilationData(
      sourceCode = trimMargin(),
      expectedStatus = CompilationStatus.COMPILATION_ERROR,
      checks = listOf(ExpectedCompilationError(partialMessage = partialMessage)))
    )
  }

  infix fun Result.andExpression(expression: String): ExecutionEnv =
    ExecutionEnv(
      classesDirectory= this.classesDirectory,
      expression= expression
    )

  infix fun ExecutionEnv.evalTo(result: String): Unit =
    assertThis(this, result)
}
