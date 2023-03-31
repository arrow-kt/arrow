package arrow.optics.plugin.internals

internal fun generateIsos(ele: ADT, target: IsoTarget) =
  Snippet(`package` = ele.packageName, name = ele.simpleName, content = processElement(ele, target.foci.first()))

private fun processElement(adt: ADT, focus: Focus): String {
  val sourceClassNameWithParams = "${adt.sourceClassName}${adt.angledTypeParameters}"
  val firstLine = when {
    adt.typeParameters.isEmpty() ->
      "${adt.visibilityModifierName} inline val ${adt.sourceClassName}.Companion.${focus.paramName}: $Iso<${adt.sourceClassName}, ${focus.className}> inline get()"
    else ->
      "${adt.visibilityModifierName} inline fun ${adt.angledTypeParameters} ${adt.sourceClassName}.Companion.${focus.paramName}(): $Iso<$sourceClassNameWithParams, ${focus.className}>"
  }
  val fineParamName = focus.paramName.plusIfNotBlank(
    prefix = "`",
    postfix = "`"
  )
    return """
  |$firstLine = $Iso(
  |  get = { ${adt.sourceName}: $sourceClassNameWithParams -> ${adt.sourceName}.$fineParamName },
  |  reverseGet = { $fineParamName: ${focus.className} -> ${adt.sourceClassName}($fineParamName) }
  |)
  |""".trimMargin()
}
