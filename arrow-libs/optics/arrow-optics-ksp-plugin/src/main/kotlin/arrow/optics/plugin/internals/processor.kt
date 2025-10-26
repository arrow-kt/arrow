package arrow.optics.plugin.internals

import arrow.optics.plugin.companionObject
import arrow.optics.plugin.isDataClass
import arrow.optics.plugin.isSealed
import arrow.optics.plugin.isValue
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.Variance.INVARIANT
import com.google.devtools.ksp.symbol.Variance.STAR
import java.util.Locale

internal fun adt(c: KSClassDeclaration, logger: KSPLogger): ADT = ADT(
  c,
  c.targets().map { target ->
    when (target) {
      OpticsTarget.LENS ->
        evalAnnotatedDataClass(c, c.qualifiedNameOrSimpleName.lensErrorMessage, logger)
          .orEmpty()
          .let(::LensTarget)

      OpticsTarget.ISO ->
        evalAnnotatedValueClass(c, c.qualifiedNameOrSimpleName.isoErrorMessage, logger)
          .let(::IsoTarget)

      OpticsTarget.PRISM ->
        evalAnnotatedPrismElement(c, c.qualifiedNameOrSimpleName.prismErrorMessage, logger)
          .let(::PrismTarget)

      OpticsTarget.DSL -> evalAnnotatedDslElement(c, logger)

      OpticsTarget.COPY -> CopyTarget(c.companionObject!!.qualifiedNameOrSimpleName)
    }
  },
)

internal fun KSClassDeclaration.targets(): Set<OpticsTarget> = targetsFromOpticsAnnotation().let { targets ->
  when {
    isSealed -> targets intersect SEALED_TARGETS
    isValue -> targets intersect VALUE_TARGETS
    else -> targets intersect OTHER_TARGETS
  }
} + listOfNotNull(OpticsTarget.COPY.takeIf { hasOpticsCopy() })

internal fun KSClassDeclaration.targetsFromOpticsAnnotation(): Set<OpticsTarget> = annotations
  .single { it.annotationType.resolve().declaration.qualifiedName?.asString() == "arrow.optics.optics" }
  .arguments
  .flatMap { (it.value as? List<*>).orEmpty() }
  .mapNotNull {
    val qualifiedName = when (it) {
      is KSType -> it.qualifiedString()
      is KSDeclaration -> it.qualifiedName?.asString()
      else -> null
    }
    when (qualifiedName) {
      "arrow.optics.OpticsTarget.ISO" -> OpticsTarget.ISO
      "arrow.optics.OpticsTarget.LENS" -> OpticsTarget.LENS
      "arrow.optics.OpticsTarget.PRISM" -> OpticsTarget.PRISM
      "arrow.optics.OpticsTarget.DSL" -> OpticsTarget.DSL
      else -> null
    }
  }.ifEmpty { DEFAULT_TARGETS }.toSet()

internal fun KSClassDeclaration.hasOpticsCopy(): Boolean = annotations.any {
  it.annotationType.resolve().declaration.qualifiedName?.asString() == "arrow.optics.optics.copy"
}

