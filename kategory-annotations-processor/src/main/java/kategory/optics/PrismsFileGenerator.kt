package kategory.optics

import kategory.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import java.io.File

class PrismsFileGenerator(
        private val annotatedList: Collection<AnnotatedOptic>,
        private val generatedDir: File
) {

    private val prism = "kategory.optics.Prism"

    fun generate() = annotatedList.map(this::processElement)
            .map { (element, funs) ->
                "${prismsAnnotationClass.simpleName}.${element.type.simpleName.toString().toLowerCase()}.kt" to
                        funs.joinToString(prefix = fileHeader(element.classData.`package`.escapedClassName), separator = "\n\n")
            }.forEach { (name, fileString) -> File(generatedDir, name).writeText(fileString) }

    private fun processElement(annotatedPrism: AnnotatedOptic): Pair<AnnotatedOptic, List<String>> =
            annotatedPrism to annotatedPrism.targets.map { target ->
                val sourceClassName = annotatedPrism.classData.fullName.escapedClassName
                val sourceName = annotatedPrism.type.simpleName.toString().toLowerCase()
                val targetClassName = target.fullName.escapedClassName
                val targetName = target.paramName

                """fun $sourceName$targetName() = $prism(
                   |        getOrModify = { $sourceName: $sourceClassName ->
                   |            when ($sourceName) {
                   |                is $targetClassName -> $sourceName.right()
                   |                else -> $sourceName.left()
                   |            }
                   |        },
                   |        reverseGet = { it }
                   |)
                """.trimMargin()
            }

    fun fileHeader(packageName: String): String =
            """package $packageName
               |
               |import kategory.left
               |import kategory.right
               |
               |""".trimMargin()

}