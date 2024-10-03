package arrow.optics.plugin.internals

import arrow.optics.plugin.OpticsProcessorOptions

internal fun OpticsProcessorOptions.generateIsos(ele: ADT, target: IsoTarget) =
  Snippet(`package` = ele.packageName, name = ele.simpleName, content = processElement(ele, target.foci.first()))

private fun OpticsProcessorOptions.processElement(adt: ADT, focus: Focus): String {
  val sourceClassNameWithParams = "${adt.sourceClassName}${adt.angledTypeParameters}"
  val firstLine = when {
    adt.typeParameters.isEmpty() ->
      "${adt.visibilityModifierName} $inlineText val ${adt.sourceClassName}.Companion.${focus.escapedParamName}: $Iso<${adt.sourceClassName}, ${focus.className}> $inlineText get()"
    else ->
      "${adt.visibilityModifierName} $inlineText fun ${adt.angledTypeParameters} ${adt.sourceClassName}.Companion.${focus.escapedParamName}(): $Iso<$sourceClassNameWithParams, ${focus.className}>"
  }
  return """
  |$firstLine = $Iso(
  |  get = { ${adt.sourceName}: $sourceClassNameWithParams -> ${adt.sourceName}.${focus.escapedParamName} },
  |  reverseGet = { ${focus.escapedParamName}: ${focus.className} -> ${adt.sourceClassName}(${focus.escapedParamName}) }
  |)
  |
  """.trimMargin()
}
