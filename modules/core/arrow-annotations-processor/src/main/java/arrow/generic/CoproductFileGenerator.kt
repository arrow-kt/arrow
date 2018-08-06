package arrow.generic

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.TypeVariableName

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec

import java.io.File

private const val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
private const val packageName = "arrow.generic"

fun generateCoproducts(destination: File) {
    generateGenericHolders(destination)

    for (size in 2 until alphabet.length + 1) {
        FileSpec.builder("$packageName.coproduct$size", "Coproduct$size")
                .addImport(packageName, "Coproduct") //Import coproduct
                .apply {
                    val generics = alphabet.subSequence(0, size).toList()
                    val coproductType = ClassName("", "Coproduct$size")
                    val parameterizedCoproductType = coproductType.parameterizedBy(
                            *generics.map { TypeVariableName(it.toString()) }.toTypedArray()
                    )

                    addCoproductTypeAlias(generics)
                    addCoproductConstructorFunctions(generics, parameterizedCoproductType)
                    addCoproductExtensionFunctions(generics, parameterizedCoproductType)
                    addCoproductFoldFunction(generics, parameterizedCoproductType)
                    addCoproductMapFunction(generics, coproductType, parameterizedCoproductType)
                }
                .build()
                .writeTo(destination)
    }
}

private fun generateGenericHolders(destination: File) {
    val parentClassName = ClassName("arrow.generic", "GenericHolder")

    FileSpec.builder("arrow.generic", "GenericHolder")
            .addType(
                    TypeSpec.classBuilder(parentClassName)
                            .addModifiers(KModifier.SEALED)
                            .build()
            )
            .apply {
                for (size in 2 until alphabet.length + 1) {
                    val generics = alphabet.subSequence(0, size).toList()

                    addType(
                            TypeSpec.classBuilder("Generic$size")
                                    .apply {
                                        addTypeVariables(generics.map { TypeVariableName(it.toString()) })
                                    }
                                    .superclass(parentClassName)
                                    .build()
                    )
                }
            }
            .build()
            .writeTo(destination)
}

private fun FileSpec.Builder.addCoproductTypeAlias(generics: List<Char>) {
    val size = generics.size
    val genericsString = generics.joinToString(separator = ", ")
    val parameterizedClass =  ClassName("arrow.generic", "Coproduct").parameterizedBy(
            ClassName("arrow.generic","Generic$size").parameterizedBy(
                    TypeVariableName(genericsString)
            )
    )
    addTypeAlias(
            TypeAliasSpec.builder("Coproduct$size<$genericsString>",parameterizedClass)
                    .build()
    )
}

private fun FileSpec.Builder.addCoproductConstructorFunctions(
        generics: List<Char>,
        parameterizedCoproductType: ParameterizedTypeName
) {
    val genericHolderString = "Generic${generics.size}<${generics.joinToString(separator = ", ")}>()"
    for (index in 0 until generics.size) {
        val generic = generics[index]
        val paramName = "${generic.toLowerCase()}"

        addFunction(
                FunSpec.builder("coproductOf")
                        .apply {
                            generics.forEach {
                                addTypeVariable(TypeVariableName("$it"))
                            }
                        }
                        .apply {
                            addParameter(
                                    name = paramName,
                                    type = TypeVariableName("$generic")
                            )

                            for (dummyArgsIndex in 0 until index) {
                                addParameter(
                                        ParameterSpec.builder("dummy$dummyArgsIndex", kotlin.Unit::class.java)
                                                .defaultValue("%T", kotlin.Unit::class.java)
                                                .build()
                                )
                            }
                        }
                        .returns(parameterizedCoproductType)
                        .addStatement("return Coproduct($paramName, $genericHolderString)")
                        .build()
        )
    }
}

private fun FileSpec.Builder.addCoproductExtensionFunctions(
        generics: List<Char>,
        parameterizedCoproductType: ParameterizedTypeName
) {
    val genericHolderString = "Generic${generics.size}<${generics.joinToString(separator = ", ")}>()"
    for (index in 0 until generics.size) {
        val generic = generics[index]

        addFunction(
                FunSpec.builder("cop")
                        .receiver(TypeVariableName(generic.toString()))
                        .apply {
                            generics.forEach {
                                addTypeVariable(TypeVariableName("$it"))
                            }
                        }
                        .apply {
                            for (dummyArgsIndex in 0 until index) {
                                addDummyParameter(dummyArgsIndex)
                            }
                        }
                        .returns(parameterizedCoproductType)
                        .addStatement("return Coproduct(this, $genericHolderString)")
                        .build()
        )
    }
}

private fun FileSpec.Builder.addCoproductFoldFunction(
        generics: List<Char>,
        parameterizedCoproductType: ParameterizedTypeName
) {
    val size = generics.size

    addFunction(
            FunSpec.builder("fold")
                    .receiver(parameterizedCoproductType)
                    .addModifiers(KModifier.INLINE)
                    .apply {
                        generics.forEach {
                            addTypeVariable(TypeVariableName(it.toString()).reified(true))
                            addParameter(
                                    "${it.toLowerCase()}",
                                    TypeVariableName("($it) -> RESULT"),
                                    KModifier.CROSSINLINE
                            )
                        }
                    }
                    .addTypeVariable(TypeVariableName("RESULT"))
                    .returns(TypeVariableName("RESULT"))
                    .apply {
                        beginControlFlow("return when (value)")
                        for (generic in generics) {
                            addStatement("is $generic -> ${generic.toLowerCase()}(value as $generic)")
                        }
                        val message = "Invalid Coproduct$size \$this"
                        addStatement("""else -> throw %T("$message")""", IllegalStateException::class.java)
                        endControlFlow()
                    }
                    .build()
    )
}

private fun FileSpec.Builder.addCoproductMapFunction(
        generics: List<Char>,
        coproductType: ClassName,
        parameterizedCoproductType: ParameterizedTypeName
) {
    val size = generics.size

    val returnType = coproductType.parameterizedBy(
            *generics.map { TypeVariableName(it.toString()) }
                    .dropLast(1)
                    .plus(TypeVariableName("RESULT"))
                    .toTypedArray()
    )

    addFunction(
            FunSpec.builder("map")
                    .receiver(parameterizedCoproductType)
                    .addModifiers(KModifier.INLINE)
                    .apply {
                        generics.forEach {
                            addTypeVariable(TypeVariableName(it.toString()).reified(true))
                        }
                    }
                    .addTypeVariable(TypeVariableName("RESULT"))
                    .addParameter("apply", TypeVariableName("(${generics.last()}) -> RESULT"), KModifier.CROSSINLINE)
                    .returns(returnType)
                    .apply {
                        val types = generics.toList()
                        addCode(CodeBlock.builder()
                                        .add("return fold<${types.joinToString()}, Coproduct$size<${types.dropLast(1).joinToString()}, RESULT>>(\n")
                                        .apply {
                                            for (type in types.dropLast(1)) {
                                                add("\t{ it.cop<${types.dropLast(1).joinToString()}, RESULT>() },\n")
                                            }
                                        }
                                        .add("\t{ apply(it).cop<${types.dropLast(1).joinToString()}, RESULT>() }\n")
                                        .add(")\n")
                                        .build())
                    }
                    .build()
    )
}

private fun FunSpec.Builder.addDummyParameter(index: Int) {
    addParameter(
            ParameterSpec.builder("dummy$index", kotlin.Unit::class.java)
                    .defaultValue("%T", kotlin.Unit::class.java)
                    .build()
    )
}