package arrow.optics.plugin.internals

import arrow.optics.plugin.OpticsProcessorOptions
import java.util.Locale

internal fun OpticsProcessorOptions.generateLenses(ele: ADT, target: LensTarget) =
  Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processElement(ele, target.foci),
  )

private fun String.toUpperCamelCase(): String =
  split(" ")
    .joinToString(
      "",
      transform = {
        it.replaceFirstChar {
          if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
      },
    )

private fun OpticsProcessorOptions.processElement(adt: ADT, foci: List<Focus>): String {
  val sourceClassNameWithParams = "${adt.sourceClassName}${adt.angledTypeParameters}"

  val setBody = { focus: Focus ->
    val setBodyCopy = """${adt.sourceName}.copy(${
      focus.paramName.plusIfNotBlank(
        prefix = "`",
        postfix = "`",
      )
    } = value)"""
    when {
      focus.subclasses.isNotEmpty() -> {
        """when(${adt.sourceName}) {
        |${focus.subclasses.joinToString(separator = "\n") { "is $it -> $setBodyCopy" }}
        |}
        |
        """.trimMargin()
      }

      else -> setBodyCopy
    }
  }
  return foci.joinToString(separator = "\n") { focus ->
    val firstLine = when {
      adt.typeParameters.isEmpty() ->
        "${adt.visibilityModifierName} $inlineText val ${adt.sourceClassName}.Companion.${focus.lensParamName()}: $Lens<${adt.sourceClassName}, ${focus.className}> $inlineText get()"
      else ->
        "${adt.visibilityModifierName} $inlineText fun ${adt.angledTypeParameters} ${adt.sourceClassName}.Companion.${focus.lensParamName()}(): $Lens<$sourceClassNameWithParams, ${focus.className}>"
    }
    """
      |$firstLine = $Lens(
      |  get = { ${adt.sourceName}: $sourceClassNameWithParams -> ${adt.sourceName}.${
      focus.paramName.plusIfNotBlank(
        prefix = "`",
        postfix = "`",
      )
    } },
      |  set = { ${adt.sourceName}: $sourceClassNameWithParams, value: ${focus.className} ->
      |  ${setBody(focus)}
      |}
      |)
      |
    """.trimMargin()
  }
}

fun Focus.lensParamName(): String =
  when (this) {
    is NullableFocus -> "nullable${paramName.toUpperCamelCase()}"
    is OptionFocus -> "option${paramName.toUpperCamelCase()}"
    is NonNullFocus -> paramName
  }
