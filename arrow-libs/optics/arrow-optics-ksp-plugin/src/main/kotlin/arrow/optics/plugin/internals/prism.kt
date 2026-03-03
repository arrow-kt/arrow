package arrow.optics.plugin.internals

import arrow.optics.plugin.OpticsProcessorOptions

internal fun OpticsProcessorOptions.generatePrisms(ele: ADT, target: PrismTarget) = Snippet(
  `package` = ele.packageName,
  name = ele.simpleName,
  imports =
  setOf("import arrow.core.left", "import arrow.core.right", "import arrow.core.identity"),
  content = processElement(ele, target.foci),
)

private fun OpticsProcessorOptions.processElement(ele: ADT, foci: List<Focus>): String = foci.joinToString(separator = "\n\n") { focus ->
  val sourceClassNameWithParams =
    focus.refinedType?.qualifiedString() ?: "${ele.sourceClassName}${ele.angledTypeParameterNames}"
  val joinedTypeParams = focus.refinedArguments.union(focus.targetTypeParameters.orEmpty())
  val angledTypeParameters = when {
    joinedTypeParams.isEmpty() -> ""
    else -> joinedTypeParams.joinToString(prefix = "<", separator = ",", postfix = ">")
  }
  val firstLine = when {
    ele.typeParameters.isEmpty() ->
      "${ele.visibilityModifierName} $inlineText val ${ele.sourceClassName}.Companion.${focus.escapedParamName}: $Prism<${ele.sourceClassName}, ${focus.targetClassNameWithParameters}> $inlineText get()"

    else ->
      "${ele.visibilityModifierName} $inlineText fun $angledTypeParameters ${ele.sourceClassName}.Companion.${focus.escapedParamName}(): $Prism<$sourceClassNameWithParams, ${focus.targetClassNameWithParameters}>"
  }
  "$firstLine = $Prism.instanceOf()"
}
