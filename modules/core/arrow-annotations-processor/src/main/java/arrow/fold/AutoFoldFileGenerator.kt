package arrow.fold

import arrow.common.utils.fullName
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
                |${params(targets, returnType)}
                |): $returnType = when (this) {
                |${patternMatching(targets)}
                |}
                """.trimMargin()
    }

  fun typeParams(params: List<String>): String =
    if (params.isNotEmpty()) params.joinToString(prefix = "<", postfix = ">")
    else ""

  fun params(variants: List<Variant>, returnType: String): String = variants.joinToString(transform = { variant ->
    "        crossinline ${variant.simpleName.decapitalize()}: (${variant.fullName.escapedClassName}${typeParams(variant.typeParams)}) -> $returnType"
  }, separator = ",\n")

  fun patternMatching(variants: List<Variant>): String = variants.joinToString(transform = { variant ->
    "    is ${variant.fullName.escapedClassName} -> ${variant.simpleName.decapitalize().escapedClassName}(this)"
  }, separator = "\n")

  fun functionTypeParams(params: List<String>, returnType: String): String =
    if (params.isEmpty()) ""
    else params.joinToString(prefix = "<", postfix = ", $returnType>")

  fun getFoldType(params: List<String>): String {
    fun check(param: String, next: List<String>): String = (param[0] + 1).let {
      if (next.contains(it.toString())) check(next.firstOrNull() ?: "", next.drop(1))
      else it.toString()
    }

    return check(params.firstOrNull() ?: "", params.drop(1))
  }

  fun fileHeader(packageName: String): String =
    """package $packageName
               |
               |""".trimMargin()

}

