package arrow.optics.plugin.internals

internal fun generateOptionals(ele: ADT, target: OptionalTarget) =
  Snippet(
    `package` = ele.packageNameAsString,
    name = ele.qualifiedNameOrSimpleName,
    imports =
      setOf("import arrow.core.left", "import arrow.core.right", "import arrow.core.toOption"),
    content = processElement(ele, target.foci)
  )

private fun processElement(ele: ADT, foci: List<Focus>): String =
  foci.joinToString(separator = "\n") { focus ->
    fun getOrModifyF(toNullable: String = "") =
      "{ ${ele.simpleName}: ${ele.qualifiedNameOrSimpleName} -> ${ele.simpleName}.${
        focus.paramName.plusIfNotBlank(
          prefix = "`",
          postfix = "`"
        )
      }$toNullable?.right() ?: ${ele.simpleName}.left() }"
    fun setF(fromNullable: String = "") =
      "${ele.simpleName}.copy(${focus.paramName.plusIfNotBlank(prefix = "`", postfix = "`")} = value$fromNullable)"

    val (targetClassName, getOrModify, set) =
      when (focus) {
        is NullableFocus -> Triple(focus.nonNullClassName, getOrModifyF(), setF())
        is OptionFocus ->
          Triple(focus.nestedClassName, getOrModifyF(".orNull()"), setF(".toOption()"))
        is NonNullFocus -> return@joinToString ""
      }

    """
      |inline val ${ele.qualifiedNameOrSimpleName}.Companion.${focus.paramName}: $Optional<${ele.qualifiedNameOrSimpleName}, $targetClassName> inline get()= $Optional(
      |  getOrModify = $getOrModify,
      |  set = { ${ele.simpleName}: ${ele.qualifiedNameOrSimpleName}, value: $targetClassName -> $set }
      |)
      |""".trimMargin()
  }
