package arrow.optics.plugin.internals

internal fun generateLenses(ele: ADT, target: LensTarget) =
  Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processElement(ele, target.foci)
  )

private fun processElement(adt: ADT, foci: List<Focus>): String {
  val sourceClassNameWithParams = "${adt.sourceClassName}${adt.angledTypeParameters}"
  return foci.joinToString(separator = "\n") { focus ->
    val firstLine = when {
      adt.typeParameters.isEmpty() ->
        "${adt.visibilityModifierName} inline val ${adt.sourceClassName}.Companion.${focus.lensParamName()}: $Lens<${adt.sourceClassName}, ${focus.className}> inline get()"
      else ->
        "${adt.visibilityModifierName} inline fun ${adt.angledTypeParameters} ${adt.sourceClassName}.Companion.${focus.lensParamName()}(): $Lens<$sourceClassNameWithParams, ${focus.className}>"
    }
    """
  |$firstLine = $Lens(
  |  get = { ${adt.sourceName}: $sourceClassNameWithParams -> ${adt.sourceName}.${
      focus.paramName.plusIfNotBlank(
        prefix = "`",
        postfix = "`"
      )
    } },
  |  set = { ${adt.sourceName}: $sourceClassNameWithParams, value: ${focus.className} -> ${adt.sourceName}.copy(${
      focus.paramName.plusIfNotBlank(
        prefix = "`",
        postfix = "`"
      )
    } = value) }
  |)
  |""".trimMargin()
  }
}

fun Focus.lensParamName(): String = paramName
