package kategory.optics

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KotlinFile
import java.io.File

class PrismsFileGenerator(
        private val annotatedList: Collection<AnnotatedPrism.Element>,
        private val generatedDir: File
) {

    fun generate() = buildPrisms(annotatedList).forEach {
        it.writeTo(generatedDir)
    }

    private fun buildPrisms(elements: Collection<AnnotatedPrism.Element>): List<KotlinFile> = elements.map(this::processElement)
            .map { (name, funs) ->
                funs.fold(KotlinFile.builder("kategory.optics", "optics.kategory.prisms.$name").skipJavaLangImports(true), { builder, prismSpec ->
                    builder.addFun(prismSpec)
                }).addStaticImport("kategory", "right", "left").build()
            }

    private fun processElement(annotatedPrism: AnnotatedPrism.Element): Pair<String, List<FunSpec>> =
            annotatedPrism.type.simpleName.toString().toLowerCase() to annotatedPrism.subTypes.map { subClass ->
                val sealedClassName = annotatedPrism.type.simpleName.toString().toLowerCase()
                val subTypeName = subClass.simpleName.toString()

                FunSpec.builder("$sealedClassName$subTypeName")
                        .addStatement(
                                """return Prism(
                                   |        getOrModify = { $sealedClassName: %T ->
                                   |            when ($sealedClassName) {
                                   |                is %T -> $sealedClassName.right()
                                   |                else -> $sealedClassName.left()
                                   |            }
                                   |        },
                                   |        reverseGet = { it }
                                   |)""".trimMargin(), annotatedPrism.type, subClass)
                        .build()
            }

}