package arrow.fold

import arrow.common.utils.fullName
import arrow.common.utils.knownError
import arrow.common.utils.removeBackticks
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import java.io.File

class AutoFoldFileGenerator(
  private val annotatedList: Collection<AnnotatedFold>,
  private val generatedDir: File
) {

  fun generate() = annotatedList.map(this::processElement)
    .map { (element, fold) ->
      "${foldAnnotationClass.simpleName}.${element.type.simpleName.toString().toLowerCase()}.kt" to
        fileHeader(element.classData.`package`.escapedClassName) + fold
    }.map { (name, fileString) -> File(generatedDir, name).writeText(fileString) }

  private fun processElement(annotatedFold: AnnotatedFold): Pair<AnnotatedFold, String> =
    annotatedFold to annotatedFold.targets.let { targets ->
      val sourceClassName = annotatedFold.classData.fullName.escapedClassName
      val sumTypeParams = typeParams(annotatedFold.typeParams)
      val returnType = getFoldType(annotatedFold.typeParams)
      val functionTypeParams = functionTypeParams(annotatedFold.typeParams, returnType)

      """inline fun $functionTypeParams $sourceClassName$sumTypeParams.fold(
                |${params(targets, returnType, annotatedFold)}
                |): $returnType = when (this) {
                |${patternMatching(targets)}
                |}
                """.trimMargin()
    }

  private fun typeParams(params: List<String>): String =
    if (params.isNotEmpty()) params.joinToString(prefix = "<", postfix = ">")
    else ""

  private fun params(variants: List<Variant>, returnType: String, annotatedFold: AnnotatedFold): String = variants.joinToString(transform = { variant ->
    if (variant.typeParams.size > annotatedFold.typeParams.size) autoFoldGenericError(annotatedFold, variant)
    else "        crossinline ${variant.simpleName.decapitalize()}: (${variant.fullName.escapedClassName}${typeParams(variant.typeParams)}) -> $returnType"
  }, separator = ",\n")

  private fun fileHeader(packageName: String) = """
    |${if (packageName != "`unnamed package`") "package $packageName" else ""}
    |
    |""".trimMargin()

  private fun patternMatching(variants: List<Variant>): String = variants.joinToString(transform = { variant ->
    "    is ${variant.fullName.escapedClassName} -> ${variant.simpleName.decapitalize().escapedClassName}(this)"
  }, separator = "\n")

  private fun functionTypeParams(params: List<String>, returnType: String): String =
    if (params.isEmpty()) "<$returnType>"
    else params.joinToString(prefix = "<", postfix = ", $returnType>")

  private fun getFoldType(params: List<String>): String {
    fun check(param: String, next: List<String>): String = (param[0] + 1).let {
      if (next.contains(it.toString())) check(next.firstOrNull() ?: "", next.drop(1))
      else it.toString()
    }

    return if (params.isNotEmpty()) check(params.first(), params.drop(1)) else "A"
  }

  private fun autoFoldGenericError(annotatedFold: AnnotatedFold, variant: Variant): Nothing = knownError(
    """
      |@autofold cannot create a fold method for sealed class ${annotatedFold.classData.fullName.escapedClassName.removeBackticks()}
      |sealed class ${annotatedFold.classData.fullName.escapedClassName.removeBackticks()}${typeParams(annotatedFold.typeParams)}
      |${" ".repeat("sealed class ${annotatedFold.classData.fullName.escapedClassName.removeBackticks()}".length)} ^ contains less generic information than variant
      |
      |${variant.fullName.escapedClassName.removeBackticks()}${typeParams(variant.typeParams)}
      |${" ".repeat(variant.fullName.escapedClassName.removeBackticks().length)} ^
      """.trimMargin(), annotatedFold.type)
}
