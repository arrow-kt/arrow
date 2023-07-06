package arrow.optics.plugin.internals

import arrow.optics.plugin.OpticsProcessorOptions

internal fun OpticsProcessorOptions.generateLenses(ele: ADT, target: LensTarget) =
  Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processElement(ele, target.foci),
  )

private fun OpticsProcessorOptions.processElement(adt: ADT, foci: List<Focus>): String {
  val sourceClassNameWithParams = "${adt.sourceClassName}${adt.angledTypeParameters}"
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
  |  set = { ${adt.sourceName}: $sourceClassNameWithParams, value: ${focus.className} -> ${adt.sourceName}.copy(${
      focus.paramName.plusIfNotBlank(
        prefix = "`",
        postfix = "`",
      )
    } = value) }
  |)
  |
    """.trimMargin()
  }
}

fun Focus.lensParamName(): String = paramName
