package arrow.optics.plugin.internals

import arrow.optics.plugin.OpticsProcessorOptions

internal fun OpticsProcessorOptions.generateOptionals(ele: ADT, target: OptionalTarget) =
  Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    imports =
    setOf("import arrow.core.left", "import arrow.core.right", "import arrow.core.toOption"),
    content = processElement(ele, target.foci),
  )

private fun OpticsProcessorOptions.processElement(ele: ADT, foci: List<Focus>): String =
  foci.joinToString(separator = "\n") { focus ->

    val targetClassName = when (focus) {
      is NullableFocus -> focus.nonNullClassName
      is OptionFocus -> focus.nestedClassName
      is NonNullFocus -> return@joinToString ""
    }

    val sourceClassNameWithParams = "${ele.sourceClassName}${ele.angledTypeParameters}"
    val firstLine = when {
      ele.typeParameters.isEmpty() ->
        "${ele.visibilityModifierName} $inlineText val ${ele.sourceClassName}.Companion.${focus.paramName}: $Optional<${ele.sourceClassName}, $targetClassName> $inlineText get()"
      else ->
        "${ele.visibilityModifierName} $inlineText fun ${ele.angledTypeParameters} ${ele.sourceClassName}.Companion.${focus.paramName}(): $Optional<$sourceClassNameWithParams, $targetClassName>"
    }

    fun getOrModifyF(toNullable: String = "") =
      "{ ${ele.sourceName}: $sourceClassNameWithParams -> ${ele.sourceName}.${
        focus.paramName.plusIfNotBlank(
          prefix = "`",
          postfix = "`",
        )
      }$toNullable?.right() ?: ${ele.sourceName}.left() }"
    fun setF(fromNullable: String = "") =
      "${ele.sourceName}.copy(${focus.paramName.plusIfNotBlank(prefix = "`", postfix = "`")} = value$fromNullable)"

    val (getOrModify, set) =
      when (focus) {
        is NullableFocus -> Pair(getOrModifyF(), setF())
        is OptionFocus -> Pair(getOrModifyF(".orNull()"), setF(".toOption()"))
        is NonNullFocus -> return@joinToString ""
      }

    """
      |$firstLine = $Optional(
      |  getOrModify = $getOrModify,
      |  set = { ${ele.sourceName}: $sourceClassNameWithParams, value: $targetClassName -> $set }
      |)
      |
    """.trimMargin()
  }
