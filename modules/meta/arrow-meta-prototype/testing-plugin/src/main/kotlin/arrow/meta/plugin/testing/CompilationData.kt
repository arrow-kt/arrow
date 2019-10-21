package arrow.meta.plugin.testing

enum class CompilationStatus {
  OK,
  INTERNAL_ERROR,
  COMPILATION_ERROR,
  SCRIPT_EXECUTION_ERROR
}

sealed class Check {

  data class ExpectedCompilationError(
    val partialMessage: String
  ) : Check()

  data class ExpectedGeneratedSourceCode(
    val code: String
  ) : Check()

  data class ExpectedGeneratedClasses(
    val filenamesWithoutExt: List<String>
  ) : Check()

  data class ExpectedExecutionResult(
    val expression: String,
    val output: String
  ) : Check()
}

data class CompilationData(
  val dependencies: List<String> = emptyList(),
  val sourceFilename: String,
  val sourceCode: String,
  val expectedStatus: CompilationStatus,
  val checks: List<Check>
)
