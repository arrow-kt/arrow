package arrow.optics.plugin.internals

import java.util.Locale

internal fun generateLenses(ele: ADT, target: LensTarget) =
  Snippet(
    `package` = ele.packageNameAsString,
    name = ele.qualifiedNameOrSimpleName,
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
  |inline val ${adt.qualifiedNameOrSimpleName}.Companion.${focus.lensParamName()}: $Lens<${adt.qualifiedNameOrSimpleName}, ${focus.className}> inline get()= $Lens(
  |  get = { ${adt.simpleName}: ${adt.qualifiedNameOrSimpleName} -> ${adt.simpleName}.${
      focus.paramName.plusIfNotBlank(
        prefix = "`",
        postfix = "`"
      )
    } },
  |  set = { ${adt.simpleName}: ${adt.qualifiedNameOrSimpleName}, value: ${focus.className} -> ${adt.simpleName}.copy(${
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
