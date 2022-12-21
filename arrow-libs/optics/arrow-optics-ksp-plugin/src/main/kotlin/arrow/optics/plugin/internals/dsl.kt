package arrow.optics.plugin.internals

fun generateLensDsl(ele: ADT, optic: DataClassDsl): Snippet =
  Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processLensSyntax(ele, optic.foci)
  )

fun generatePrismDsl(ele: ADT, isoOptic: SealedClassDsl): Snippet =
  Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processPrismSyntax(ele, isoOptic)
  )

private fun processLensSyntax(ele: ADT, foci: List<Focus>): String =
  if (ele.typeParameters.isEmpty()) {
    foci.joinToString(separator = "\n") { focus ->
    """
    |${ele.visibilityModifierName} inline val <S> $Lens<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Lens<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
    |${ele.visibilityModifierName} inline val <S> $Optional<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Optional<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
    |${ele.visibilityModifierName} inline val <S> $Traversal<S, ${ele.sourceClassName}>.${focus.lensParamName()}: $Traversal<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.lensParamName()}
    |""".trimMargin()
    }
  } else {
    val sourceClassNameWithParams = "${ele.sourceClassName}${ele.angledTypeParameters}"
    val joinedTypeParams = ele.typeParameters.joinToString(separator=",")
    foci.joinToString(separator = "\n") { focus ->
      """
    |${ele.visibilityModifierName} inline fun <S,$joinedTypeParams> $Lens<S, $sourceClassNameWithParams>.${focus.lensParamName()}(): $Lens<S, ${focus.className}> = this + ${ele.sourceClassName}.${focus.lensParamName()}()
    |${ele.visibilityModifierName} inline fun <S,$joinedTypeParams> $Optional<S, $sourceClassNameWithParams>.${focus.lensParamName()}(): $Optional<S, ${focus.className}> = this + ${ele.sourceClassName}.${focus.lensParamName()}()
    |${ele.visibilityModifierName} inline fun <S,$joinedTypeParams> $Traversal<S, $sourceClassNameWithParams>.${focus.lensParamName()}(): $Traversal<S, ${focus.className}> = this + ${ele.sourceClassName}.${focus.lensParamName()}()
    |""".trimMargin()
    }
  }

private fun processPrismSyntax(ele: ADT, dsl: SealedClassDsl): String =
  if (ele.typeParameters.isEmpty()) {
    dsl.foci.joinToString(separator = "\n\n") { focus ->
    """
    |${ele.visibilityModifierName} inline val <S> $Optional<S, ${ele.sourceClassName}>.${focus.paramName}: $Optional<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |${ele.visibilityModifierName} inline val <S> $Prism<S, ${ele.sourceClassName}>.${focus.paramName}: $Prism<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |${ele.visibilityModifierName} inline val <S> $Traversal<S, ${ele.sourceClassName}>.${focus.paramName}: $Traversal<S, ${focus.className}> inline get() = this + ${ele.sourceClassName}.${focus.paramName}
    |""".trimMargin()
    }
  } else {
    dsl.foci.joinToString(separator = "\n\n") { focus ->
      val sourceClassNameWithParams = focus.refinedType?.qualifiedString() ?: "${ele.sourceClassName}${ele.angledTypeParameters}"
      val joinedTypeParams = when {
        focus.refinedArguments.isEmpty() -> ""
        else -> focus.refinedArguments.joinToString(separator=",")
      }
    """
    |${ele.visibilityModifierName} inline fun <S,$joinedTypeParams> $Optional<S, $sourceClassNameWithParams>.${focus.paramName}(): $Optional<S, ${focus.className}> = this + ${ele.sourceClassName}.${focus.paramName}()
    |${ele.visibilityModifierName} inline fun <S,$joinedTypeParams> $Prism<S, $sourceClassNameWithParams>.${focus.paramName}(): $Prism<S, ${focus.className}> = this + ${ele.sourceClassName}.${focus.paramName}()
    |${ele.visibilityModifierName} inline fun <S,$joinedTypeParams> $Traversal<S, $sourceClassNameWithParams>.${focus.paramName}(): $Traversal<S, ${focus.className}> = this + ${ele.sourceClassName}.${focus.paramName}()
    |""".trimMargin()
    }
  }
