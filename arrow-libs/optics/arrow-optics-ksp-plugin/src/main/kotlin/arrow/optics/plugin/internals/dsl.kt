package arrow.optics.plugin.internals

import arrow.optics.plugin.OpticsProcessorOptions
import com.google.devtools.ksp.getDeclaredProperties

fun OpticsProcessorOptions.generateLensDsl(ele: ADT, optic: DataClassDsl): Snippet {
  val (className, classImport) = resolveClassName(ele)
  val (lensType, lensImport) = ele.resolveTypeName(Lens)
  val (optionalType, optionalImport) = ele.resolveTypeName(Optional)
  val (traversalType, traversalImport) = ele.resolveTypeName(Traversal)
  return Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processLensSyntax(ele, optic.foci, className, lensType, optionalType, traversalType),
    imports = setOf(classImport, lensImport, optionalImport, traversalImport).filter { it.isNotBlank() }.toSet(),
  )
}

fun OpticsProcessorOptions.generatePrismDsl(ele: ADT, isoOptic: SealedClassDsl): Snippet {
  val (className, classImport) = resolveClassName(ele)
  val (optionalType, optionalImport) = ele.resolveTypeName(Optional)
  val (prismType, prismImport) = ele.resolveTypeName(Prism)
  val (traversalType, traversalImport) = ele.resolveTypeName(Traversal)
  return Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processPrismSyntax(ele, isoOptic, className, optionalType, prismType, traversalType),
    imports = setOf(classImport, optionalImport, prismImport, traversalImport).filter { it.isNotBlank() }.toSet(),
  )
}

fun OpticsProcessorOptions.generateIsoDsl(ele: ADT, isoOptic: ValueClassDsl): Snippet {
  val (className, classImport) = resolveClassName(ele)
  val (isoType, isoImport) = ele.resolveTypeName(Iso)
  val (lensType, lensImport) = ele.resolveTypeName(Lens)
  val (optionalType, optionalImport) = ele.resolveTypeName(Optional)
  val (prismType, prismImport) = ele.resolveTypeName(Prism)
  val (traversalType, traversalImport) = ele.resolveTypeName(Traversal)
  return Snippet(
    `package` = ele.packageName,
    name = ele.simpleName,
    content = processIsoSyntax(ele, isoOptic, className, isoType, lensType, optionalType, prismType, traversalType),
    imports = setOf(classImport, isoImport, lensImport, optionalImport, prismImport, traversalImport).filter { it.isNotBlank() }.toSet(),
  )
}

private fun OpticsProcessorOptions.processLensSyntax(ele: ADT, foci: List<Focus>, className: String, lensType: String, optionalType: String, traversalType: String): String = if (ele.typeParameters.isEmpty()) {
  foci.joinToString(separator = "\n") { focus ->
    """
    |${ele.visibilityModifierName} $inlineText val <__S> $lensType<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $lensType<__S, ${focus.classNameWithParameters}> $inlineText get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} $inlineText val <__S> $optionalType<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $optionalType<__S, ${focus.classNameWithParameters}> $inlineText get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} $inlineText val <__S> $traversalType<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $traversalType<__S, ${focus.classNameWithParameters}> $inlineText get() = this + $className.${focus.escapedParamName}
    |
    """.trimMargin()
  }
} else {
  val sourceClassNameWithParams = "${ele.sourceClassName}${ele.angledTypeParameterNames}"
  val joinedTypeParams = ele.typeParameters.joinToString(separator = ",")
  foci.joinToString(separator = "\n") { focus ->
    """
    |${ele.visibilityModifierName} $inlineText fun <__S,$joinedTypeParams> $lensType<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $lensType<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} $inlineText fun <__S,$joinedTypeParams> $optionalType<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $optionalType<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} $inlineText fun <__S,$joinedTypeParams> $traversalType<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $traversalType<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |
    """.trimMargin()
  }
}

