package arrow.optics

import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank
import java.io.File

fun generateLenses(annotatedOptic: AnnotatedOptic, lensOptic: LensOptic) = Snippet(
  content = processElement(annotatedOptic, lensOptic.foci)
)

private fun String.toUpperCamelCase(): String = split(" ").joinToString("", transform = String::capitalize)

private fun processElement(annotatedOptic: AnnotatedOptic, foci: List<Focus>): String = foci.map { variable ->
  val sourceClassName = annotatedOptic.classData.fullName.escapedClassName
  val sourceName = annotatedOptic.type.simpleName.toString().decapitalize()
  val targetClassName = variable.fullName
  val targetName = variable.paramName
  val lensType = when (variable) {
    is NullableFocus -> "nullable${targetName.toUpperCamelCase()}"
    is OptionFocus -> "option${targetName.toUpperCamelCase()}"
    is NonNullFocus -> targetName
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

}.joinToString(separator = "\n")
