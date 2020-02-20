package arrow.optics

import arrow.common.utils.simpleName

fun generatePrisms(ele: AnnotatedElement, target: PrismTarget) = Snippet(
  `package` = ele.packageName,
  name = ele.classData.simpleName,
  imports = setOf("import arrow.core.left", "import arrow.core.right", "import arrow.core.identity"),
  content = processElement(ele, target.foci)
)

private fun processElement(ele: AnnotatedElement, foci: List<Focus>): String = foci.joinToString(separator = "\n\n") { focus ->
  """
  |inline val ${ele.sourceClassName}.Companion.${focus.paramName}: $Prism<${ele.sourceClassName}, ${focus.className}> inline get()= $Prism(
  |  getOrModify = { ${ele.sourceName}: ${ele.sourceClassName} ->
  |    when (${ele.sourceName}) {
  |      is ${focus.className} -> ${ele.sourceName}.right()
  |      else -> ${ele.sourceName}.left()
  |    }
  |  },
  |  reverseGet = ::identity
  |)
  |""".trimMargin()
}
