package kategory.optics

import com.squareup.kotlinpoet.*
import kategory.*
import java.io.File
import javax.lang.model.element.VariableElement

class IsosFileGenerator(
        private val annotatedList: Collection<AnnotatedIso.Element>,
        private val generatedDir: File
) {
    private val letters = "abcdefghij"

    fun generate() = buildIsos(annotatedList).writeTo(generatedDir)

    private fun buildIsos(elements: Collection<AnnotatedIso.Element>) = elements.map(this::processElement)
            .fold(KotlinFile.builder("optikal", "Isos").skipJavaLangImports(true), { builder, isoSpec ->
                builder.addFun(isoSpec)
            }).build()

    private fun processElement(annotatedIso: AnnotatedIso.Element): FunSpec {
        val className = annotatedIso.type.simpleName.toString().toLowerCase()
        val properties = annotatedIso.properties.toList()
        val propertiesTypes = properties.map { it.asType().asTypeName() }

        val startArgs = listOf(annotatedIso.type)
        val tupleArguments = propertiesTypes
        val setFuncArgs = listOf(annotatedIso.type) + propertiesTypes
        val endArgs = listOf(annotatedIso.type)
        val args = startArgs + tupleArguments + setFuncArgs + endArgs
        val arrayArgs = Array(args.size, { args[it] })

        return FunSpec.builder("${className}Iso")
                .addStatement(
                        """return ${getConstructor(properties)}(
                                   |        get = { $className: %T -> ${getTuple(properties, className)} },
                                   |        reverseGet = { tuple: ${setTuple(properties.size)} -> %T(${classConstructor(properties.size)}) }
                                   |)""".trimMargin(), *arrayArgs
                ).build()
    }

    private fun getConstructor(properties: List<VariableElement>) =
            properties.joinToString(prefix = "kategory.optics.Iso<%T, kategory.Tuple${properties.size}<", postfix = ">>", transform = { "%T" })


    private fun getTuple(properties: List<VariableElement>, className: String) =
            properties.joinToString(prefix = "kategory.Tuple${properties.size}(", postfix = ")", transform = { "$className.${it.simpleName}" })

    private fun setTuple(propertiesSize: Int) =
            (0 until propertiesSize).joinToString(prefix = "kategory.Tuple$propertiesSize<", postfix = ">", transform = { "%T" })

    private fun classConstructor(propertiesSize: Int) = (0 until propertiesSize).joinToString { "tuple.${letters[it]}" }
}