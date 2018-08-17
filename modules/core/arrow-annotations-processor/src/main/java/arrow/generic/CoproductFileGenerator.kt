package arrow.generic

import java.io.File

private val genericsToClassNames = mapOf(
        "A" to "First",
        "B" to "Second",
        "C" to "Third",
        "D" to "Fourth",
        "E" to "Fifth",
        "F" to "Sixth",
        "G" to "Seventh",
        "H" to "Eighth",
        "I" to "Ninth",
        "J" to "Tenth",
        "K" to "Eleventh",
        "L" to "Twelfth",
        "M" to "Thirteenth",
        "N" to "Fourteenth",
        "O" to "Fifteenth",
        "P" to "Sixteenth",
        "Q" to "Seventeenth",
        "R" to "Eighteenth",
        "S" to "Nineteenth",
        "T" to "Twentieth",
        "U" to "TwentyFirst",
        "V" to "TwentySecond"
)

fun generateCoproducts(destination: File) {
    for (size in 2 until genericsToClassNames.size + 1) {
        val generics = genericsToClassNames.keys.toList().take(size)

        val fileString = listOf(
                packageName(size),
                imports(),
                coproductClassDeclaration(generics),
                coproductOfConstructors(generics),
                copExtensionConstructors(generics),
                selectFunctions(generics),
                foldFunction(generics)
        ).joinToString(separator = "\n")

        val parentDir = File(destination, "arrow/generic/coproduct$size")
                .also { it.mkdirs() }

        File(parentDir, "Coproduct$size.kt")
                .also { it.createNewFile() }
                .printWriter()
                .use { it.println(fileString) }
    }
}

private fun packageName(size: Int) = "package arrow.generic.coproduct$size"

private fun imports() = """|
    |import arrow.core.Option
    |import arrow.core.toOption
    |import kotlin.Unit
|""".trimMargin()

private fun coproductClassDeclaration(generics: List<String>): String {
    val allGenerics = generics.joinToString()
    val parentClass = "Coproduct${generics.size}<$allGenerics>"
    val parentClassDeclaration = "sealed class $parentClass\n"

    val subClasses = generics.map {
        "internal data class ${genericsToClassNames[it]}<$allGenerics>(val ${it.toLowerCase()}: $it): $parentClass()\n"
    }

    return (listOf(parentClassDeclaration) + subClasses)
            .joinToString(separator = "")
}

private fun coproductOfConstructors(generics: List<String>): String {
    val size = generics.size
    val genericsDeclaration = generics.joinToString(separator = ", ")

    return generics.mapIndexed { index, generic ->
        val params = listOf("${generic.toLowerCase()} : $generic") + additionalParameters(generics.indexOf(generic))

        "fun <$genericsDeclaration> coproductOf(${params.joinToString()}): Coproduct$size<$genericsDeclaration> = ${genericsToClassNames[generic]}(${generic.toLowerCase()})\n"
    }.joinToString(separator = "")
}

private fun copExtensionConstructors(generics: List<String>): String {
    val size = generics.size
    val genericsDeclaration = generics.joinToString(separator = ", ")

    return generics.mapIndexed { index, generic ->
        val params = additionalParameters(generics.indexOf(generic)).joinToString()

        "fun <$genericsDeclaration> $generic.cop($params): Coproduct$size<$genericsDeclaration> = coproductOf<$genericsDeclaration>(this)\n"
    }.joinToString(separator = "")
}

private fun selectFunctions(generics: List<String>): String {
    val size = generics.size

    return generics.mapIndexed { index, generic ->
        val params = additionalParameters(generics.indexOf(generic)).joinToString()
        val receiverGenerics = generics.map { if (it == generic) generic else "*" }
                .joinToString(separator = ", ")

        "fun <$generic> Coproduct$size<$receiverGenerics>.select($params): Option<$generic> = (this as? ${genericsToClassNames[generic]})?.${generic.toLowerCase()}.toOption()\n"
    }.joinToString(separator = "")
}

private fun foldFunction(generics: List<String>): String {
    val size = generics.size
    val genericsDeclaration = generics.joinToString(separator = ", ")

    val functionGenerics = (generics + "RESULT").joinToString()

    val params = generics.map {
        "   ${it.toLowerCase()}: ($it) -> RESULT"
    }.joinToString(separator = ",\n")

    val cases = generics.map {
        "       is ${genericsToClassNames[it]} -> ${it.toLowerCase()}(this.${it.toLowerCase()})"
    }.joinToString(separator = "\n")

    return """
        |fun <$functionGenerics> Coproduct$size<$genericsDeclaration>.fold(
        |$params
        |): RESULT {
        |   return when (this) {
        |$cases
        |   }
        |}
    |""".trimMargin()
}

private fun additionalParameters(count: Int): List<String> = List(count) { "dummy$it: Unit = Unit" }