package arrow.meta.plugin.testing

import java.io.File

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
}

data class PartialCompilationData(
  val dependencies: List<String> = emptyList(),
  val sourceCode: String
)

data class CompilationData(
  val dependencies: List<String> = emptyList(),
  val sourceCode: String,
  val expectedStatus: CompilationStatus,
  val checks: List<Check> = emptyList()
)

data class Result(
  val classesDirectory: File
)

data class ExecutionEnv(
  val classesDirectory: File,
  val expression: String
)
