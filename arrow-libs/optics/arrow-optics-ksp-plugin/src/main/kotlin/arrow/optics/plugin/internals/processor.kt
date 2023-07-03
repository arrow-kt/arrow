package arrow.optics.plugin.internals

import arrow.optics.plugin.isDataClass
import arrow.optics.plugin.isSealed
import arrow.optics.plugin.isValue
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.symbol.Variance.INVARIANT
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
        OpticsTarget.OPTIONAL ->
          evalAnnotatedDataClass(c, c.qualifiedNameOrSimpleName.optionalErrorMessage, logger)
            .let(::OptionalTarget)
        OpticsTarget.ISO ->
          evalAnnotatedIsoElement(c, c.qualifiedNameOrSimpleName.isoErrorMessage, logger)
            .let(::IsoTarget)
        OpticsTarget.PRISM ->
          evalAnnotatedPrismElement(c, c.qualifiedNameOrSimpleName.prismErrorMessage, logger)
            .let(::PrismTarget)
        OpticsTarget.DSL -> evalAnnotatedDslElement(c, logger)
      }
    }
  )

internal fun KSClassDeclaration.targets(): List<OpticsTarget> =
  targetsFromOpticsAnnotation().let { targets ->
    when {
      isSealed ->
        if (targets.isEmpty())
          listOf(OpticsTarget.PRISM, OpticsTarget.DSL)
        else targets.filter { it == OpticsTarget.PRISM || it == OpticsTarget.DSL }
      isValue ->
        listOf(OpticsTarget.ISO, OpticsTarget.DSL)
          .filter { targets.isEmpty() || it in targets }
      else ->
        if (targets.isEmpty())
          listOf(OpticsTarget.ISO, OpticsTarget.LENS, OpticsTarget.OPTIONAL, OpticsTarget.DSL)
        else targets.filter {
          when (it) {
            OpticsTarget.ISO, OpticsTarget.LENS, OpticsTarget.OPTIONAL, OpticsTarget.DSL -> true
            else -> false
          }
        }
    }
  }

internal fun KSClassDeclaration.targetsFromOpticsAnnotation(): List<OpticsTarget> =
  annotations
    .find { it.annotationType.resolve().declaration.qualifiedName?.asString() == "arrow.optics.optics" }
    ?.arguments
    ?.flatMap { (it.value as? ArrayList<*>).orEmpty().mapNotNull { it as? KSType } }
    ?.mapNotNull {
      when (it.qualifiedString() ) {
        "arrow.optics.OpticsTarget.ISO" -> OpticsTarget.ISO
        "arrow.optics.OpticsTarget.LENS" -> OpticsTarget.LENS
        "arrow.optics.OpticsTarget.PRISM" -> OpticsTarget.PRISM
        "arrow.optics.OpticsTarget.OPTIONAL" -> OpticsTarget.OPTIONAL
        "arrow.optics.OpticsTarget.DSL" -> OpticsTarget.DSL
        else -> null
      }
    }.orEmpty().distinct()

internal fun evalAnnotatedPrismElement(
  element: KSClassDeclaration,
  errorMessage: String,
  logger: KSPLogger
): List<Focus> =
  when {
    element.isSealed -> {
      val sealedSubclasses = element.getSealedSubclasses().toList()
      sealedSubclasses.map {
        Focus(
          it.primaryConstructor?.returnType?.resolve()?.qualifiedString() ?: it.qualifiedNameOrSimpleName,
          it.simpleName.asString().replaceFirstChar { c -> c.lowercase(Locale.getDefault()) },
          it.superTypes.first().resolve(),
          onlyOneSealedSubclass = sealedSubclasses.size == 1
        )
      }
    }
    else -> {
      logger.error(errorMessage, element)
      emptyList()
    }
  }

internal val KSDeclaration.qualifiedNameOrSimpleName: String
  get() = (qualifiedName ?: simpleName).asSanitizedString()

internal fun KSClassDeclaration.sealedSubclassFqNameList(): List<String> =
  getSealedSubclasses().mapNotNull { it.qualifiedName?.asString() }.toList()

internal fun evalAnnotatedDataClass(
  element: KSClassDeclaration,
  errorMessage: String,
  logger: KSPLogger
): List<Focus> =
  when {
    element.isDataClass ->
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
    element.isDataClass ->
      DataClassDsl(
        element
          .getConstructorTypesNames()
          .zip(element.getConstructorParamNames(), Focus.Companion::invoke)
      )
    element.isValue ->
      ValueClassDsl(
        Focus(element.getConstructorTypesNames().first(), element.getConstructorParamNames().first())
      )
    element.isSealed ->
      SealedClassDsl(evalAnnotatedPrismElement(element, element.qualifiedNameOrSimpleName.prismErrorMessage, logger))
    else -> throw IllegalStateException("should only be sealed, data, or value by now")
  }

internal fun evalAnnotatedIsoElement(
  element: KSClassDeclaration,
  errorMessage: String,
  logger: KSPLogger
): List<Focus> =
  when {
    element.isDataClass ->
      element
        .getConstructorTypesNames()
        .zip(element.getConstructorParamNames(), Focus.Companion::invoke)
        .takeIf { it.size <= 22 }
        ?: run {
          logger.error(element.qualifiedNameOrSimpleName.isoTooBigErrorMessage, element)
          emptyList()
        }
    element.isValue ->
      listOf(Focus(element.getConstructorTypesNames().first(), element.getConstructorParamNames().first()))
    else -> {
      logger.error(errorMessage, element)
      emptyList()
    }
  }

internal fun KSClassDeclaration.getConstructorTypesNames(): List<String> =
  primaryConstructor?.parameters?.map { it.type.resolve().qualifiedString() }.orEmpty()

internal fun KSType.qualifiedString(prefix: String = ""): String = when (declaration) {
  is KSTypeParameter -> {
    val n = declaration.simpleName.asSanitizedString(prefix = prefix)
    if (isMarkedNullable) "$n?" else n
  }
  else -> when (val qname = declaration.qualifiedName?.asSanitizedString(prefix = prefix)) {
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
  else -> ty.qualifiedString(prefix = "${variance.label} ".takeIf { variance != INVARIANT }.orEmpty())
}

internal fun KSClassDeclaration.getConstructorParamNames(): List<String> =
  primaryConstructor?.parameters?.mapNotNull { it.name?.asString() }.orEmpty()
