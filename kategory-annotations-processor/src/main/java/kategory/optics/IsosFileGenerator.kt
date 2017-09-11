package kategory.optics

import kategory.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import java.io.File

class IsosFileGenerator(
        private val annotatedList: Collection<AnnotatedIso.Element>,
        private val generatedDir: File
) {

    private val tuple = "kategory.Tuple"
    private val letters = "abcdefghij"

    fun generate() = buildIsos(annotatedList)

    private fun buildIsos(elements: Collection<AnnotatedIso.Element>) =
            elements.map(this::processElement)
                    .forEach { (name, funString) ->
                        File(generatedDir, isosAnnotationClass.simpleName + ".$name.kt").printWriter().use { w ->
                            w.println(funString)
                        }
                    }

    private fun processElement(annotatedIso: AnnotatedIso.Element): Pair<String, String> {
        val sourceClassName = annotatedIso.classData.fullName.escapedClassName
        val sourceName = annotatedIso.type.simpleName.toString().toLowerCase()
        val targetName = annotatedIso.properties.map(Variable::fullName)

        return sourceName to """
            |package ${annotatedIso.classData.`package`.escapedClassName}
            |
            |fun ${sourceName}Iso() = ${isoConstructor(sourceClassName, targetName)}(
            |        get = { $sourceName: $sourceClassName -> ${tupleConstructor(annotatedIso.properties, sourceName)} },
            |        reverseGet = { tuple: ${tupleType(targetName)} -> ${classConstructorFromTuple(sourceClassName, targetName.size)} }
            |)
            |""".trimMargin()
    }

    private fun isoConstructor(sourceName: String, targetTypes: List<String>) = "kategory.optics.Iso<$sourceName, ${tupleType(targetTypes)}>"

    private fun tupleConstructor(targetTypes: List<Variable>, sourceName: String) =
            targetTypes.joinToString(prefix = "$tuple${targetTypes.size}(", postfix = ")", transform = { "$sourceName.${it.paramName}" })

    private fun tupleType(targetTypes: List<String>) =
            targetTypes.joinToString(prefix = "$tuple${targetTypes.size}<", postfix = ">")

    private fun classConstructorFromTuple(sourceClassName: String, propertiesSize: Int) =
            (0 until propertiesSize).joinToString(prefix = "$sourceClassName(", postfix = ")", transform = { "tuple.${letters[it]}" })

}