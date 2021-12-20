package arrow.optics.plugin.internals

internal fun generateIsos(ele: ADT, target: IsoTarget) =
  Snippet(`package` = ele.packageName, name = ele.simpleName, content = processElement(ele, target))

inline val Target.targetNames
  inline get() = foci.map(Focus::className)

private fun processElement(iso: ADT, target: Target): String {
  val foci = target.foci
  val letters = ('a'..'v').toList()

  fun Focus.format(): String =
    "${iso.sourceName}.${paramName.plusIfNotBlank(prefix = "`", postfix = "`")}"

  fun tupleConstructor() =
    when (foci.size) {
      1 -> "${iso.sourceName}.${foci.first().paramName}"
      2 -> "$Pair(${foci[0].format()}, ${foci[1].format()})"
      3 -> "$Triple(${foci[0].format()}, ${foci[1].format()}, ${foci[2].format()})"
      else ->
        foci.joinToString(prefix = "$Tuple${foci.size}(", postfix = ")", transform = Focus::format)
    }

  fun focusType() =
    when (foci.size) {
      1 -> target.targetNames.first()
      2 -> target.targetNames.joinToString(prefix = "$Pair<", postfix = ">")
      3 -> target.targetNames.joinToString(prefix = "$Triple<", postfix = ">")
      else -> target.targetNames.joinToString(prefix = "$Tuple${foci.size}<", postfix = ">")
    }

  fun classConstructorFromTuple() =
    when (foci.size) {
      1 -> "${iso.sourceClassName}(it)"
      2 -> "pair: ${focusType()} -> ${iso.sourceClassName}(pair.first, pair.second)"
      3 ->
        "triple: ${focusType()} -> ${iso.sourceClassName}(triple.first, triple.second, triple.third)"
      else ->
        "tuple: ${focusType()} -> ${(foci.indices).joinToString(prefix = "${iso.sourceClassName}(", postfix = ")", transform = { "tuple.${letters[it]}" })}"
    }

  return """
        |inline val ${iso.sourceClassName}.Companion.iso: $Iso<${iso.sourceClassName}, ${focusType()}> inline get()= $Iso(
        |  get = { ${iso.sourceName}: ${iso.sourceClassName} -> ${tupleConstructor()} },
        |  reverseGet = { ${classConstructorFromTuple()} }
        |)
        |""".trimMargin()
}
