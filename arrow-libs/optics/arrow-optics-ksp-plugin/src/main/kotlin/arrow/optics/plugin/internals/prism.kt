package arrow.optics.plugin.internals

internal fun generatePrisms(ele: ADT, target: PrismTarget) =
  Snippet(
    `package` = ele.packageNameAsString,
    name = ele.qualifiedNameOrSimpleName,
    imports =
      setOf("import arrow.core.left", "import arrow.core.right", "import arrow.core.identity"),
    content = processElement(ele, target.foci)
  )

private fun processElement(ele: ADT, foci: List<Focus>): String =
  foci.joinToString(separator = "\n\n") { focus ->
    """
  |inline val ${ele.qualifiedNameOrSimpleName}.Companion.${focus.paramName}: $Prism<${ele.qualifiedNameOrSimpleName}, ${focus.className}> inline get()= $Prism(
  |  getOrModify = { ${ele.simpleName}: ${ele.qualifiedNameOrSimpleName} ->
  |    when (${ele.simpleName}) {
  |      is ${focus.className} -> ${ele.simpleName}.right()
  |      else -> ${ele.simpleName}.left()
  |    }
  |  },
  |  reverseGet = ::identity
  |)
  |""".trimMargin()
  }
