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

private fun processElement(ele: ADT, foci: List<Focus>): String =
  foci.joinToString(separator = "\n") { focus ->
    """
  |inline val ${ele.sourceClassName}.Companion.${focus.lensParamName()}: $Lens<${ele.sourceClassName}, ${focus.className}> inline get()= $Lens(
  |  get = { ${ele.sourceName}: ${ele.sourceClassName} -> ${ele.sourceName}.${focus.paramName.plusIfNotBlank(prefix = "`", postfix = "`")} },
  |  set = { ${ele.sourceName}: ${ele.sourceClassName}, value: ${focus.className} -> ${ele.sourceName}.copy(${focus.paramName.plusIfNotBlank(prefix = "`", postfix = "`")} = value) }
  |)
  |""".trimMargin()
  }

fun Focus.lensParamName(): String =
  when (this) {
    is NullableFocus -> "nullable${paramName.toUpperCamelCase()}"
    is OptionFocus -> "option${paramName.toUpperCamelCase()}"
    is NonNullFocus -> paramName
  }
