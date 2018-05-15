package arrow.optics

import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank
import java.io.File

class OptionalFileGenerator(
  private val annotatedList: Collection<AnnotatedOptic>,
  private val generatedDir: File
) {

  private val filePrefix = "optionals"

  fun generate() = annotatedList.map(this::processElement)
    .filter { it.second.joinToString(separator = "").isNotEmpty() }
    .map { (element, funs) ->
      "$filePrefix.${element.classData.`package`}.${element.type.simpleName.toString().toLowerCase()}.kt" to
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
        is Target.NullableTarget -> processNullableOptional(sourceName, sourceClassName, targetName, targetClassName.dropLast(1))
        is Target.OptionTarget -> processOptionOptional(sourceName, sourceClassName, targetName, variable.nestedFullName)
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

  private fun processNullableOptional(sourceName: String, sourceClassName: String, targetName: String, targetClassName: String) = """
      |inline val $sourceClassName.Companion.$targetName: $Optional<$sourceClassName, $targetClassName> get()= $Optional(
      |  getOrModify = { $sourceName: $sourceClassName -> $sourceName.${targetName.plusIfNotBlank(prefix = "`", postfix = "`")}?.right() ?: $sourceName.left() },
      |  set = { value: $targetClassName ->
      |    { $sourceName: $sourceClassName ->
      |      $sourceName.copy(${targetName.plusIfNotBlank(prefix = "`", postfix = "`")} = value)
      |    }
      |  }
      |)
      |
      |${processSyntax(sourceName, sourceClassName, targetName, targetClassName)}
      |""".trimMargin()

  private fun processOptionOptional(sourceName: String, sourceClassName: String, targetName: String, targetClassName: String) = """
      |inline val $sourceClassName.Companion.$targetName: $Optional<$sourceClassName, $targetClassName> get()= $Optional(
      |  getOrModify = { $sourceName: $sourceClassName -> $sourceName.${targetName.plusIfNotBlank(prefix = "`", postfix = "`")}.orNull()?.right() ?: $sourceName.left() },
      |  set = { value: $targetClassName ->
      |    { $sourceName: $sourceClassName ->
      |      $sourceName.copy(${targetName.plusIfNotBlank(prefix = "`", postfix = "`")} = value.toOption())
      |    }
      |  }
      |)
      |
      |${processSyntax(sourceName, sourceClassName, targetName, targetClassName)}
      |""".trimMargin()

  private fun processSyntax(sourceName: String, sourceClassName: String, targetName: String, targetClassName: String) = """
    |inline val <S> $Iso<S, $sourceClassName>.$targetName: $Optional<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    |inline val <S> $Lens<S, $sourceClassName>.$targetName: $Optional<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    |inline val <S> $Optional<S, $sourceClassName>.$targetName: $Optional<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    |inline val <S> $Prism<S, $sourceClassName>.$targetName: $Optional<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    |inline val <S> $Setter<S, $sourceClassName>.$targetName: $Setter<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    |inline val <S> $Traversal<S, $sourceClassName>.$targetName: $Traversal<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    |inline val <S> $Fold<S, $sourceClassName>.$targetName: $Fold<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    """.trimMargin()

}
