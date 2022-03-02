package arrow.optics.plugin.internals

fun generateLensDsl(ele: ADT, optic: DataClassDsl): Snippet =
  Snippet(
    `package` = ele.packageNameAsString,
    name = ele.qualifiedNameOrSimpleName,
    content = processLensSyntax(ele, optic.foci)
  )

fun generateOptionalDsl(ele: ADT, optic: DataClassDsl): Snippet =
  Snippet(
    `package` = ele.packageNameAsString,
    name = ele.qualifiedNameOrSimpleName,
    content = processOptionalSyntax(ele, optic)
  )

fun generatePrismDsl(ele: ADT, isoOptic: SealedClassDsl): Snippet =
  Snippet(
    `package` = ele.packageNameAsString,
    name = ele.qualifiedNameOrSimpleName,
    content = processPrismSyntax(ele, isoOptic)
  )

private fun processLensSyntax(ele: ADT, foci: List<Focus>): String =
  foci.joinToString(separator = "\n") { focus ->
    """
    |inline val <S> $Iso<S, ${ele.qualifiedNameOrSimpleName}>.${focus.lensParamName()}: $Lens<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.lensParamName()}
    |inline val <S> $Lens<S, ${ele.qualifiedNameOrSimpleName}>.${focus.lensParamName()}: $Lens<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.lensParamName()}
    |inline val <S> $Optional<S, ${ele.qualifiedNameOrSimpleName}>.${focus.lensParamName()}: $Optional<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.lensParamName()}
    |inline val <S> $Prism<S, ${ele.qualifiedNameOrSimpleName}>.${focus.lensParamName()}: $Optional<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.lensParamName()}
    |inline val <S> $Getter<S, ${ele.qualifiedNameOrSimpleName}>.${focus.lensParamName()}: $Getter<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.lensParamName()}
    |inline val <S> $Setter<S, ${ele.qualifiedNameOrSimpleName}>.${focus.lensParamName()}: $Setter<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.lensParamName()}
    |inline val <S> $Traversal<S, ${ele.qualifiedNameOrSimpleName}>.${focus.lensParamName()}: $Traversal<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.lensParamName()}
    |inline val <S> $Fold<S, ${ele.qualifiedNameOrSimpleName}>.${focus.lensParamName()}: $Fold<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.lensParamName()}
    |inline val <S> $Every<S, ${ele.qualifiedNameOrSimpleName}>.${focus.lensParamName()}: $Every<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.lensParamName()}
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
    |inline val <S> $Iso<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Optional<S, $targetClassName> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Lens<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Optional<S, $targetClassName> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Optional<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Optional<S, $targetClassName> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Prism<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Optional<S, $targetClassName> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Setter<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Setter<S, $targetClassName> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Traversal<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Traversal<S, $targetClassName> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Fold<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Fold<S, $targetClassName> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Every<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Every<S, $targetClassName> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |""".trimMargin()
  }

private fun processPrismSyntax(ele: ADT, dsl: SealedClassDsl): String =
  dsl.foci.joinToString(separator = "\n\n") { focus ->
    """
    |inline val <S> $Iso<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Prism<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Lens<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Optional<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Optional<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Optional<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Prism<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Prism<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Setter<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Setter<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Traversal<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Traversal<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Fold<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Fold<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |inline val <S> $Every<S, ${ele.qualifiedNameOrSimpleName}>.${focus.paramName}: $Every<S, ${focus.className}> inline get() = this + ${ele.qualifiedNameOrSimpleName}.${focus.paramName}
    |""".trimMargin()
  }
