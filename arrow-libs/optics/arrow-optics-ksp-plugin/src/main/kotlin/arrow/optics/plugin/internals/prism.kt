package arrow.optics.plugin.internals

import arrow.optics.plugin.OpticsProcessorOptions

internal fun OpticsProcessorOptions.generatePrisms(ele: ADT, target: PrismTarget) =
  Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    imports =
    setOf("import arrow.core.left", "import arrow.core.right", "import arrow.core.identity"),
    content = processElement(ele, target.foci),
  )

private fun OpticsProcessorOptions.processElement(ele: ADT, foci: List<Focus>): String {
  return foci.joinToString(separator = "\n\n") { focus ->
    val sourceClassNameWithParams =
      focus.refinedType?.qualifiedString() ?: "${ele.sourceClassName}${ele.angledTypeParameters}"
    val angledTypeParameters = when {
      focus.refinedArguments.isEmpty() -> ""
      else -> focus.refinedArguments.joinToString(prefix = "<", separator = ",", postfix = ">")
    }
    val firstLine = when {
      ele.typeParameters.isEmpty() ->
        "${ele.visibilityModifierName} $inlineText val ${ele.sourceClassName}.Companion.${focus.paramName}: $Prism<${ele.sourceClassName}, ${focus.className}> $inlineText get()"
      else ->
        "${ele.visibilityModifierName} $inlineText fun $angledTypeParameters ${ele.sourceClassName}.Companion.${focus.paramName}(): $Prism<$sourceClassNameWithParams, ${focus.className}>"
    }

    val elseBranch = if (focus.onlyOneSealedSubclass) {
      ""
    } else """
  |      else -> ${ele.sourceName}.left()
    """.trimMargin()

    """
  |$firstLine = $Prism(
  |  getOrModify = { ${ele.sourceName}: $sourceClassNameWithParams ->
  |    when (${ele.sourceName}) {
  |      is ${focus.className} -> ${ele.sourceName}.right()
  |      $elseBranch
  |    }
  |  },
  |  reverseGet = ::identity
  |)
  |
    """.trimMargin()
  }
}
