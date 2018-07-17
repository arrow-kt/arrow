package arrow.optics

import arrow.common.utils.knownError
import arrow.common.utils.removeBackticks
import arrow.common.utils.simpleName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank
import arrow.optics.Optic.*

val AnnotatedType.optionalSnippet
  get() = when (this) {
    is AnnotatedProductType -> Snippet(
      `package` = packageName,
      name = classData.simpleName,
      imports = setOf("import arrow.core.left", "import arrow.core.right", "import arrow.core.toOption"),
      content = processElement()
    )
    is AnnotatedSumType -> knownError(element.optionalErrorMessage, element)
    is AnnotatedFunctionType -> knownError(element.optionalErrorMessage, element)
  }

private fun AnnotatedProductType.processElement(): String = foci.joinToString(separator = "\n") { focus ->
  fun getOrModifyF(toNullable: String = "") = "{ $sourceName: $sourceClassName -> $sourceName.${focus.paramName.plusIfNotBlank(prefix = "`", postfix = "`")}$toNullable?.right() ?: $sourceName.left() }"
  fun setF(fromNullable: String = "") = "$sourceName.copy(${focus.paramName.plusIfNotBlank(prefix = "`", postfix = "`")} = value$fromNullable)"

  val (targetClassName, getOrModify, set) = when (focus) {
    is NullableFocus -> Triple(focus.nonNullClassName, getOrModifyF(), setF())
    is OptionFocus -> Triple(focus.nestedClassName, getOrModifyF(".orNull()"), setF(".toOption()"))
    is NonNullFocus -> return@joinToString ""
  }

  """
    |/**
    | * [$Optional] that can see into ${sourceClassName.removeBackticks()} and focus in its property ${focus.paramName} [${targetClassName.removeBackticks()}]
    | */
    |inline val $sourceClassName.Companion.${focus.paramName}: $Optional<$sourceClassName, $targetClassName> inline get()= $Optional(
    |  getOrModify = $getOrModify,
    |  set = { value: $targetClassName ->
    |    { $sourceName: $sourceClassName ->
    |      $set
    |    }
    |  }
    |)
    |""".trimMargin()
}