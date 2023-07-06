package arrow.optics.plugin.internals

import arrow.optics.plugin.OpticsProcessorOptions
import arrow.optics.plugin.isValue

internal fun OpticsProcessorOptions.generateIsos(ele: ADT, target: IsoTarget) =
  Snippet(`package` = ele.packageName, name = ele.simpleName, content = processElement(ele, target))

inline val Target.targetNames
  inline get() = foci.map(Focus::className)

private fun OpticsProcessorOptions.processElement(iso: ADT, target: Target): String {
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
    "twentySecond",
  )

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

  val isoName = if (iso.declaration.isValue) target.foci.first().paramName else "iso"
  val sourceClassNameWithParams = "${iso.sourceClassName}${iso.angledTypeParameters}"
  val firstLine = when {
    iso.typeParameters.isEmpty() ->
      "${iso.visibilityModifierName} $inlineText val ${iso.sourceClassName}.Companion.$isoName: $Iso<${iso.sourceClassName}, ${focusType()}> $inlineText get()"
    else ->
      "${iso.visibilityModifierName} $inlineText fun ${iso.angledTypeParameters} ${iso.sourceClassName}.Companion.$isoName(): $Iso<$sourceClassNameWithParams, ${focusType()}>"
  }

  return """
        |$firstLine = $Iso(
        |  get = { ${iso.sourceName}: $sourceClassNameWithParams -> ${tupleConstructor()} },
        |  reverseGet = { ${classConstructorFromTuple()} }
        |)
        |
  """.trimMargin()
}
