package arrow.optics

import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank

fun generateOptionals(annotatedOptic: AnnotatedOptic, optic: OptionalOptic) = Snippet(
  imports = setOf("import arrow.core.left", "import arrow.core.right", "import arrow.core.toOption"),
  content = processElement(annotatedOptic, optic)
)

private fun processElement(annotatedOptic: AnnotatedOptic, optic: OptionalOptic): String =
  optic.foci.map { variable ->
    val sourceClassName = annotatedOptic.classData.fullName.escapedClassName
    val sourceName = annotatedOptic.type.simpleName.toString().decapitalize()
    val targetClassName = variable.fullName
    val targetName = variable.paramName

    when (variable) {
      is NullableFocus -> processNullableOptional(sourceName, sourceClassName, targetName, targetClassName.dropLast(1))
      is OptionFocus -> processOptionOptional(sourceName, sourceClassName, targetName, variable.nestedFullName)
      is NonNullFocus -> "" //Don't generate optional for non-null foci.
    }
  }.joinToString(separator = "\n")

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
      |${processSyntax(sourceClassName, targetName, targetClassName)}
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
      |${processSyntax(sourceClassName, targetName, targetClassName)}
      |""".trimMargin()

private fun processSyntax(sourceClassName: String, targetName: String, targetClassName: String) = """
    |inline val <S> $Iso<S, $sourceClassName>.$targetName: $Optional<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    |inline val <S> $Lens<S, $sourceClassName>.$targetName: $Optional<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    |inline val <S> $Optional<S, $sourceClassName>.$targetName: $Optional<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    |inline val <S> $Prism<S, $sourceClassName>.$targetName: $Optional<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    |inline val <S> $Setter<S, $sourceClassName>.$targetName: $Setter<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    |inline val <S> $Traversal<S, $sourceClassName>.$targetName: $Traversal<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    |inline val <S> $Fold<S, $sourceClassName>.$targetName: $Fold<S, $targetClassName> inline get() = this + $sourceClassName.$targetName
    """.trimMargin()