private fun OpticsProcessorOptions.processPrismSyntax(ele: ADT, dsl: SealedClassDsl, className: String, optionalType: String, prismType: String, traversalType: String): String = if (ele.typeParameters.isEmpty()) {
  dsl.foci.joinToString(separator = "\n\n") { focus ->
    """
    |${ele.visibilityModifierName} $inlineText val <__S> $optionalType<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $optionalType<__S, ${focus.classNameWithParameters}> $inlineText get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} $inlineText val <__S> $prismType<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $prismType<__S, ${focus.classNameWithParameters}> $inlineText get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} $inlineText val <__S> $traversalType<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $traversalType<__S, ${focus.classNameWithParameters}> $inlineText get() = this + $className.${focus.escapedParamName}
    |
    """.trimMargin()
  }
} else {
  dsl.foci.joinToString(separator = "\n\n") { focus ->
    val sourceClassNameWithParams = focus.refinedType?.qualifiedString() ?: "${ele.sourceClassName}${ele.angledTypeParameterNames}"
    val joinedTypeParams = when {
      focus.refinedArguments.isEmpty() -> ""
      else -> focus.refinedArguments.joinToString(separator = ",")
    }
    """
    |${ele.visibilityModifierName} $inlineText fun <__S,$joinedTypeParams> $optionalType<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $optionalType<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} $inlineText fun <__S,$joinedTypeParams> $prismType<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $prismType<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} $inlineText fun <__S,$joinedTypeParams> $traversalType<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $traversalType<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |
    """.trimMargin()
  }
}

private fun OpticsProcessorOptions.processIsoSyntax(ele: ADT, dsl: ValueClassDsl, className: String, isoType: String, lensType: String, optionalType: String, prismType: String, traversalType: String): String = if (ele.typeParameters.isEmpty()) {
  dsl.foci.joinToString(separator = "\n\n") { focus ->
    """
    |${ele.visibilityModifierName} $inlineText val <__S> $isoType<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $isoType<__S, ${focus.classNameWithParameters}> $inlineText get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} $inlineText val <__S> $lensType<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $lensType<__S, ${focus.classNameWithParameters}> $inlineText get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} $inlineText val <__S> $optionalType<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $optionalType<__S, ${focus.classNameWithParameters}> $inlineText get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} $inlineText val <__S> $prismType<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $prismType<__S, ${focus.classNameWithParameters}> $inlineText get() = this + $className.${focus.escapedParamName}
    |${ele.visibilityModifierName} $inlineText val <__S> $traversalType<__S, ${ele.sourceClassName}>.${focus.escapedParamName}: $traversalType<__S, ${focus.classNameWithParameters}> $inlineText get() = this + $className.${focus.escapedParamName}
    |
    """.trimMargin()
  }
} else {
  dsl.foci.joinToString(separator = "\n\n") { focus ->
    val sourceClassNameWithParams = focus.refinedType?.qualifiedString() ?: "${ele.sourceClassName}${ele.angledTypeParameterNames}"
    val joinedTypeParams = when {
      focus.refinedArguments.isEmpty() -> ""
      else -> focus.refinedArguments.joinToString(separator = ",")
    }
    """
    |${ele.visibilityModifierName} $inlineText fun <__S,$joinedTypeParams> $isoType<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $isoType<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} $inlineText fun <__S,$joinedTypeParams> $lensType<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $lensType<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} $inlineText fun <__S,$joinedTypeParams> $optionalType<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $optionalType<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} $inlineText fun <__S,$joinedTypeParams> $prismType<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $prismType<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
    |${ele.visibilityModifierName} $inlineText fun <__S,$joinedTypeParams> $traversalType<__S, $sourceClassNameWithParams>.${focus.escapedParamName}(): $traversalType<__S, ${focus.classNameWithParameters}> = this + $className.${focus.escapedParamName}()
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

private fun hasPackageCollisions(ele: ADT): Boolean = ele.declaration.getDeclaredProperties().let { properties ->
  ele.packageName
    .split(".")
    .any { p ->
      properties.any { it.simpleName.asString() == p }
    }
}
