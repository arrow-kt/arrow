package arrow.optics.plugin.internals

import arrow.optics.plugin.isData
import arrow.optics.plugin.isSealed
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import java.util.Locale

internal fun adt(c: KSClassDeclaration, logger: KSPLogger): ADT =
  ADT(
    c.packageName,
    c,
    c.targets().map { target ->
      when (target) {
        OpticsTarget.LENS ->
          evalAnnotatedDataClass(c, c.qualifiedNameOrSimpleName.lensErrorMessage, logger)
            .let(::LensTarget)
        OpticsTarget.PRISM -> evalAnnotatedPrismElement(c, logger).let(::PrismTarget)
        OpticsTarget.DSL -> evalAnnotatedDslElement(c, logger)
      }
    }
  )

internal fun KSClassDeclaration.targets(): List<OpticsTarget> =
  targetsFromOpticsAnnotation().let { targets ->
    when {
      isSealed ->
        listOf(OpticsTarget.PRISM, OpticsTarget.DSL)
          .filter { targets.isEmpty() || it in targets }
      else ->
        listOf(OpticsTarget.LENS, OpticsTarget.DSL)
          .filter { targets.isEmpty() || it in targets }
    }
  }

internal fun KSClassDeclaration.targetsFromOpticsAnnotation(): List<OpticsTarget> =
  annotations
    .find { it.annotationType.resolve().declaration.qualifiedName?.asString() == "arrow.optics.optics" }
    ?.arguments
    ?.flatMap { arg -> (arg.value as? ArrayList<*>).orEmpty().mapNotNull { it as? KSType } }
    ?.mapNotNull {
      when (it.qualifiedString() ) {
        "arrow.optics.OpticsTarget.LENS" -> OpticsTarget.LENS
        "arrow.optics.OpticsTarget.PRISM" -> OpticsTarget.PRISM
        "arrow.optics.OpticsTarget.DSL" -> OpticsTarget.DSL
        else -> null
      }
    }.orEmpty().distinct()

internal fun evalAnnotatedPrismElement(
  element: KSClassDeclaration,
  logger: KSPLogger
): List<Focus> =
  when {
    element.isSealed ->
      element.getSealedSubclasses().map {
        Focus(
          it.primaryConstructor?.returnType?.resolve()?.qualifiedString() ?: it.qualifiedNameOrSimpleName,
          it.simpleName.asString().replaceFirstChar { c -> c.lowercase(Locale.getDefault()) },
          it.superTypes.first().resolve()
        )
      }.toList()
    else -> {
      logger.error(element.qualifiedNameOrSimpleName.prismErrorMessage, element)
      emptyList()
    }
  }

internal val KSDeclaration.qualifiedNameOrSimpleName: String
  get() = (qualifiedName ?: simpleName).asSanitizedString()

internal fun evalAnnotatedDataClass(
  element: KSClassDeclaration,
  errorMessage: String,
  logger: KSPLogger
): List<Focus> =
  when {
    element.isData ->
      element
        .getConstructorTypesNames()
        .zip(element.getConstructorParamNames(), Focus.Companion::invoke)
    else -> {
      logger.error(errorMessage, element)
      emptyList()
    }
  }

internal fun evalAnnotatedDslElement(element: KSClassDeclaration, logger: KSPLogger): Target =
  when {
    element.isData ->
      DataClassDsl(
        element
          .getConstructorTypesNames()
          .zip(element.getConstructorParamNames(), Focus.Companion::invoke)
      )
    element.isSealed -> SealedClassDsl(evalAnnotatedPrismElement(element, logger))
    else -> throw IllegalStateException("should only be sealed or data by now")
  }

internal fun KSClassDeclaration.getConstructorTypesNames(): List<String> =
  primaryConstructor?.parameters?.map { it.type.resolve().qualifiedString() }.orEmpty()

internal fun KSType.qualifiedString(): String = when (declaration) {
  is KSTypeParameter -> {
    val n = declaration.simpleName.asSanitizedString()
    if (isMarkedNullable) "$n?" else n
  }
  else -> when (val qname = declaration.qualifiedName?.asSanitizedString()) {
    null -> toString()
    else -> {
      val withArgs = when {
        arguments.isEmpty() -> qname
        else -> "$qname<${arguments.joinToString(separator = ", ") { it.qualifiedString() }}>"
      }
      if (isMarkedNullable) "$withArgs?" else withArgs
    }
  }
}

internal fun KSTypeArgument.qualifiedString(): String = when (val ty = type?.resolve()) {
  null -> toString()
  else -> ty.qualifiedString()
}

internal fun KSClassDeclaration.getConstructorParamNames(): List<String> =
  primaryConstructor?.parameters?.mapNotNull { it.name?.asString() }.orEmpty()
