package arrow.optics

import arrow.common.utils.removeBackticks

data class DslElement(
  val `package`: String,
  val params: List<String>,
  val sourceType: String,
  val dslName: String,
  val originalFocus: String,
  val resultFocus: String,
  val optic: String,
  val opticType: Optic
) {

  val isoSyntax = """
    |${isoSnippet(Iso)}
    |${lensSnippet(Lens)}
    |${prismSnippet(Prism)}
    |${optionalSnippet(Optional)}
    |${getterSnippet(Getter)}
    |${setterSnippet(Setter)}
    |${traversalSnippet(Traversal)}
    |${foldSnippet(Fold)}
  """.trimMargin()

  val lensSyntax = """
    |${isoSnippet(Lens)}
    |${lensSnippet(Lens)}
    |${prismSnippet(Optional)}
    |${optionalSnippet(Optional)}
    |${getterSnippet(Getter)}
    |${setterSnippet(Setter)}
    |${traversalSnippet(Traversal)}
    |${foldSnippet(Fold)}
  """.trimMargin()

  val prismSyntax = """
    |${isoSnippet(Prism)}
    |${lensSnippet(Optional)}
    |${prismSnippet(Prism)}
    |${optionalSnippet(Optional)}
    |${getterSnippet(Fold)}
    |${setterSnippet(Setter)}
    |${traversalSnippet(Traversal)}
    |${foldSnippet(Fold)}
  """.trimMargin()

  val optionalSyntax = """
    |${isoSnippet(Optional)}
    |${lensSnippet(Optional)}
    |${prismSnippet(Optional)}
    |${optionalSnippet(Optional)}
    |${getterSnippet(Fold)}
    |${setterSnippet(Setter)}
    |${traversalSnippet(Traversal)}
    |${foldSnippet(Fold)}
  """.trimMargin()

  val getterSyntax = """
    |${isoSnippet(Getter)}
    |${lensSnippet(Getter)}
    |${prismSnippet(Fold)}
    |${optionalSnippet(Fold)}
    |${getterSnippet(Getter)}
    |${traversalSnippet(Fold)}
    |${foldSnippet(Fold)}
  """.trimMargin()

  val setterSyntax = """
    |${isoSnippet(Setter)}
    |${lensSnippet(Setter)}
    |${prismSnippet(Setter)}
    |${optionalSnippet(Setter)}
    |${setterSnippet(Setter)}
    |${traversalSnippet(Setter)}
  """.trimMargin()

  val traversalSyntax = """
    |${isoSnippet(Traversal)}
    |${lensSnippet(Traversal)}
    |${prismSnippet(Traversal)}
    |${optionalSnippet(Traversal)}
    |${getterSnippet(Fold)}
    |${setterSnippet(Setter)}
    |${traversalSnippet(Traversal)}
    |${foldSnippet(Fold)}
  """.trimMargin()

  val foldSyntax = """
    |${isoSnippet(Fold)}
    |${lensSnippet(Fold)}
    |${prismSnippet(Fold)}
    |${optionalSnippet(Fold)}
    |${getterSnippet(Fold)}
    |${traversalSnippet(Fold)}
    |${foldSnippet(Fold)}
  """.trimMargin()

  private fun isoSnippet(resultType: Optic) = """
    |/**
    | * DSL to compose an [Iso] with focus of ${originalFocus.removeBackticks()} with a [$opticType] with focus ${resultFocus.removeBackticks()}
    | *
    | * @receiver [Iso] with a focus ${originalFocus.removeBackticks()}
    | * @return [$resultType] with a focus in ${resultFocus.removeBackticks()}
    | */
    |inline val <${params.joinToString()}> $Iso<$sourceType, $originalFocus>.$dslName: $resultType<$sourceType, $resultFocus> inline get() = this compose $optic
    |""".trimMargin()


  private fun lensSnippet(resultType: Optic) = """
    |/**
    | * DSL to compose a [Lens] with focus of ${originalFocus.removeBackticks()} with a [$opticType] with focus ${resultFocus.removeBackticks()}
    | *
    | * @receiver [Lens] with a focus ${originalFocus.removeBackticks()}
    | * @return [$resultType] with a focus in ${resultFocus.removeBackticks()}
    | */
    |inline val <${params.joinToString()}> $Lens<$sourceType, $originalFocus>.$dslName: $resultType<$sourceType, $resultFocus> inline get() = this compose $optic
    |""".trimMargin()

  private fun optionalSnippet(resultType: Optic) = """
    |/**
    | * DSL to compose a [Optional] with focus of ${originalFocus.removeBackticks()} with a [$opticType] with focus ${resultFocus.removeBackticks()}
    | *
    | * @receiver [Lens] with a focus ${originalFocus.removeBackticks()}
    | * @return [$resultType] with a focus in ${resultFocus.removeBackticks()}
    | */
    |inline val <${params.joinToString()}> $Optional<$sourceType, $originalFocus>.$dslName: $resultType<$sourceType, $resultFocus> inline get() = this compose $optic
    |""".trimMargin()

  private fun prismSnippet(resultType: Optic) = """
    |/**
    | * DSL to compose a [Prism] with focus of ${originalFocus.removeBackticks()} with a [$opticType] with focus ${resultFocus.removeBackticks()}
    | *
    | * @receiver [Lens] with a focus ${originalFocus.removeBackticks()}
    | * @return [$resultType] with a focus in ${resultFocus.removeBackticks()}
    | */
    |inline val <${params.joinToString()}> $Prism<$sourceType, $originalFocus>.$dslName: $resultType<$sourceType, $resultFocus> inline get() = this compose $optic
    |""".trimMargin()


  private fun getterSnippet(resultType: Optic) = """
    |/**
    | * DSL to compose a [Getter] with focus of ${originalFocus.removeBackticks()} with a [$opticType] with focus ${resultFocus.removeBackticks()}
    | *
    | * @receiver [Getter] with a focus ${originalFocus.removeBackticks()}
    | * @return [$resultType] with a focus in ${resultFocus.removeBackticks()}
    | */
    |inline val <${params.joinToString()}> $Getter<$sourceType, $originalFocus>.$dslName: $resultType<$sourceType, $resultFocus> inline get() = this compose $optic
    |""".trimMargin()

  private fun setterSnippet(resultType: Optic) = """
    |/**
    | * DSL to compose a [Setter] with focus of ${originalFocus.removeBackticks()} with a [$opticType] with focus ${resultFocus.removeBackticks()}
    | *
    | * @receiver [Lens] with a focus ${originalFocus.removeBackticks()}
    | * @return [$resultType] with a focus in ${resultFocus.removeBackticks()}
    | */
    |inline val <${params.joinToString()}> $Setter<$sourceType, $originalFocus>.$dslName: $resultType<$sourceType, $resultFocus> inline get() = this compose $optic
    |""".trimMargin()

  private fun traversalSnippet(resultType: Optic) = """
    |/**
    | * DSL to compose a [Traversal] with focus of ${originalFocus.removeBackticks()} with a [$opticType] with focus ${resultFocus.removeBackticks()}
    | *
    | * @receiver [Lens] with a focus ${originalFocus.removeBackticks()}
    | * @return [$resultType] with a focus in ${resultFocus.removeBackticks()}
    | */
    |inline val <${params.joinToString()}> $Traversal<$sourceType, $originalFocus>.$dslName: $resultType<$sourceType, $resultFocus> inline get() = this compose $optic
    |""".trimMargin()

  private fun foldSnippet(resultType: Optic) = """
    |/**
    | * DSL to compose a [Fold] with focus of ${originalFocus.removeBackticks()} with a [$opticType] with focus ${resultFocus.removeBackticks()}
    | *
    | * @receiver [Lens] with a focus ${originalFocus.removeBackticks()}
    | * @return [$resultType] with a focus in ${resultFocus.removeBackticks()}
    | */
    |inline val <${params.joinToString()}> $Fold<$sourceType, $originalFocus>.$dslName: $resultType<$sourceType, $resultFocus> inline get() = this compose $optic
    |""".trimMargin()

}