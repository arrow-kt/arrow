package arrow.optics.plugin.internals

internal fun generateIsos(ele: ADT, target: IsoTarget) =
  Snippet(`package` = ele.packageNameAsString, name = ele.qualifiedNameOrSimpleName, content = processElement(ele, target))

inline val Target.targetNames
  inline get() = foci.map(Focus::className)

private fun processElement(iso: ADT, target: Target): String {
  val foci = target.foci
  val letters = listOf(
    "first",
    "second",
    "third",
    "fourth",
    "fifth",
    "sixth",
    "seventh",
    "eighth",
    "ninth",
    "tenth",
    "eleventh",
    "twelfth",
    "thirteenth",
    "fourteenth",
    "fifteenth",
    "sixteenth",
    "seventeenth",
    "eighteenth",
    "nineteenth",
    "twentieth",
    "twentyFirst",
    "twentySecond"
  )

  fun Focus.format(): String =
    "${iso.simpleName}.${paramName.plusIfNotBlank(prefix = "`", postfix = "`")}"

  fun tupleConstructor() =
    when (foci.size) {
      1 -> "${iso.simpleName}.${foci.first().paramName}"
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
      1 -> "${iso.qualifiedNameOrSimpleName}(it)"
      2 -> "pair: ${focusType()} -> ${iso.qualifiedNameOrSimpleName}(pair.first, pair.second)"
      3 ->
        "triple: ${focusType()} -> ${iso.qualifiedNameOrSimpleName}(triple.first, triple.second, triple.third)"
      else ->
        "tuple: ${focusType()} -> ${(foci.indices).joinToString(prefix = "${iso.qualifiedNameOrSimpleName}(", postfix = ")", transform = { "tuple.${letters[it]}" })}"
    }

  return """
        |inline val ${iso.qualifiedNameOrSimpleName}.Companion.iso: $Iso<${iso.qualifiedNameOrSimpleName}, ${focusType()}> inline get()= $Iso(
        |  get = { ${iso.simpleName}: ${iso.qualifiedNameOrSimpleName} -> ${tupleConstructor()} },
        |  reverseGet = { ${classConstructorFromTuple()} }
        |)
        |""".trimMargin()
}
