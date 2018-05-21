package arrow.optics

import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank
import java.io.File

class LensesFileGenerator(
  private val annotatedList: Collection<AnnotatedOptic>,
  private val generatedDir: File
) {

  private val filePrefix = "lenses"

  fun generate() = annotatedList.map(this::processElement)
    .map { (element, funs) ->
      "$filePrefix.${element.classData.`package`}.${element.type.simpleName.toString().toLowerCase()}.kt" to
        funs.joinToString(prefix = "package ${element.classData.`package`.escapedClassName}\n\n", separator = "\n")
    }.forEach { (name, fileString) -> File(generatedDir, name).writeText(fileString) }

  private fun String.toUpperCamelCase(): String = split(" ").joinToString("", transform = String::capitalize)

  private fun processElement(annotatedOptic: AnnotatedOptic): Pair<AnnotatedOptic, List<String>> =
    annotatedOptic to annotatedOptic.targets.map { variable ->
      val sourceClassName = annotatedOptic.classData.fullName.escapedClassName
      val sourceName = annotatedOptic.type.simpleName.toString().decapitalize()
      val targetClassName = variable.fullName
      val targetName = variable.paramName
      val lensType = when (variable) {
        is Target.NullableTarget -> "nullable${targetName.toUpperCamelCase()}"
        is Target.OptionTarget -> "option${targetName.toUpperCamelCase()}"
        is Target.NonNullTarget -> targetName
      }

      """
                    |inline val $sourceClassName.Companion.$lensType: $Lens<$sourceClassName, $targetClassName> get()= $Lens(
                    |        get = { $sourceName: $sourceClassName -> $sourceName.${targetName.plusIfNotBlank(prefix = "`", postfix = "`")} },
                    |        set = { value: $targetClassName ->
                    |            { $sourceName: $sourceClassName ->
                    |                $sourceName.copy(${targetName.plusIfNotBlank(prefix = "`", postfix = "`")} = value)
                    |            }
                    |        }
                    |)
                    |
                    |inline val <S> $Iso<S, $sourceClassName>.$lensType: $Lens<S, $targetClassName> inline get() = this + $sourceClassName.$lensType
                    |inline val <S> $Lens<S, $sourceClassName>.$lensType: $Lens<S, $targetClassName> inline get() = this + $sourceClassName.$lensType
                    |inline val <S> $Optional<S, $sourceClassName>.$lensType: $Optional<S, $targetClassName> inline get() = this + $sourceClassName.$lensType
                    |inline val <S> $Prism<S, $sourceClassName>.$lensType: $Optional<S, $targetClassName> inline get() = this + $sourceClassName.$lensType
                    |inline val <S> $Getter<S, $sourceClassName>.$lensType: $Getter<S, $targetClassName> inline get() = this + $sourceClassName.$lensType
                    |inline val <S> $Setter<S, $sourceClassName>.$lensType: $Setter<S, $targetClassName> inline get() = this + $sourceClassName.$lensType
                    |inline val <S> $Traversal<S, $sourceClassName>.$lensType: $Traversal<S, $targetClassName> inline get() = this + $sourceClassName.$lensType
                    |inline val <S> $Fold<S, $sourceClassName>.$lensType: $Fold<S, $targetClassName> inline get() = this + $sourceClassName.$lensType
                    |""".trimMargin()
    }

}
