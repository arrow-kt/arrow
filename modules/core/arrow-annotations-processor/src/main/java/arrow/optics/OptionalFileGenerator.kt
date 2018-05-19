package arrow.optics

import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank

fun generateOptionals(annotatedOptic: AnnotatedOptic, optic: OptionalOptic) = Snippet(
  imports = setOf("import arrow.core.left", "import arrow.core.right", "import arrow.core.toOption"),
  content = processElement(annotatedOptic, optic)
)

private fun processElement(ele: AnnotatedOptic, optic: OptionalOptic): String = optic.foci.joinToString(separator = "\n") { focus ->
  fun getOrModifyF(toNullable: String = "") = "{ ${ele.sourceName}: ${ele.sourceClassName} -> ${ele.sourceName}.${focus.paramName.plusIfNotBlank(prefix = "`", postfix = "`")}$toNullable?.right() ?: ${ele.sourceName}.left() }"
  fun setF(fromNullable: String = "") = "${ele.sourceName}.copy(${focus.paramName.plusIfNotBlank(prefix = "`", postfix = "`")} = value$fromNullable)"

  val (targetClassName, getOrModify, set) = when (focus) {
    is NullableFocus -> Triple(focus.nonNullClassName, getOrModifyF(), setF())
    is OptionFocus -> Triple(focus.nestedClassName, getOrModifyF(".orNull()"), setF(".toOption()"))
    is NonNullFocus -> return@joinToString ""
  }

  """
      |inline val ${ele.sourceClassName}.Companion.${focus.paramName}: $Optional<${ele.sourceClassName}, $targetClassName> inline get()= $Optional(
      |  getOrModify = $getOrModify,
      |  set = { value: $targetClassName ->
      |    { ${ele.sourceName}: ${ele.sourceClassName} ->
      |      $set
      |    }
      |  }
      |)
      |""".trimMargin()
}