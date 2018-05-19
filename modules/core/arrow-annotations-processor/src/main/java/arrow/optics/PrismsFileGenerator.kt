package arrow.optics

fun generatePrisms(annotatedOptic: AnnotatedOptic, isoOptic: PrismOptic) = Snippet(
  imports = setOf("import arrow.core.left", "import arrow.core.right"),
  content = processElement(annotatedOptic, isoOptic)
)

private fun processElement(ele: AnnotatedOptic, isoOptic: PrismOptic): String = isoOptic.foci.joinToString(separator = "\n\n") { focus ->
  """
  |inline val ${ele.sourceClassName}.Companion.${focus.paramName}: $Prism<${ele.sourceClassName}, ${focus.className}> inline get()= $Prism(
  |  getOrModify = { ${ele.sourceName}: ${ele.sourceClassName} ->
  |    when (${ele.sourceName}) {
  |      is ${focus.className} -> ${ele.sourceName}.right()
  |      else -> ${ele.sourceName}.left()
  |    }
  |  },
  |  reverseGet = { it }
  |)
  |""".trimMargin()
}
