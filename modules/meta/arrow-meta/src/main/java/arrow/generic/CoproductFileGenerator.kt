@file:Suppress("StringLiteralDuplication")

package arrow.generic

import arrow.common.utils.toCamelCase
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.KModifier.OUT
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

fun generateCoproducts(destination: File) {
  for (size in 2 until genericsToClassNames.size + 1) {
    val generics = genericsToClassNames.keys.toList().take(size)

    FileSpec.builder("arrow.generic.coproduct$size", "Coproduct$size")
      .apply {
        addCoproductClassDeclaration(generics)
        addExtensionConstructors(generics)
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
      .addKdoc(
        "Represents a sealed hierarchy of ${generics.size} types where only one of the types is actually present.\n"
      )
      .addModifiers(KModifier.SEALED)
      .addTypeVariables(generics.map { TypeVariableName(it, variance = OUT) })
      .build()
  )

  for (generic in generics) {
    addType(
      TypeSpec.classBuilder(genericsToClassNames[generic]!!)
        .addKdoc(
          "Represents the ${genericsToClassNames[generic]!!.toLowerCase()} type of a Coproduct${generics.size}\n"
        )
        .addModifiers(KModifier.DATA)
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

private fun FileSpec.Builder.addExtensionConstructors(generics: List<String>) {
  for (generic in generics) {
    addFunction(
      FunSpec.builder(genericsToClassNames[generic]!!.toCamelCase())
        .addKdoc(
          methodDocumentation(
            description = "Creates a Coproduct from the $generic type",
            output = "A Coproduct${generics.size}<${generics.joinToString(separator = ",·")}> where the receiver is the $generic"
          )
        )
        .receiver(TypeVariableName(generic))
        .addTypeVariables(generics.toTypeParameters())
        .addStatement("return ${genericsToClassNames[generic]}(this)")
        .returns(parameterizedCoproductNClassName(generics))
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
    val additionalParameterCount = generics.indexOf(generic)

    addFunction(
      FunSpec.builder("select")
        .addKdoc(
          methodDocumentation(
            description = "Transforms the Coproduct into an Option based on the actual value of the Coproduct",
            output = "None if the Coproduct was not the specified type, Some if it was the specified type"
          )
        )
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

private fun FileSpec.Builder.addFoldFunction(generics: List<String>) {
  addFunction(
    FunSpec.builder("fold")
      .addKdoc(
        methodDocumentation(
          description = "Runs the function related to the actual value of the Coproduct and returns the result",
          input = generics.map {
            it.toLowerCase() to "The function used to map ${it.toUpperCase()} to the RESULT type"
          },
          output = "RESULT generated by one of the input functions"
        )
      )
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

private fun methodDocumentation(
  output: String,
  description: String,
  input: List<Pair<String, String>> = emptyList()
): String {
  val parameters = if (input.isEmpty()) {
    ""
  } else {
    input.joinToString(separator = "") { "@param ${it.first} ${it.second}\n" } + "\n"
  }

  return "$description\n\n$parameters@return $output\n"
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
