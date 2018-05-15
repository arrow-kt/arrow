package arrow.optics

import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import java.io.File

class PrismsFileGenerator(
  private val annotatedList: Collection<AnnotatedOptic>,
  private val generatedDir: File
) {

  private val filePrefix = "prisms"

  fun generate() = annotatedList.map(this::processElement)
    .map { (element, funs) ->
      "$filePrefix.${element.classData.`package`}.${element.type.simpleName.toString().toLowerCase()}.kt" to
        funs.joinToString(prefix = fileHeader(element.classData.`package`.escapedClassName), separator = "\n\n")
    }.forEach { (name, fileString) -> File(generatedDir, name).writeText(fileString) }

  private fun processElement(annotatedPrism: AnnotatedOptic): Pair<AnnotatedOptic, List<String>> =
    annotatedPrism to annotatedPrism.targets.map { target ->
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
    }

  fun fileHeader(packageName: String): String =
    """package $packageName
               |
               |import arrow.core.left
               |import arrow.core.right
               |
               |""".trimMargin()

}