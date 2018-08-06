package arrow.generic

import java.io.File

private const val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

fun generateCoproducts(destination: File) {
    for (size in 2 until alphabet.length + 1) {
        val generics = alphabet.subSequence(0, size).toList()

        val fileString = listOf(
                packageName(size),
                imports(),
                coproductClassDeclaration(generics),
                coproductOfConstructors(generics),
                copExtensionConstructors(generics),
                selectFunctions(generics),
                foldFunction(generics),
                mapFunction(generics)
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
    |import java.lang.IllegalStateException
    |import kotlin.Unit
|""".trimMargin()

private fun coproductClassDeclaration(generics: List<Char>) = "data class Coproduct${generics.size}<${generics.joinToString()}>(val value: Any?)"

private fun coproductOfConstructors(generics: List<Char>): String {
    val size = generics.size
    val genericsDeclaration = generics.joinToString(separator = ", ")

    val result = StringBuilder()

    for (generic in generics) {
        val params = listOf("${generic.toLowerCase()} : $generic") + additionalParameters(generics.indexOf(generic))

        result.append(
                "fun <$genericsDeclaration> coproductOf(${params.joinToString()}) = Coproduct$size<$genericsDeclaration>(${generic.toLowerCase()})\n"
        )
    }

    return result.toString()
}

private fun copExtensionConstructors(generics: List<Char>): String {
    val genericsDeclaration = generics.joinToString(separator = ", ")

    val result = StringBuilder()

    for (generic in generics) {
        val params = additionalParameters(generics.indexOf(generic)).joinToString()

        result.append(
                "fun <$genericsDeclaration> $generic.cop($params) = coproductOf<$genericsDeclaration>(this)\n"
        )
    }

    return result.toString()
}

private fun selectFunctions(generics: List<Char>): String {
    val size = generics.size
    val result = StringBuilder()

    for (generic in generics) {
        val params = additionalParameters(generics.indexOf(generic)).joinToString()
        val receiverGenerics = generics.map { if (it == generic) generic.toString() else "*" }
                .joinToString(separator = ", ")

        result.append(
                "inline fun <reified $generic> Coproduct$size<$receiverGenerics>.select($params): Option<$generic> = (value as? $generic).toOption()\n"
        )
    }

    return result.toString()
}

private fun foldFunction(generics: List<Char>): String {
    val size = generics.size
    val genericsDeclaration = generics.joinToString(separator = ", ")

    val exceptionMessage = "Invalid Coproduct$size \$this"

    val functionGenerics = (generics.map { "reified $it" } + "RESULT").joinToString()

    val params = generics.map {
        "   ${it.toLowerCase()}: ($it) -> RESULT"
    }.joinToString(separator = ",\n")

    val cases = generics.map {
        "      is $it -> ${it.toLowerCase()}(value)"
    }.joinToString(separator = "\n")

    return """|
        |inline fun <$functionGenerics> Coproduct$size<$genericsDeclaration>.fold(
        |$params
        |): RESULT {
        |   return when (value) {
        |$cases
        |       else -> throw IllegalStateException("$exceptionMessage")
        |   }
        |}
    """.trimMargin()
}

private fun mapFunction(generics: List<Char>): String {
    val size = generics.size
    val genericsDeclaration = generics.joinToString(separator = ", ")

    val returnGenerics = generics.map { "$it" + 1 }
    val returnGenericsString = returnGenerics.joinToString()
    val functionGenerics = (generics.map { "reified $it" } + returnGenerics).joinToString()

    val params = generics.map {
        "   ${it.toLowerCase()}: ($it) -> $it" + 1
    }.joinToString(separator = ",\n")

    val lambdas = generics.map {
        "       { ${it.toLowerCase()}(it).cop<$returnGenericsString>() }"
    }.joinToString(separator = ",\n")

    return """|
        |inline fun <$functionGenerics> Coproduct$size<$genericsDeclaration>.map(
        |$params
        |): Coproduct$size<$returnGenericsString> {
        |   return fold(
        |$lambdas
        |   )
        |}
    """.trimMargin()
}

private fun additionalParameters(count: Int): List<String> {
    return List(count) { "dummy$it: Unit = Unit" }
}