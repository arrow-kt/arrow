package arrow.optics.plugin.internals

import arrow.optics.plugin.isData
import arrow.optics.plugin.isSealed
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.util.Locale

internal fun adt(c: KSClassDeclaration, logger: KSPLogger): ADT =
  ADT(
    c.packageName,
    c,
    c.targets().map { target ->
      when (target) {
        OpticsTarget.LENS ->
          evalAnnotatedDataClass(c, c.simpleName.asString().lensErrorMessage, logger)
            .let(::LensTarget)
        OpticsTarget.OPTIONAL ->
          evalAnnotatedDataClass(c, c.simpleName.asString().optionalErrorMessage, logger)
            .let(::OptionalTarget)
        OpticsTarget.ISO -> evalAnnotatedIsoElement(c, logger).let(::IsoTarget)
        OpticsTarget.PRISM -> evalAnnotatedPrismElement(c, logger).let(::PrismTarget)
        OpticsTarget.DSL -> evalAnnotatedDslElement(c, logger)
      }
    }
  )

internal fun KSClassDeclaration.targets(): List<OpticsTarget> =
  if (isSealed) listOf(OpticsTarget.PRISM, OpticsTarget.DSL)
  else listOf(OpticsTarget.ISO, OpticsTarget.LENS, OpticsTarget.OPTIONAL, OpticsTarget.DSL)

internal fun evalAnnotatedPrismElement(
  element: KSClassDeclaration,
  logger: KSPLogger
): List<Focus> =
  when {
    element.isSealed ->
      element.sealedSubclassFqNameList().map {
        Focus(
          it,
          it.substringAfterLast(".").replaceFirstChar { c -> c.lowercase(Locale.getDefault()) }
        )
      }
    else -> {
      logger.error(element.simpleName.asString().prismErrorMessage, element)
      emptyList()
    }
  }

internal fun KSClassDeclaration.sealedSubclassFqNameList(): List<String> =
  getSealedSubclasses().mapNotNull { it.qualifiedName?.asString() }.toList()

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

internal fun evalAnnotatedIsoElement(element: KSClassDeclaration, logger: KSPLogger): List<Focus> =
  when {
    element.isData ->
      element
        .getConstructorTypesNames()
        .zip(element.getConstructorParamNames(), Focus.Companion::invoke)
        .takeIf { it.size <= 22 }
        ?: run {
          logger.error(element.simpleName.asString().isoTooBigErrorMessage, element)
          emptyList()
        }
    else -> {
      logger.error(element.simpleName.asString().isoErrorMessage, element)
      emptyList()
    }
  }

internal fun KSClassDeclaration.getConstructorTypesNames(): List<String> =
  primaryConstructor?.parameters?.map { it.type.resolve().toString() }.orEmpty()

internal fun KSClassDeclaration.getConstructorParamNames(): List<String> =
  primaryConstructor?.parameters?.mapNotNull { it.name?.asString() }.orEmpty()
