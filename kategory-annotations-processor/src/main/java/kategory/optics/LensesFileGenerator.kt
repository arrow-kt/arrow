package kategory.optics

import kategory.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import java.io.File

class LensesFileGenerator(
        private val annotatedList: Collection<AnnotatedOptic>,
        private val generatedDir: File
) {

    private val lens = "kategory.optics.Lens"

    fun generate() = annotatedList.map(this::processElement)
            .map { (element, funs) ->
                "${lensesAnnotationClass.simpleName}.${element.type.simpleName.toString().toLowerCase()}.kt" to
                        funs.joinToString(prefix = "package ${element.classData.`package`.escapedClassName}\n\n", separator = "\n")
            }.forEach { (name, fileString) -> File(generatedDir, name).writeText(fileString) }

    private fun processElement(annotatedOptic: AnnotatedOptic): Pair<AnnotatedOptic, List<String>> =
            annotatedOptic to annotatedOptic.targets.map { variable ->
                val sourceClassName = annotatedOptic.classData.fullName.escapedClassName
                val sourceName = annotatedOptic.type.simpleName.toString().toLowerCase()
                val targetClassName = variable.fullName
                val targetName = variable.paramName

                """
                    |fun $sourceName${targetName.capitalize()}() = $lens(
                    |        get = { $sourceName: $sourceClassName -> $sourceName.$targetName },
                    |        set = { $targetName: $targetClassName ->
                    |            { $sourceName: $sourceClassName ->
                    |                $sourceName.copy($targetName = $targetName)
                    |            }
                    |        }
                    |)
                    """.trimMargin()
            }

}
