package arrow.optics

import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank

fun generateIsos(annotatedOptic: AnnotatedOptic, isoOptic: IsoOptic) = Snippet(
  content = processElement(annotatedOptic, isoOptic)
)

private fun processElement(iso: AnnotatedOptic, isoOptic: IsoOptic): String {
  val foci = isoOptic.foci
  val hasTupleFocus = foci.size > 1
  val letters = ('a'..'v').toList()

  fun tupleConstructor() =
    foci.joinToString(prefix = "$Tuple${foci.size}(", postfix = ")", transform = { "${iso.sourceName}.${it.paramName.plusIfNotBlank(prefix = "`", postfix = "`")}" })

  fun focusType() =
    if (hasTupleFocus) isoOptic.targetNames.joinToString(prefix = "$Tuple${foci.size}<", postfix = ">")
    else isoOptic.targetNames.first()

  fun classConstructorFromTuple(sourceClassName: String, propertiesSize: Int) =
    (0 until propertiesSize).joinToString(prefix = "$sourceClassName(", postfix = ")", transform = { "tuple.${letters[it]}" })

  val get = if (hasTupleFocus) tupleConstructor() else "${iso.sourceName}.${isoOptic.foci.first().paramName}"
  val reverseGet = if (hasTupleFocus) "tuple: ${focusType()} -> ${classConstructorFromTuple(iso.sourceClassName, foci.size)}" else "${iso.sourceClassName}(it)"

  return """
        |inline val ${iso.sourceClassName}.Companion.iso: $Iso<${iso.sourceClassName}, ${focusType()}> get()= $Iso(
        |        get = { ${iso.sourceName}: ${iso.sourceClassName} -> $get },
        |        reverseGet = { $reverseGet }
        |)
        |""".trimMargin()
}
