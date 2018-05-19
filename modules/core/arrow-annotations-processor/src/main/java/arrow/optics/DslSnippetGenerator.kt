package arrow.optics

fun generateLensDsl(annotatedOptic: AnnotatedOptic, optic: DataClassDsl) = Snippet(
  content = processLensSyntax(annotatedOptic, optic.foci)
)

fun generateOptionalDsl(annotatedOptic: AnnotatedOptic, optic: DataClassDsl) = Snippet(
  content = processOptionalSyntax(annotatedOptic, optic)
)

fun generatePrismDsl(annotatedOptic: AnnotatedOptic, isoOptic: SealedClassDsl) = Snippet(
  content = processPrismSyntax(annotatedOptic, isoOptic)
)

private fun processLensSyntax(ele: AnnotatedOptic, foci: List<Focus>): String = foci.joinToString(separator = "\n") { focus ->
  """
  |inline val <S> $Iso<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Lens<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
  |inline val <S> $Lens<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Lens<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
  |inline val <S> $Optional<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Optional<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
  |inline val <S> $Prism<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Optional<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
  |inline val <S> $Getter<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Getter<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
  |inline val <S> $Setter<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Setter<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
  |inline val <S> $Traversal<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Traversal<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
  |inline val <S> $Fold<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Fold<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
  |""".trimMargin()
}

private fun processOptionalSyntax(ele: AnnotatedOptic, optic: DataClassDsl) = optic.foci.joinToString(separator = "\n") { focus ->
  val targetClassName = when (focus) {
    is NullableFocus -> focus.nonNullClassName
    is OptionFocus -> focus.nestedClassName
    is NonNullFocus -> return@joinToString ""
  }

  """
  |inline val <S> $Iso<S, ${ele.sourceClassName}>.${focus.paramName}: $Optional<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |inline val <S> $Lens<S, ${ele.sourceClassName}>.${focus.paramName}: $Optional<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |inline val <S> $Optional<S, ${ele.sourceClassName}>.${focus.paramName}: $Optional<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |inline val <S> $Prism<S, ${ele.sourceClassName}>.${focus.paramName}: $Optional<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |inline val <S> $Setter<S, ${ele.sourceClassName}>.${focus.paramName}: $Setter<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |inline val <S> $Traversal<S, ${ele.sourceClassName}>.${focus.paramName}: $Traversal<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |inline val <S> $Fold<S, ${ele.sourceClassName}>.${focus.paramName}: $Fold<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |""".trimMargin()
}

private fun processPrismSyntax(ele: AnnotatedOptic, dsl: SealedClassDsl): String = dsl.foci.joinToString(separator = "\n\n") { focus ->
  """
  |inline val <S> $Iso<S, ${ele.sourceClassName}>.${focus.paramName}: $Prism<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |inline val <S> $Lens<S, ${ele.sourceClassName}>.${focus.paramName}: $Optional<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |inline val <S> $Optional<S, ${ele.sourceClassName}>.${focus.paramName}: $Optional<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |inline val <S> $Prism<S, ${ele.sourceClassName}>.${focus.paramName}: $Prism<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |inline val <S> $Setter<S, ${ele.sourceClassName}>.${focus.paramName}: $Setter<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |inline val <S> $Traversal<S, ${ele.sourceClassName}>.${focus.paramName}: $Traversal<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |inline val <S> $Fold<S, ${ele.sourceClassName}>.${focus.paramName}: $Fold<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
  |""".trimMargin()
}
