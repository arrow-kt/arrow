package arrow.optics.plugin.internals

fun generateLensDsl(ele: ADT, optic: DataClassDsl): Snippet =
  Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processLensSyntax(ele, optic.foci)
  )

fun generateOptionalDsl(ele: ADT, optic: DataClassDsl): Snippet =
  Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processOptionalSyntax(ele, optic)
  )

fun generatePrismDsl(ele: ADT, isoOptic: SealedClassDsl): Snippet =
  Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processPrismSyntax(ele, isoOptic)
  )

private fun processLensSyntax(ele: ADT, foci: List<Focus>): String =
  foci.joinToString(separator = "\n") { focus ->
    """
    |inline val <S> $Iso<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Lens<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
    |inline val <S> $Lens<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Lens<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
    |inline val <S> $Optional<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Optional<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
    |inline val <S> $Prism<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Optional<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
    |inline val <S> $Getter<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Getter<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
    |inline val <S> $Setter<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Setter<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
    |inline val <S> $Traversal<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Traversal<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
    |inline val <S> $Fold<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Fold<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
    |inline val <S> $Every<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Every<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
    |""".trimMargin()
  }

private fun processOptionalSyntax(ele: ADT, optic: DataClassDsl) =
  optic.foci.filterNot { it is NonNullFocus }.joinToString(separator = "\n") { focus ->
    val targetClassName =
      when (focus) {
        is NullableFocus -> focus.nonNullClassName
        is OptionFocus -> focus.nestedClassName
        is NonNullFocus -> ""
      }

    """
    |inline val <S> $Iso<S, ${ele.sourceClassName}>.${focus.paramName}: $Optional<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Lens<S, ${ele.sourceClassName}>.${focus.paramName}: $Optional<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Optional<S, ${ele.sourceClassName}>.${focus.paramName}: $Optional<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Prism<S, ${ele.sourceClassName}>.${focus.paramName}: $Optional<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Setter<S, ${ele.sourceClassName}>.${focus.paramName}: $Setter<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Traversal<S, ${ele.sourceClassName}>.${focus.paramName}: $Traversal<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Fold<S, ${ele.sourceClassName}>.${focus.paramName}: $Fold<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Every<S, ${ele.sourceClassName}>.${focus.paramName}: $Every<S, $targetClassName> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |""".trimMargin()
  }

private fun processPrismSyntax(ele: ADT, dsl: SealedClassDsl): String =
  dsl.foci.joinToString(separator = "\n\n") { focus ->
    """
    |inline val <S> $Iso<S, ${ele.sourceClassName}>.${focus.paramName}: $Prism<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Lens<S, ${ele.sourceClassName}>.${focus.paramName}: $Optional<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Optional<S, ${ele.sourceClassName}>.${focus.paramName}: $Optional<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Prism<S, ${ele.sourceClassName}>.${focus.paramName}: $Prism<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Setter<S, ${ele.sourceClassName}>.${focus.paramName}: $Setter<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Traversal<S, ${ele.sourceClassName}>.${focus.paramName}: $Traversal<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Fold<S, ${ele.sourceClassName}>.${focus.paramName}: $Fold<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |inline val <S> $Every<S, ${ele.sourceClassName}>.${focus.paramName}: $Every<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |""".trimMargin()
  }
