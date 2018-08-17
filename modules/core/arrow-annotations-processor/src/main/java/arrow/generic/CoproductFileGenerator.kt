package arrow.generic

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import java.io.File
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

private val genericsToClassNames = mapOf(
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

fun generateCoproducts(destination: File) {
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

private fun FileSpec.Builder.addCoproductClassDeclaration(generics: List<String>) {
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
                        .addTypeVariables(generics.map { TypeVariableName(it) })
                        .superclass(
                                ClassName("", "Coproduct${generics.size}")
                                        .parameterizedBy(*generics.map { TypeVariableName(it) }.toTypedArray())
                        )
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

private fun FileSpec.Builder.addCoproductOfConstructors(generics: List<String>) {
    for (generic in generics) {
        addFunction(
                FunSpec.builder("coproductOf")
                        .addTypeVariables(generics.map { TypeVariableName(it) })
                        .addParameter(generic.toLowerCase(), TypeVariableName(generic))
                        .addParameters(additionalParameterSpecs(generics.indexOf(generic)))
                        .addStatement("return ${genericsToClassNames[generic]}(${generic.toLowerCase()})")
                        .returns(
                                ClassName("", "Coproduct${generics.size}")
                                        .parameterizedBy(*generics.map { TypeVariableName(it) }.toTypedArray())
                        )
                        .build()
        )
    }
}

private fun FileSpec.Builder.addCopExtensionConstructors(generics: List<String>) {
    for (generic in generics) {
        addFunction(
                FunSpec.builder("cop")
                        .receiver(TypeVariableName(generic))
                        .addTypeVariables(generics.map { TypeVariableName(it) })
                        .addParameters(additionalParameterSpecs(generics.indexOf(generic)))
                        .addStatement("return coproductOf<${generics.joinToString(separator = ", ")}>(this)")
                        .returns(
                                ClassName("", "Coproduct${generics.size}")
                                        .parameterizedBy(*generics.map { TypeVariableName(it) }.toTypedArray())
                        )
                        .build()
        )
    }
}

private fun FileSpec.Builder.addSelectFunctions(generics: List<String>) {
    addImport("arrow.core", "Option")
    addImport("arrow.core", "toOption")

    for (generic in generics) {
        val receiverGenerics = generics
                .map { if (it == generic) TypeVariableName(generic) else TypeVariableName("*") }
                .toTypedArray()

        addFunction(
                FunSpec.builder("select")
                        .addTypeVariable(TypeVariableName(generic))
                        .receiver(ClassName("", "Coproduct${generics.size}").parameterizedBy(*receiverGenerics))
                        .addParameters(additionalParameterSpecs(generics.indexOf(generic)))
                        .returns(ClassName("arrow.core", "Option").parameterizedBy(TypeVariableName(generic)))
                        .addStatement("return (this as? ${genericsToClassNames[generic]})?.${generic.toLowerCase()}.toOption()")
                        .build()
        )
    }
}

private fun FileSpec.Builder.addFoldFunction(generics: List<String>) {
    addFunction(
            FunSpec.builder("fold")
                    .receiver(
                            ClassName(
                                    "",
                                    "Coproduct${generics.size}"
                            ).parameterizedBy(*generics.map { TypeVariableName(it) }.toTypedArray())
                    )
                    .addTypeVariables((generics + "RESULT").map { TypeVariableName(it) })
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

private fun additionalParameterSpecs(count: Int): List<ParameterSpec> = List(count) {
    ParameterSpec.builder("dummy$it", Unit::class)
            .defaultValue("Unit")
            .build()
}