package arrow.optics

import arrow.common.utils.knownError

val AnnotatedType.dslSnippet
  get() = when (this) {
    is AnnotatedSumType -> generatePrismDsl()
    is AnnotatedProductType -> generateLensDsl() + generateOptionalDsl()
    is AnnotatedType.Function -> when(dslElement.opticType) {
      Iso -> dslElement.isoSyntax
      Lens -> dslElement.lensSyntax
      Optional -> dslElement.optionalSyntax
      Prism -> dslElement.prismSyntax
      Getter -> dslElement.getterSyntax
      Setter -> dslElement.setterSyntax
      Traversal -> dslElement.traversalSyntax
      Fold -> dslElement.foldSyntax
    }
  }


fun AnnotatedProductType.generateLensDsl() = foci.map { focus ->
  DslElement(
    `package` = packageName,
    params = listOf("S"),
    sourceType = "S",
    dslName = focus.lensParamName(),
    originalFocus = sourceClassName,
    resultFocus = focus.className,
    optic = "$sourceClassName.${focus.lensParamName()}",
    opticType = Lens
  )
}.map(DslElement::lensSyntax).fold(Snippet.EMPTY, Snippet::plus)

fun AnnotatedProductType.generateOptionalDsl() = foci.filterNot { it is NonNullFocus }.map { focus ->
  DslElement(
    `package` = packageName,
    params = listOf("S"),
    sourceType = "S",
    dslName = focus.paramName,
    originalFocus = sourceClassName,
    resultFocus = when (focus) {
      is NullableFocus -> focus.nonNullClassName
      is OptionFocus -> focus.nestedClassName
      is NonNullFocus -> knownError("Something went wrong generating dsl for $element", element)
    },
    optic = "$sourceClassName.${focus.paramName}",
    opticType = Optional
  )
}.map(DslElement::optionalSyntax).fold(Snippet.EMPTY, Snippet::plus)

fun AnnotatedSumType.generatePrismDsl() = foci.map { focus ->
  DslElement(
    `package` = packageName,
    params = listOf("S"),
    sourceType = "S",
    dslName = focus.paramName,
    originalFocus = sourceClassName,
    resultFocus = focus.className,
    optic = "$sourceClassName.${focus.paramName}",
    opticType = Prism
  )
}.map(DslElement::prismSyntax).fold(Snippet.EMPTY, Snippet::plus)

