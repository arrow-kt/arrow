@file:Suppress("StringLiteralDuplication")
package arrow.generic

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import java.io.File

private val genericsToClassNames: Map<String, String> = mapOf(
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

fun generateCoproducts(destination: File): Unit {
    for (size in 2 until genericsToClassNames.size + 1) {
        val generics = genericsToClassNames.keys.toList().take(size)

        FileSpec.builder("arrow.generic.coproduct$size", "Coproduct$size")
                .apply {
                    addCoproductClassDeclaration(generics)
                    addCoproductOfConstructors(generics)
                    addCopExtensionConstructors(generics)
                    addSelectFunctions(generics)
                    addFoldFunction(generics)
                }
                .build()
                .writeTo(destination)
    }
}

private fun FileSpec.Builder.addCoproductClassDeclaration(generics: List<String>): Unit {
    addType(
            TypeSpec.classBuilder("Coproduct${generics.size}")
                    .addModifiers(KModifier.SEALED)
                    .addTypeVariables(generics.map { TypeVariableName(it) })
                    .build()
    )

    for (generic in generics) {
        addType(
                TypeSpec.classBuilder(genericsToClassNames[generic]!!)
                        .addModifiers(KModifier.INTERNAL, KModifier.DATA)
                        .addTypeVariables(generics.toTypeParameters())
                        .superclass(parameterizedCoproductNClassName(generics))
                        .addProperty(
                                PropertySpec.builder(generic.toLowerCase(), TypeVariableName(generic))
                                        .initializer(generic.toLowerCase())
                                        .build()
                        )
                        .primaryConstructor(
                                FunSpec.constructorBuilder()
                                        .addParameter(generic.toLowerCase(), TypeVariableName(generic))
                                        .build()
                        )
                        .build()
        )
    }
}

private fun FileSpec.Builder.addCoproductOfConstructors(generics: List<String>): Unit {
    for (generic in generics) {
        val additionalParameterCount = generics.indexOf(generic)

        addFunction(
                FunSpec.builder("coproductOf")
                        .addAnnotations(additionalParameterSuppressAnnotation(additionalParameterCount))
                        .addTypeVariables(generics.toTypeParameters())
                        .addParameter(generic.toLowerCase(), TypeVariableName(generic))
                        .addParameters(additionalParameterSpecs(additionalParameterCount))
                        .addStatement("return ${genericsToClassNames[generic]}(${generic.toLowerCase()})")
                        .returns(parameterizedCoproductNClassName(generics))
                        .build()
        )
    }
}

private fun FileSpec.Builder.addCopExtensionConstructors(generics: List<String>): Unit {
    for (generic in generics) {
        val additionalParameterCount = generics.indexOf(generic)

        addFunction(
                FunSpec.builder("cop")
                        .addAnnotations(additionalParameterSuppressAnnotation(additionalParameterCount))
                        .receiver(TypeVariableName(generic))
                        .addTypeVariables(generics.toTypeParameters())
                        .addParameters(additionalParameterSpecs(additionalParameterCount))
                        .addStatement("return coproductOf<${generics.joinToString(separator = ", ")}>(this)")
                        .returns(parameterizedCoproductNClassName(generics))
                        .build()
        )
    }
}

private fun FileSpec.Builder.addSelectFunctions(generics: List<String>): Unit {
    addImport("arrow.core", "Option")
    addImport("arrow.core", "toOption")

    for (generic in generics) {
        val receiverGenerics = generics
                .map { if (it == generic) TypeVariableName(generic) else TypeVariableName("*") }
                .toTypedArray()
        val additionalParameterCount = generics.indexOf(generic)

        addFunction(
                FunSpec.builder("select")
                        .addAnnotations(additionalParameterSuppressAnnotation(additionalParameterCount))
                        .addTypeVariable(TypeVariableName(generic))
                        .receiver(ClassName("", "Coproduct${generics.size}").parameterizedBy(*receiverGenerics))
                        .addParameters(additionalParameterSpecs(additionalParameterCount))
                        .returns(ClassName("arrow.core", "Option").parameterizedBy(TypeVariableName(generic)))
                        .addStatement("return (this as? ${genericsToClassNames[generic]})?.${generic.toLowerCase()}.toOption()")
                        .build()
        )
    }
}

private fun FileSpec.Builder.addFoldFunction(generics: List<String>): Unit {
    addFunction(
            FunSpec.builder("fold")
                    .receiver(parameterizedCoproductNClassName(generics))
                    .addTypeVariables(generics.toTypeParameters() + TypeVariableName("RESULT"))
                    .addParameters(
                            generics.map {
                                ParameterSpec.builder(
                                        it.toLowerCase(),
                                        LambdaTypeName.get(
                                                parameters = listOf(ParameterSpec.unnamed(TypeVariableName(it))),
                                                returnType = TypeVariableName("RESULT")
                                        )
                                ).build()
                            }
                    )
                    .returns(TypeVariableName("RESULT"))
                    .apply {
                        beginControlFlow("return when (this)")
                        for (generic in generics) {
                            addStatement("is ${genericsToClassNames[generic]} -> ${generic.toLowerCase()}(this.${generic.toLowerCase()})")
                        }
                        endControlFlow()
                    }
                    .build()
    )
}

private fun List<String>.toTypeParameters(): List<TypeVariableName> = map { TypeVariableName(it) }

private fun parameterizedCoproductNClassName(generics: List<String>): ParameterizedTypeName =
        ClassName("", "Coproduct${generics.size}")
                .parameterizedBy(*generics.map { TypeVariableName(it) }.toTypedArray())

private fun additionalParameterSuppressAnnotation(count: Int): List<AnnotationSpec> =
        if (count > 0) {
            listOf(
                    AnnotationSpec.builder(Suppress::class)
                            .addMember("\"UNUSED_PARAMETER\"")
                            .build()
            )
        } else {
            emptyList()
        }

private fun additionalParameterSpecs(count: Int): List<ParameterSpec> = List(count) {
    ParameterSpec.builder("dummy$it", Unit::class)
            .defaultValue("Unit")
            .build()
}
