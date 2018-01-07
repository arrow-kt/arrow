package arrow.optics

import me.eugeniomarletti.kotlin.metadata.escapedClassName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank
import java.io.File

class IsosFileGenerator(
        private val annotatedList: Collection<AnnotatedOptic>,
        private val generatedDir: File
) {

    private val tuple = "arrow.core.Tuple"
    private val letters = ('a'..'j').toList()

    fun generate() = buildIsos(annotatedList)

    private fun buildIsos(optics: Collection<AnnotatedOptic>) =
            optics.map(this::processElement)
                    .forEach { (name, funString) ->
                        File(generatedDir, isosAnnotationClass.simpleName + ".$name.kt").printWriter().use { w ->
                            w.println(funString)
                        }
                    }

    private fun processElement(iso: AnnotatedOptic): Pair<String, String> = iso.sourceName to """
            |package ${iso.classData.`package`.escapedClassName}
            |
            |fun ${iso.sourceName}Iso(): arrow.optics.Iso<${iso.sourceClassName}, ${focusType(iso)}> = arrow.optics.Iso(
            |        get = { ${iso.sourceName}: ${iso.sourceClassName} -> ${getFunction(iso)} },
            |        reverseGet = { ${reverseGetFunction(iso)} }
            |)""".trimMargin()

    private fun getFunction(iso: AnnotatedOptic) =
            if (iso.hasTupleFocus) tupleConstructor(iso)
            else "${iso.sourceName}.${iso.targets.first().paramName}"

    private fun reverseGetFunction(iso: AnnotatedOptic) =
            if (iso.hasTupleFocus) "tuple: ${focusType(iso)} -> ${classConstructorFromTuple(iso.sourceClassName, iso.focusSize)}"
            else "${iso.sourceClassName}(it)"

    private fun tupleConstructor(iso: AnnotatedOptic) =
            iso.targets.joinToString(prefix = "$tuple${iso.focusSize}(", postfix = ")", transform = { "${iso.sourceName}.${it.paramName.plusIfNotBlank(prefix = "`", postfix = "`")}" })

    private fun focusType(iso: AnnotatedOptic) =
            if (iso.hasTupleFocus) iso.targetNames.joinToString(prefix = "$tuple${iso.targets.size}<", postfix = ">")
            else iso.targetNames.first()

    private fun classConstructorFromTuple(sourceClassName: String, propertiesSize: Int) =
            (0 until propertiesSize).joinToString(prefix = "$sourceClassName(", postfix = ")", transform = { "tuple.${letters[it]}" })

}