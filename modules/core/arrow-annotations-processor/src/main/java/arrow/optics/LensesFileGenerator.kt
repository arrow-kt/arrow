package arrow.optics

import arrow.common.utils.knownError
import arrow.common.utils.removeBackticks
import arrow.common.utils.simpleName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank
import arrow.optics.Optic.*

val AnnotatedType.lensSnippet
  get() = when (this) {
    is AnnotatedProductType -> Snippet(
      `package` = packageName,
      name = classData.simpleName,
      content = processElement()
    )
    is AnnotatedSumType -> knownError(element.lensErrorMessage, element)
    is AnnotatedFunctionType -> knownError(element.lensErrorMessage, element)
  }

private fun String.toUpperCamelCase(): String = split(" ").joinToString("", transform = String::capitalize)

private fun AnnotatedProductType.processElement(): String = foci.joinToString(separator = "\n") { focus ->
  """
    |/**
    | * [$Lens] that can see into ${sourceClassName.removeBackticks()} and focus in its property ${focus.lensParamName()} [${focus.className.removeBackticks()}]
    | */
    |inline val $sourceClassName.Companion.${focus.lensParamName()}: $Lens<$sourceClassName, ${focus.className}> inline get()= $Lens(
    |  get = { $sourceName: $sourceClassName -> $sourceName.${focus.paramName.plusIfNotBlank(prefix = "`", postfix = "`")} },
    |  set = { value: ${focus.className} ->
    |    { $sourceName: $sourceClassName ->
    |      $sourceName.copy(${focus.paramName.plusIfNotBlank(prefix = "`", postfix = "`")} = value)
    |    }
    |  }
    |)
    |""".trimMargin()
}

fun Focus.lensParamName(): String = when (this) {
  is NullableFocus -> "nullable${paramName.toUpperCamelCase()}"
  is OptionFocus -> "option${paramName.toUpperCamelCase()}"
  is NonNullFocus -> paramName
}
