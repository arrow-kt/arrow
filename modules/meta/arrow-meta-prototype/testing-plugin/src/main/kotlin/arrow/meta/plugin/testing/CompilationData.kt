package arrow.meta.plugin.testing

const val META_PREFIX = "//meta"

enum class CompilationStatus {
  OK,
  INTERNAL_ERROR,
  COMPILATION_ERROR,
  SCRIPT_EXECUTION_ERROR

}

data class CompilationData(
  val dependencies: List<String> = emptyList(),
  val sourceFilename: String,
  val sourceCode: String,
  val compilationStatus: CompilationStatus,
  val checks: List<Check>
)

sealed class Check {

  data class CompilationError(
    val partialMessage: String
  ): Check()

  data class GeneratedSourceCode(
    val code: String
  ): Check()

  data class GeneratedClasses(
    val filenamesWithoutExt: List<String>
  ): Check()
}