internal fun evalAnnotatedPrismElement(
  element: KSClassDeclaration,
  errorMessage: String,
  logger: KSPLogger,
): List<Focus> = when {
  element.isSealed -> {
    val sealedSubclasses = element.getSealedSubclasses().toList()
    sealedSubclasses.map { subclass ->
      Focus(
        subclass.qualifiedNameOrSimpleName,
        subclass.simpleName.asString().replaceFirstChar { c -> c.lowercase(Locale.getDefault()) },
        subclass.superTypes.first().resolve(),
        onlyOneSealedSubclass = sealedSubclasses.size == 1,
        classNameWithParameters = subclass.qualifiedNameOrSimpleNameWithTypeParameters,
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

internal val KSClassDeclaration.qualifiedNameOrSimpleNameWithTypeParameters: String
  get() = when {
    typeParameters.isEmpty() -> qualifiedNameOrSimpleName
    else -> "$qualifiedNameOrSimpleName<${typeParameters.joinToString { it.simpleName.asString() }}>"
  }

internal fun evalAnnotatedDataClass(
  element: KSClassDeclaration,
  errorMessage: String,
  logger: KSPLogger,
): List<Focus>? {
  return when {
    element.isDataClass ->
      element
        .getConstructorTypesNames()
        .zip(element.getConstructorParamNames(), Focus.Companion::invoke)
    element.isSealed -> {
      val properties = element
        .getDeclaredProperties()
        .filter { it.isAbstract() && it.extensionReceiver == null }

      if (properties.none()) {
        logger.info(element.qualifiedNameOrSimpleName.sealedLensNoTargets, element)
        return null
      }

      val subclasses = element.getSealedSubclasses()

      if (subclasses.any { !it.isDataClass }) {
        logger.info(element.qualifiedNameOrSimpleName.sealedLensNonDataClassChildren, element)
        return null
      }

      val (goodProperties, badProperties) = properties.partition { it.sameInChildren(subclasses) }

      badProperties.forEach {
        logger.info(it.qualifiedNameOrSimpleName.sealedLensChangedProperties, element)
      }

      val propertyNames = goodProperties
        .map { it.simpleName.asString() }

      val nonConstructorOverrides = subclasses
        .any { subclass ->
          val parameters = subclass.getConstructorParamNames()
          propertyNames.any { it !in parameters }
        }

      if (nonConstructorOverrides) {
        logger.info(element.qualifiedNameOrSimpleName.sealedLensConstructorOverridesOnly, element)
        return null
      }

      goodProperties
        .map { it.type.resolve().qualifiedString() }
        .zip(propertyNames) { type, name ->
          Focus(
            fullName = type,
            paramName = name,
            subclasses = subclasses.map { it.qualifiedNameOrSimpleName }.toList(),
          )
        }
        .toList()
    }
    else -> {
      logger.error(errorMessage, element)
      emptyList()
    }
  }
}

fun KSPropertyDeclaration.sameInChildren(subclasses: Sequence<KSClassDeclaration>): Boolean = subclasses.all { subclass ->
  val property = subclass.getAllProperties().singleOrNull { it.simpleName == this.simpleName }
  when (property) {
    null -> false
    else -> property.type.resolve() == this.type.resolve()
  }
}

internal fun evalAnnotatedValueClass(
  element: KSClassDeclaration,
  errorMessage: String,
  logger: KSPLogger,
): List<Focus> = when {
  element.isValue ->
    listOf(Focus(element.getConstructorTypesNames().first(), element.getConstructorParamNames().first()))
  else -> {
    logger.error(errorMessage, element)
    emptyList()
  }
}

internal fun evalAnnotatedDslElement(element: KSClassDeclaration, logger: KSPLogger): Target = when {
  element.isDataClass ->
    DataClassDsl(
      element
        .getConstructorTypesNames()
        .zip(element.getConstructorParamNames(), Focus.Companion::invoke),
    )
  element.isValue ->
    ValueClassDsl(
      Focus(element.getConstructorTypesNames().first(), element.getConstructorParamNames().first()),
    )
  element.isSealed ->
    SealedClassDsl(evalAnnotatedPrismElement(element, element.qualifiedNameOrSimpleName.prismErrorMessage, logger))
  else -> throw IllegalStateException("should only be sealed, data, or value by now")
}

internal fun KSClassDeclaration.getConstructorTypesNames(): List<String> = primaryConstructor?.parameters?.map { it.type.resolve().qualifiedString() }.orEmpty()

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

internal fun KSTypeArgument.qualifiedString(): String {
  if (variance == STAR) return "*"
  val ty = type?.resolve() ?: return toString()
  return when (variance) {
    STAR -> "*"
    INVARIANT -> ty.qualifiedString()
    else -> ty.qualifiedString(prefix = "${variance.label} ")
  }
}

internal fun KSClassDeclaration.getConstructorParamNames(): List<String> = primaryConstructor?.parameters?.mapNotNull { it.name?.asString() }.orEmpty()
