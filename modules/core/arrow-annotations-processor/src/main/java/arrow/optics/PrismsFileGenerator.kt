package arrow.optics

import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName

fun generatePrisms(annotatedOptic: AnnotatedOptic, isoOptic: PrismOptic) = Snippet(
  imports = setOf("import arrow.core.left", "import arrow.core.right"),
  content = processElement(annotatedOptic, isoOptic)
)

private fun processElement(annotatedPrism: AnnotatedOptic, isoOptic: PrismOptic): String =
  isoOptic.foci.map { target ->
    val sourceClassName = annotatedPrism.classData.fullName.escapedClassName
    val sourceName = annotatedPrism.type.simpleName.toString().decapitalize()
    val targetClassName = target.fullName.escapedClassName
    val targetName = target.paramName.decapitalize()

    """
        |inline val $sourceClassName.Companion.$targetName: $Prism<$sourceClassName, $targetClassName> get()= $Prism(
        |  getOrModify = { $sourceName: $sourceClassName ->
        |    when ($sourceName) {
        |      is $targetClassName -> $sourceName.right()
        |      else -> $sourceName.left()
        |    }
        |  },
        |  reverseGet = { it }
        |)
        |
        |inline val <S> $Iso<S, $sourceClassName>.$targetName: $Prism<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
        |inline val <S> $Lens<S, $sourceClassName>.$targetName: $Optional<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
        |inline val <S> $Optional<S, $sourceClassName>.$targetName: $Optional<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
        |inline val <S> $Prism<S, $sourceClassName>.$targetName: $Prism<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
        |inline val <S> $Setter<S, $sourceClassName>.$targetName: $Setter<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
        |inline val <S> $Traversal<S, $sourceClassName>.$targetName: $Traversal<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
        |inline val <S> $Fold<S, $sourceClassName>.$targetName: $Fold<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
        |""".trimMargin()
  }.joinToString(separator = "\n\n")