package arrow.optics.plugin.internals

import arrow.optics.plugin.OpticsProcessorOptions

internal fun OpticsProcessorOptions.generateLenses(ele: ADT, target: LensTarget): Snippet {
  val (lensType, lensImport) = ele.resolveTypeName(Lens)
  return Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processElement(ele, target.foci, lensType),
    imports = setOf(lensImport).filter { it.isNotBlank() }.toSet(),
  )
}

private fun OpticsProcessorOptions.processElement(adt: ADT, foci: List<Focus>, lensType: String): String {
  val sourceClassNameWithParams = "${adt.sourceClassName}${adt.angledTypeParameterNames}"

  val setBody = { focus: Focus ->
    val setBodyCopy = """${adt.sourceName}.copy(${
      focus.escapedParamName
    } = value)"""
    when {
      focus.subclasses.isNotEmpty() -> {
        """when(${adt.sourceName}) {
        |${focus.subclasses.joinToString(separator = "\n") { "is $it -> $setBodyCopy" }}
        |}
        |
        """.trimMargin()
      }

      else -> setBodyCopy
    }
  }
  return foci.joinToString(separator = "\n") { focus ->
    val firstLine = when {
      adt.typeParameters.isEmpty() ->
        "${adt.visibilityModifierName} $inlineText val ${adt.sourceClassName}.Companion.${focus.escapedParamName}: $lensType<${adt.sourceClassName}, ${focus.className}> $inlineText get()"

      else ->
        "${adt.visibilityModifierName} $inlineText fun ${adt.angledTypeParameters} ${adt.sourceClassName}.Companion.${focus.escapedParamName}(): $lensType<$sourceClassNameWithParams, ${focus.className}>"
    }
    """
      |$firstLine = $lensType(
      |  get = { ${adt.sourceName}: $sourceClassNameWithParams -> ${adt.sourceName}.${focus.escapedParamName} },
      |  set = { ${adt.sourceName}: $sourceClassNameWithParams, value: ${focus.className} ->
      |  ${setBody(focus)}
      |}
      |)
      |
    """.trimMargin()
  }
}
