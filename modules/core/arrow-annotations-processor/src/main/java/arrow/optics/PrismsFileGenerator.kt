package arrow.optics

import arrow.common.utils.simpleName
import arrow.common.utils.knownError
import arrow.common.utils.removeBackticks

val AnnotatedType.prismSnippet
  get() = when (this) {
    is AnnotatedSumType -> Snippet(
      `package` = packageName,
      name = classData.simpleName,
      imports = setOf("import arrow.core.left", "import arrow.core.right", "import arrow.core.identity"),
      content = processElement()
    )

    is AnnotatedProductType -> knownError(element.prismErrorMessage, element)
    is AnnotatedFunctionType -> knownError(element.prismErrorMessage, element)
  }

private fun AnnotatedSumType.processElement(): String = foci.joinToString(separator = "\n\n") { focus ->
  """
    |/**
    | * [$Prism] that can see into ${sourceClassName.removeBackticks()} and focus in its property ${focus.paramName} ${focus.className.removeBackticks()}
    | */
    |inline val $sourceClassName.Companion.${focus.paramName}: $Prism<$sourceClassName, ${focus.className}> inline get()= $Prism(
    |  getOrModify = { $sourceName: $sourceClassName ->
    |    when ($sourceName) {
    |      is ${focus.className} -> $sourceName.right()
    |      else -> $sourceName.left()
    |    }
    |  },
    |  reverseGet = ::identity
    |)
    |""".trimMargin()
}
