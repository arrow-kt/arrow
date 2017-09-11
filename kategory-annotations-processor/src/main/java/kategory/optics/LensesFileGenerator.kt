package kategory.optics

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KotlinFile
import com.squareup.kotlinpoet.asClassName
import java.io.File

class LensesFileGenerator(
        private val annotatedList: Collection<AnnotatedLens.Element>,
        private val generatedDir: File
) {

    fun generate() = buildLenses(annotatedList).forEach {
        it.writeTo(generatedDir)
    }

    private fun buildLenses(elements: Collection<AnnotatedLens.Element>): List<KotlinFile> = elements.map(this::processElement)
            .map { (name, funs) ->
                funs.fold(KotlinFile.builder(name.packageName(), "${name.simpleName().toLowerCase()}.lenses").skipJavaLangImports(true), { builder, lensSpec ->
                    builder.addFun(lensSpec)
                }).build()
            }

    private fun processElement(annotatedLens: AnnotatedLens.Element): Pair<ClassName, List<FunSpec>> =
            annotatedLens.type.asClassName() to annotatedLens.properties.map { variable ->
                val className = annotatedLens.type.simpleName.toString().toLowerCase()
                val variableName = variable.paramName

                FunSpec.builder("$className${variableName.capitalize()}")
                        .addStatement(
                                """return kategory.optics.Lens(
                                   |        get = { $className: %T -> $className.$variableName },
                                   |        set = { $variableName: %T ->
                                   |            { $className: %T ->
                                   |                $className.copy($variableName = $variableName)
                                   |            }
                                   |        }
                                   |)""".trimMargin(), annotatedLens.type, variable, annotatedLens.type)
                        .build()
            }

}
