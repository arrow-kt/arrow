package arrow.optics.plugin.internals

import com.google.devtools.ksp.getDeclaredProperties

fun generateLensDsl(ele: ADT, optic: DataClassDsl): Snippet {
  val (className, import) = resolveClassName(ele)
  return Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processLensSyntax(ele, optic.foci, className),
    imports = setOf(import),
  )
}

fun generatePrismDsl(ele: ADT, isoOptic: SealedClassDsl): Snippet {
  val (className, import) = resolveClassName(ele)
  return Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processPrismSyntax(ele, isoOptic, className),
    imports = setOf(import),
  )
}

fun generateIsoDsl(ele: ADT, isoOptic: ValueClassDsl): Snippet {
  val (className, import) = resolveClassName(ele)
  return Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processIsoSyntax(ele, isoOptic, className),
    imports = setOf(import),
  )
}

private fun processLensSyntax(ele: ADT, foci: List<Focus>, className: String): String {
  return if (ele.typeParameters.isEmpty()) {
    foci.joinToString(separator = "\n") { focus ->
      """
    |${ele.visibilityModifierName} inline val <__S> $Lens<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $Lens<__S, ${focus.classNameWithParameters}> inline get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} inline val <__S> $Optional<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $Optional<__S, ${focus.classNameWithParameters}> inline get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} inline val <__S> $Traversal<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $Traversal<__S, ${focus.classNameWithParameters}> inline get() = this + $className.${focus.escapedParamName}
    |
      """.trimMargin()
    }
  } else {
    val sourceClassNameWithParams = "${ele.sourceClassName}${ele.angledTypeParameters}"
    val joinedTypeParams = ele.typeParameters.joinToString(separator = ",")
    foci.joinToString(separator = "\n") { focus ->
      """
    |${ele.visibilityModifierName} inline fun <__S,$joinedTypeParams> $Lens<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $Lens<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} inline fun <__S,$joinedTypeParams> $Optional<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $Optional<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} inline fun <__S,$joinedTypeParams> $Traversal<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $Traversal<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |
      """.trimMargin()
    }
  }
}

private fun processPrismSyntax(ele: ADT, dsl: SealedClassDsl, className: String): String =
  if (ele.typeParameters.isEmpty()) {
    dsl.foci.joinToString(separator = "\n\n") { focus ->
      """
    |${ele.visibilityModifierName} inline val <__S> $Optional<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $Optional<__S, ${focus.classNameWithParameters}> inline get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} inline val <__S> $Prism<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $Prism<__S, ${focus.classNameWithParameters}> inline get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} inline val <__S> $Traversal<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $Traversal<__S, ${focus.classNameWithParameters}> inline get() = this + $className.${focus.escapedParamName}
    |
      """.trimMargin()
    }
  } else {
    dsl.foci.joinToString(separator = "\n\n") { focus ->
      val sourceClassNameWithParams = focus.refinedType?.qualifiedString() ?: "${ele.sourceClassName}${ele.angledTypeParameters}"
      val joinedTypeParams = when {
        focus.refinedArguments.isEmpty() -> ""
        else -> focus.refinedArguments.joinToString(separator = ",")
      }
      """
    |${ele.visibilityModifierName} inline fun <__S,$joinedTypeParams> $Optional<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $Optional<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} inline fun <__S,$joinedTypeParams> $Prism<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $Prism<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} inline fun <__S,$joinedTypeParams> $Traversal<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $Traversal<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |
      """.trimMargin()
    }
  }

private fun processIsoSyntax(ele: ADT, dsl: ValueClassDsl, className: String): String =
  if (ele.typeParameters.isEmpty()) {
    dsl.foci.joinToString(separator = "\n\n") { focus ->
      """
    |${ele.visibilityModifierName} inline val <__S> $Iso<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $Iso<__S, ${focus.classNameWithParameters}> inline get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} inline val <__S> $Lens<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $Lens<__S, ${focus.classNameWithParameters}> inline get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} inline val <__S> $Optional<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $Optional<__S, ${focus.classNameWithParameters}> inline get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} inline val <__S> $Prism<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $Prism<__S, ${focus.classNameWithParameters}> inline get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} inline val <__S> $Traversal<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $Traversal<__S, ${focus.classNameWithParameters}> inline get() = this + $className.${focus.escapedParamName}
    |
      """.trimMargin()
    }
  } else {
    dsl.foci.joinToString(separator = "\n\n") { focus ->
      val sourceClassNameWithParams = focus.refinedType?.qualifiedString() ?: "${ele.sourceClassName}${ele.angledTypeParameters}"
      val joinedTypeParams = when {
        focus.refinedArguments.isEmpty() -> ""
        else -> focus.refinedArguments.joinToString(separator = ",")
      }
      """
    |${ele.visibilityModifierName} inline fun <__S,$joinedTypeParams> $Iso<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $Iso<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} inline fun <__S,$joinedTypeParams> $Lens<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $Lens<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} inline fun <__S,$joinedTypeParams> $Optional<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $Optional<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} inline fun <__S,$joinedTypeParams> $Prism<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $Prism<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} inline fun <__S,$joinedTypeParams> $Traversal<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $Traversal<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |
      """.trimMargin()
    }
  }

private fun resolveClassName(ele: ADT): Pair<String, String> = if (hasPackageCollisions(ele)) {
  val classNameAlias = ele.sourceClassName.replace(".", "").replace("`", "").sanitize()
  val aliasImport = "import ${ele.sourceClassName} as $classNameAlias"
  classNameAlias to aliasImport
} else {
  ele.sourceClassName to ""
}

private fun hasPackageCollisions(ele: ADT): Boolean =
  ele.declaration.getDeclaredProperties().let { properties ->
    ele.packageName
      .split(".")
      .any { p ->
        properties.any { it.simpleName.asString() == p }
      }
  }
