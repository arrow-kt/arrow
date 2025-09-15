package arrow.optics.plugin.internals

import arrow.optics.plugin.OpticsProcessorOptions

internal fun OpticsProcessorOptions.generateCopy(ele: ADT, target: CopyTarget) =
  Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processElement(ele, target.companionName),
    imports = setOf("import arrow.optics.copy"),
  )

private fun OpticsProcessorOptions.processElement(adt: ADT, companionName: String): String {
  val sourceClassNameWithParams = "${adt.sourceClassName}${adt.angledTypeParameterNames}"
  return """
  | ${adt.visibilityModifierName} fun ${adt.angledTypeParameters} $sourceClassNameWithParams.copy(
  |   block: context(arrow.optics.Copy<$sourceClassNameWithParams>) $companionName.($sourceClassNameWithParams) -> Unit
  | ): $sourceClassNameWithParams {
  |   val me = this
  |   return me.copy { block(this, $companionName, me) }
  | }
  """.trimMargin()
}
