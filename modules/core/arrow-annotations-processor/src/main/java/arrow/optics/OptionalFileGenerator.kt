package arrow.optics

import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank
import java.io.File

class OptionalFileGenerator(
  private val annotatedList: Collection<AnnotatedOptic>,
  private val generatedDir: File
) {

  private val optional = "arrow.optics.Optional"

  fun generate() = annotatedList.map(this::processElement)
    .filter { it.second.joinToString(separator = "").isNotEmpty() }
    .map { (element, funs) ->
      "${optionalsAnnotationClass.simpleName}.${element.classData.`package`}.${element.type.simpleName.toString().toLowerCase()}.kt" to
        funs.joinToString(prefix = fileHeader(element.classData.`package`.escapedClassName), separator = "\n")
    }.forEach { (name, fileString) -> File(generatedDir, name).writeText(fileString) }

  private fun String.toUpperCamelCase(): String = split(" ").joinToString("", transform = String::capitalize)

  private fun processElement(annotatedOptic: AnnotatedOptic): Pair<AnnotatedOptic, List<String>> =
    annotatedOptic to annotatedOptic.targets.map { variable ->
      val sourceClassName = annotatedOptic.classData.fullName.escapedClassName
      val sourceName = annotatedOptic.type.simpleName.toString().decapitalize()
      val targetClassName = variable.fullName
      val targetName = variable.paramName

      when (variable) {
        is Target.NullableTarget -> processNullableOptional(targetClassName.dropLast(1), sourceName, targetName, sourceClassName)
        is Target.OptionTarget -> processOptionOptional(sourceName, targetName, sourceClassName, variable.nestedFullName)
        is Target.NonNullTarget -> "" //Don't generate optional for non-null targets.
      }
    }

  private fun fileHeader(packageName: String): String =
    """package $packageName
               |
               |import arrow.core.left
               |import arrow.core.right
               |import arrow.core.toOption
               |""".trimMargin()

  private fun processNullableOptional(nonNullTargetClassName: String, sourceName: String, targetName: String, sourceClassName: String) = """
      |fun $sourceName${targetName.toUpperCamelCase()}(): $optional<$sourceClassName, $nonNullTargetClassName> = $optional(
      |  getOrModify = { $sourceName: $sourceClassName -> $sourceName.${targetName.plusIfNotBlank(prefix = "`", postfix = "`")}?.right() ?: $sourceName.left() },
      |  set = { value: $nonNullTargetClassName ->
      |    { $sourceName: $sourceClassName ->
      |      $sourceName.copy(${targetName.plusIfNotBlank(prefix = "`", postfix = "`")} = value)
      |    }
      |  }
      |)
      """.trimMargin()

  private fun processOptionOptional(sourceName: String, targetName: String, sourceClassName: String, clz: String) = """
      |fun $sourceName${targetName.toUpperCamelCase()}(): $optional<$sourceClassName, $clz> = $optional(
      |  getOrModify = { $sourceName: $sourceClassName -> $sourceName.${targetName.plusIfNotBlank(prefix = "`", postfix = "`")}.orNull()?.right() ?: $sourceName.left() },
      |  set = { value: $clz ->
      |    { $sourceName: $sourceClassName ->
      |      $sourceName.copy(${targetName.plusIfNotBlank(prefix = "`", postfix = "`")} = value.toOption())
      |    }
      |  }
      |)
      """.trimMargin()

}
