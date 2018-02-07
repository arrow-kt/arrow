package arrow.optics

import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank
import java.io.File

class OptionalFileGenerator(
        private val annotatedList: Collection<AnnotatedOptic>,
        private val generatedDir: File
) {

    private val optional = "arrow.optics.Optional"

    fun generate() = annotatedList.map(this::processElement)
            .filter { it.second.joinToString(separator = "").isNotEmpty() }
            .map { (element, funs) ->
                "${optionalsAnnotationClass.simpleName}.${element.classData.`package`}.${element.type.simpleName.toString().toLowerCase()}.kt" to
                        funs.joinToString(prefix = fileHeader(element.classData.`package`.escapedClassName), separator = "\n")
            }.forEach { (name, fileString) -> File(generatedDir, name).writeText(fileString) }

    private fun String.toUpperCamelCase(): String = split(" ").joinToString("", transform = String::capitalize)

    private fun processElement(annotatedOptic: AnnotatedOptic): Pair<AnnotatedOptic, List<String>> =
            annotatedOptic to annotatedOptic.targets.map { variable ->
                val sourceClassName = annotatedOptic.classData.fullName.escapedClassName
                val sourceName = annotatedOptic.type.simpleName.toString().decapitalize()
                val targetClassName = variable.fullName
                val targetName = variable.paramName

                if (targetClassName.endsWith("?")) {
                    val nonNullTargetClassName = targetClassName.dropLast(1)
                    """
                    |fun $sourceName${targetName.toUpperCamelCase()}Optional(): $optional<$sourceClassName, $nonNullTargetClassName> = $optional(
                    |        getOrModify = { $sourceName: $sourceClassName -> $sourceName.${targetName.plusIfNotBlank(prefix = "`", postfix = "`")}?.right() ?: $sourceName.left() },
                    |        set = { value: $nonNullTargetClassName ->
                    |            { $sourceName: $sourceClassName ->
                    |                $sourceName.copy(${targetName.plusIfNotBlank(prefix = "`", postfix = "`")} = value)
                    |            }
                    |        }
                    |)
                    """.trimMargin()
                } else if (targetClassName.startsWith("`arrow`.`core`.`Option`")) {

                    val clz = Regex("`arrow`.`core`.`Option`<(.*)>$").matchEntire(targetClassName)?.groupValues?.get(1) ?: return@map ""

                    """
                    |fun $sourceName${targetName.toUpperCamelCase()}Optional(): $optional<$sourceClassName, $clz> = $optional(
                    |        getOrModify = { $sourceName: $sourceClassName -> $sourceName.${targetName.plusIfNotBlank(prefix = "`", postfix = "`")}.orNull()?.right() ?: $sourceName.left() },
                    |        set = { value: $clz ->
                    |            { $sourceName: $sourceClassName ->
                    |                $sourceName.copy(${targetName.plusIfNotBlank(prefix = "`", postfix = "`")} = value.toOption())
                    |            }
                    |        }
                    |)
                    """.trimMargin()

                } else {
                    ""
                }
            }

    fun fileHeader(packageName: String): String =
            """package $packageName
               |import arrow.syntax.either.*
               |import arrow.syntax.option.toOption
               |""".trimMargin()

}
