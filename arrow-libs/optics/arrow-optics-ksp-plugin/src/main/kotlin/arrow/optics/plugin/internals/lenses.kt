package arrow.optics.plugin.internals

import java.util.Locale

internal fun generateLenses(ele: ADT, target: LensTarget) =
  Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processElement(ele, target.foci)
  )

private fun String.toUpperCamelCase(): String =
  split(" ")
    .joinToString(
      "",
      transform = {
        it.replaceFirstChar {
          if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
      }
    )

private fun processElement(adt: ADT, foci: List<Focus>): String =
  foci.joinToString(separator = "\n") { focus ->
    """
  |${adt.visibilityModifierName} inline val ${adt.sourceClassName}.Companion.${focus.lensParamName()}: $Lens<${adt.sourceClassName}, ${focus.className}> inline get()= $Lens(
  |  get = { ${adt.sourceName}: ${adt.sourceClassName} -> ${adt.sourceName}.${
      focus.paramName.plusIfNotBlank(
        prefix = "`",
        postfix = "`"
      )
    } },
  |  set = { ${adt.sourceName}: ${adt.sourceClassName}, value: ${focus.className} -> ${adt.sourceName}.copy(${
      focus.paramName.plusIfNotBlank(
        prefix = "`",
        postfix = "`"
      )
    } = value) }
  |)
  |""".trimMargin()
  }

fun Focus.lensParamName(): String =
  when (this) {
    is NullableFocus -> "nullable${paramName.toUpperCamelCase()}"
    is OptionFocus -> "option${paramName.toUpperCamelCase()}"
    is NonNullFocus -> paramName
  }
