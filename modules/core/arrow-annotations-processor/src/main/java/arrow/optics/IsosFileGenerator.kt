package arrow.optics

import arrow.common.utils.knownError
import arrow.common.utils.removeBackticks
import arrow.common.utils.simpleName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank
import arrow.optics.Optic.*

val AnnotatedType.isoSnippet
  get() = when (this) {
    is AnnotatedProductType -> Snippet(
      `package` = packageName,
      name = classData.simpleName,
      content = processElement()
    )
    is AnnotatedSumType -> knownError(element.isoErrorMessage, element)
    is AnnotatedFunctionType -> knownError(element.isoErrorMessage, element)
  }

private fun AnnotatedProductType.processElement(): String {
  if (foci.size > 22) knownError(element.isoTooBigErrorMessage, element)

  val hasTupleFocus = foci.size > 1
  val letters = ('a'..'v').toList()
  val fociClassNames = foci.map(Focus::className)

  fun tupleConstructor() =
    foci.joinToString(prefix = "$Tuple${foci.size}(", postfix = ")", transform = { "$sourceName.${it.paramName.plusIfNotBlank(prefix = "`", postfix = "`")}" })

  fun focusType() =
    if (hasTupleFocus) fociClassNames.joinToString(prefix = "$Tuple${foci.size}<", postfix = ">")
    else fociClassNames.first()

  fun classConstructorFromTuple(sourceClassName: String, propertiesSize: Int) =
    (0 until propertiesSize).joinToString(prefix = "$sourceClassName(", postfix = ")", transform = { "tuple.${letters[it]}" })

  val get = if (hasTupleFocus) tupleConstructor() else "$sourceName.${foci.first().paramName}"
  val reverseGet = if (hasTupleFocus) "tuple: ${focusType()} -> ${classConstructorFromTuple(sourceClassName, foci.size)}" else "$sourceClassName(it)"

  return """
    |/**
    | * [$Iso] that defines the equality between ${sourceClassName.removeBackticks()} and its generic representation [${focusType().removeBackticks()}]
    | */
    |inline val $sourceClassName.Companion.iso: $Iso<$sourceClassName, ${focusType()}> inline get()= $Iso(
    |  get = { $sourceName: $sourceClassName -> $get },
    |  reverseGet = { $reverseGet }
    |)
    |""".trimMargin()
}
