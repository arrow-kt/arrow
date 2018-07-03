package arrow.optics

import arrow.common.utils.simpleName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank

fun generateIsos(ele: AnnotatedElement, target: IsoTarget) = Snippet(
  `package` = ele.packageName,
  name = ele.classData.simpleName,
  content = processElement(ele, target)
)

inline val Target.targetNames inline get() = foci.map(Focus::className)

private fun processElement(iso: AnnotatedElement, target: Target): String {
  val foci = target.foci
  val hasTupleFocus = foci.size > 1
  val letters = ('a'..'v').toList()

  fun tupleConstructor() =
    foci.joinToString(prefix = "$Tuple${foci.size}(", postfix = ")", transform = { "${iso.sourceName}.${it.paramName.plusIfNotBlank(prefix = "`", postfix = "`")}" })

  fun focusType() =
    if (hasTupleFocus) target.targetNames.joinToString(prefix = "$Tuple${foci.size}<", postfix = ">")
    else target.targetNames.first()

  fun classConstructorFromTuple(sourceClassName: String, propertiesSize: Int) =
    (0 until propertiesSize).joinToString(prefix = "$sourceClassName(", postfix = ")", transform = { "tuple.${letters[it]}" })

  val get = if (hasTupleFocus) tupleConstructor() else "${iso.sourceName}.${foci.first().paramName}"
  val reverseGet = if (hasTupleFocus) "tuple: ${focusType()} -> ${classConstructorFromTuple(iso.sourceClassName, foci.size)}" else "${iso.sourceClassName}(it)"

  return """
        |inline val ${iso.sourceClassName}.Companion.iso: $Iso<${iso.sourceClassName}, ${focusType()}> inline get()= $Iso(
        |  get = { ${iso.sourceName}: ${iso.sourceClassName} -> $get },
        |  reverseGet = { $reverseGet }
        |)
        |""".trimMargin()
}
