package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticKind
import arrow.optics.plugin.OpticsClassKind
import arrow.optics.plugin.OpticsNames
import arrow.optics.plugin.OpticsTargetKind
import arrow.optics.plugin.computeTargets
import arrow.optics.plugin.isUsableTopLevelVisibility
import arrow.optics.plugin.lowercaseFirst
import arrow.optics.plugin.memberVisibility
import arrow.optics.plugin.mostRestrictive
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.getSealedClassInheritors
import org.jetbrains.kotlin.fir.declarations.primaryConstructorIfAny
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.declarations.resolvePhase
import org.jetbrains.kotlin.fir.declarations.utils.isData
import org.jetbrains.kotlin.fir.declarations.utils.isInlineOrValue
import org.jetbrains.kotlin.fir.declarations.utils.visibility
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.FirAnnotationCall
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.packageFqName
import org.jetbrains.kotlin.fir.resolve.fullyExpandedType
import org.jetbrains.kotlin.fir.resolve.providers.firProvider
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.symbols.lazyResolveToPhase
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeStarProjection
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirUserTypeRef
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.fir.types.isMarkedNullable
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

/** A single base-optic focus discovered on the source class, as seen from FIR. */
data class FirFocus(
  val kind: OpticKind,
  val opticName: Name,
  /** The focus type as used for a *monomorphic* parent (field type for a lens, `Sub<*>` for a prism). */
  val focusType: ConeKotlinType?,
  /** For lenses/isos: the source component (constructor parameter / property) name. */
  val componentName: Name? = null,
  /** For prisms: the subclass symbol (used for generic parents, algo §6). */
  val subclass: FirClassSymbol<*>? = null,
  /** For prisms with a generic parent: the subclass's supertype, e.g. `Parent<String, C>`. */
  val refinedSource: ConeKotlinType? = null,
  /**
   * Visibility of the generated companion member: the most restrictive of the constructors,
   * properties and subclasses its body relies on (review 1.1, 2.2). Only meaningful once types are resolved.
   */
  val visibility: Visibility = Visibilities.Public,
)

/** Reads `@optics`-annotated FIR class symbols and extracts the foci to generate. */
object FirOpticsExtractor {
  /**
   * The kind of [symbol]. Only reads flags that are available in the raw FIR (explicit modifiers and
   * the declared primary constructor), so it is safe to call before status resolution.
   */
  @OptIn(DirectDeclarationsAccess::class)
  fun classKind(symbol: FirRegularClassSymbol): OpticsClassKind {
    if (symbol.classKind != ClassKind.CLASS && symbol.classKind != ClassKind.INTERFACE) return OpticsClassKind.INELIGIBLE
    return when {
      symbol.isData && symbol.classKind == ClassKind.CLASS -> OpticsClassKind.DATA

      symbol.isInlineOrValue && symbol.classKind == ClassKind.CLASS -> {
        // an iso needs exactly one field (multi-field value classes are ineligible)
        val ctor = symbol.declarationSymbols.filterIsInstance<FirConstructorSymbol>().firstOrNull { it.isPrimary }
        if (ctor?.valueParameterSymbols?.size == 1) OpticsClassKind.VALUE else OpticsClassKind.INELIGIBLE
      }

      symbol.rawStatus.modality == Modality.SEALED -> OpticsClassKind.SEALED

      else -> OpticsClassKind.INELIGIBLE
    }
  }

  /** Whether the `arrow-optics` runtime the generated code refers to is on the classpath. */
  fun runtimeAvailable(session: FirSession): Boolean = session.symbolProvider.getClassLikeSymbolByClassId(OpticsNames.PLENS) != null

  /**
   * Base optic foci to generate as companion members of [symbol], honouring the requested targets.
   *
   * When [resolveFocusTypes] is `false` the result is a superset used only for *names*. When it is
   * `true`, foci whose names clash with each other are dropped (none of them is generated, review 1.4).
   */
  fun foci(symbol: FirRegularClassSymbol, resolveFocusTypes: Boolean, session: FirSession): List<FirFocus> {
    val all = allFoci(symbol, resolveFocusTypes, session)
    if (!resolveFocusTypes) return all
    val clashing = clashingNames(all)
    return all.filter { it.opticName !in clashing }
  }

  /** All foci of [symbol], without removing clashing names. */
  fun allFoci(symbol: FirRegularClassSymbol, resolveFocusTypes: Boolean, session: FirSession): List<FirFocus> {
    val targets = effectiveTargets(symbol)
    val all = when (classKind(symbol)) {
      OpticsClassKind.DATA -> constructorFoci(symbol, session, OpticKind.LENS, resolveFocusTypes)
      OpticsClassKind.VALUE -> constructorFoci(symbol, session, OpticKind.ISO, resolveFocusTypes)
      OpticsClassKind.SEALED -> prismFoci(symbol, session, resolveFocusTypes) + sealedLensFoci(symbol, session, resolveFocusTypes)
      else -> emptyList()
    }
    return all.filter { targetOf(it.kind) in targets }
  }

  /**
   * Names of the members the user declared in [companion] (empty for a generated companion); optics
   * with those names are never generated (review 1.4).
   */
  @OptIn(DirectDeclarationsAccess::class)
  fun userMemberNames(companion: FirClassSymbol<*>?): Set<Name> {
    if (companion == null || keyOf(companion.origin) != null) return emptySet()
    return companion.declarationSymbols
      .filterIsInstance<FirCallableSymbol<*>>()
      .filter { keyOf(it.origin) == null }
      .mapNotNullTo(mutableSetOf()) { it.callableId?.callableName }
  }

  /** Names shared by more than one focus. */
  fun clashingNames(foci: List<FirFocus>): Set<Name> = foci.groupingBy { it.opticName }.eachCount().filterValues { it > 1 }.keys

  private fun targetOf(kind: OpticKind): OpticsTargetKind = when (kind) {
    OpticKind.LENS -> OpticsTargetKind.LENS
    OpticKind.ISO -> OpticsTargetKind.ISO
    OpticKind.PRISM -> OpticsTargetKind.PRISM
  }

  /** Whether the DSL composition extensions should be generated for [symbol]. */
  fun dslEnabled(symbol: FirRegularClassSymbol): Boolean = OpticsTargetKind.DSL in effectiveTargets(symbol)

  /**
   * The foci that get DSL composition helpers. Per algo section 8.4 a sealed type contributes only its
   * prism family - its shared-property lenses (section 5.2) do *not* get DSL variants.
   */
  @OptIn(SymbolInternals::class)
  fun dslFoci(source: FirRegularClassSymbol, resolveFocusTypes: Boolean, session: FirSession): List<FirFocus> {
    val isSealed = classKind(source) == OpticsClassKind.SEALED
    val userNames = if (resolveFocusTypes) userMemberNames(source.companionObjectSymbol) else emptySet()
    return foci(source, resolveFocusTypes, session).filter { !(isSealed && it.kind == OpticKind.LENS) && it.opticName !in userNames }
  }

  /**
   * Visibility of the top-level DSL extensions for [focus], or `null` when they cannot be generated
   * because they would have to be `private` (in a synthetic file) or `protected` (review 1.1).
   */
  fun dslVisibility(source: FirRegularClassSymbol, focus: FirFocus, session: FirSession): Visibility? {
    val visibility = mostRestrictive(effectiveVisibility(source, session), focus.visibility)
    return visibility.takeIf { isUsableTopLevelVisibility(it) }
  }

  /** The effective target set (algo §2.3): requested ∩ kind-allowed, plus COPY when present. */
  @OptIn(SymbolInternals::class)
  fun effectiveTargets(symbol: FirRegularClassSymbol): Set<OpticsTargetKind> {
    if (symbol.annotations.any { it.checkEvenIfUnresolved(OpticsNames.SERIALIZABLE_ANNOTATION) } && symbol.companionObjectSymbol == null) return emptySet()

    val requested = requestedTargets(symbol)
    val hasCopy = symbol.annotations.any { it.checkEvenIfUnresolved(OpticsNames.OPTICS_COPY_ANNOTATION) }
    return computeTargets(classKind(symbol), requested, hasCopy)
  }

  /**
   * Parse the `targets` array of `@optics(...)`, collecting the referenced [OpticsTargetKind]s.
   *
   * We walk the *raw* annotation argument expressions (only the annotation's class id is forced to
   * resolve) and read the referenced enum-entry names. This is robust to the resolution phase at
   * which generation runs — relying on `resolvedAnnotationsWithArguments.argumentMapping` is not, as
   * the argument mapping may not yet be populated when `getCallableNamesForClass` runs (review §2.6).
   */
  @OptIn(SymbolInternals::class)
  private fun requestedTargets(symbol: FirRegularClassSymbol): Set<OpticsTargetKind> {
    val annotation = symbol.annotations.firstOrNull { it.checkEvenIfUnresolved(OpticsNames.OPTICS_ANNOTATION) }
    if (annotation !is FirAnnotationCall) return emptySet()

    val found = mutableSetOf<OpticsTargetKind>()
    val collector = object : FirVisitorVoid() {
      override fun visitElement(element: FirElement) {
        element.acceptChildren(this)
      }

      override fun visitPropertyAccessExpression(propertyAccessExpression: FirPropertyAccessExpression) {
        when (propertyAccessExpression.calleeReference.name.asString()) {
          "ISO" -> found += OpticsTargetKind.ISO
          "LENS" -> found += OpticsTargetKind.LENS
          "PRISM" -> found += OpticsTargetKind.PRISM
          "DSL" -> found += OpticsTargetKind.DSL
          // OPTIONAL is silently dropped (algo §2.3)
        }
        propertyAccessExpression.acceptChildren(this)
      }
    }
    annotation.argumentList.arguments.forEach { it.accept(collector, null) }
    return found
  }

  /**
   * LENS foci for abstract properties that are uniform across every (data-class) subclass of a
   * sealed type (algo §5.2). Only monomorphic parents for now.
   */
  private fun sealedLensFoci(symbol: FirRegularClassSymbol, session: FirSession, resolveFocusTypes: Boolean): List<FirFocus> {
    if (symbol.typeParameterSymbols.isNotEmpty()) return emptyList()
    val abstractProps = mutableListOf<FirPropertySymbol>()
    symbol.processAllDeclarations(session) visitor@{
      if (it !is FirPropertySymbol) return@visitor
      if (!it.isAbstractMemberOf(symbol)) return@visitor
      if (it.receiverParameterSymbol != null) return@visitor
      abstractProps.add(it)
    }
    if (abstractProps.isEmpty()) return emptyList()

    val inheritors = symbol.getSealedClassInheritors(session, resolveSupertypes = resolveFocusTypes)

    if (!inheritors.allAreResolved) {
      check(!resolveFocusTypes) { "Supertypes must be resolved to compute focus types of $symbol" }

      // Supertypes aren't resolved yet, so we don't know which subclasses are real. Every abstract property
      // is then a candidate. Generation will run later and re-check with guaranteed resolved types.
      return abstractProps.map { prop ->
        FirFocus(
          kind = OpticKind.LENS,
          opticName = prop.name,
          focusType = null,
          componentName = prop.name,
        )
      }
    }

    val subclasses = inheritors.symbols

    if (subclasses.isEmpty() || subclasses.any { !it.isData }) return emptyList()

    return abstractProps.mapNotNull { prop ->
      val uniform = if (resolveFocusTypes) {
        val propType = prop.resolvedReturnType
        subclasses.all { sub ->
          val ctorParam = sub.primaryConstructorIfAny(session)?.valueParameterSymbols?.firstOrNull { it.name == prop.name }
          ctorParam != null && sameType(ctorParam.resolvedReturnType, propType)
        }
      } else {
        subclasses.all { sub ->
          val ctorParam = sub.primaryConstructorIfAny(session)?.valueParameterSymbols?.firstOrNull { it.name == prop.name }
          ctorParam != null
        }
      }
      if (!uniform) return@mapNotNull null
      FirFocus(
        kind = OpticKind.LENS,
        opticName = prop.name,
        focusType = if (resolveFocusTypes) prop.resolvedReturnType else null,
        componentName = prop.name,
        visibility = if (resolveFocusTypes) {
          // `set` rebuilds every subclass through its constructor and reads its properties
          memberVisibility(
            listOf(prop.visibility) + subclasses.flatMap { sub ->
              listOfNotNull(sub.primaryConstructorIfAny(session)?.visibility, sub.declaredProperty(prop.name)?.visibility) +
                classChainVisibilities(sub as FirRegularClassSymbol, session)
            },
          )
        } else {
          Visibilities.Public
        },
      )
    }
  }

  /**
   * Whether [this] property is abstract. Interface members without a body are implicitly abstract, but
   * their *raw* modality is `null` until status resolution, so we also recognise them syntactically:
   * this keeps the result independent of the resolution phase (review 2.4).
   */
  @OptIn(SymbolInternals::class)
  private fun FirPropertySymbol.isAbstractMemberOf(owner: FirRegularClassSymbol): Boolean = when (rawStatus.modality) {
    Modality.ABSTRACT -> true

    null ->
      owner.classKind == ClassKind.INTERFACE &&
        fir.initializer == null &&
        fir.delegate == null &&
        fir.getter?.body == null

    else -> false
  }

  @OptIn(DirectDeclarationsAccess::class)
  private fun FirClassSymbol<*>.declaredProperty(name: Name): FirPropertySymbol? = declarationSymbols.filterIsInstance<FirPropertySymbol>().firstOrNull { it.name == name }

  /**
   * Structural type equality for the §5.2 uniformity check: classifier, nullability, and (recursively)
   * the type arguments together with their projection kind, so e.g. `List<String>` and `List<Int>`
   * are *not* considered uniform.
   */
  private fun sameType(a: ConeKotlinType, b: ConeKotlinType): Boolean {
    if (a.classId != b.classId || a.isMarkedNullable != b.isMarkedNullable) return false
    val aArgs = a.typeArguments
    val bArgs = b.typeArguments
    if (aArgs.size != bArgs.size) return false
    return aArgs.indices.all { i ->
      val at = aArgs[i].type
      val bt = bArgs[i].type
      if (at == null || bt == null) {
        aArgs[i].kind == bArgs[i].kind // star projections
      } else {
        aArgs[i].kind == bArgs[i].kind && sameType(at, bt)
      }
    }
  }

  /**
   * The most-restrictive visibility of [symbol] and all of its enclosing classifiers (algo §3.3).
   * Used for the top-level DSL extensions.
   */
  fun effectiveVisibility(symbol: FirRegularClassSymbol, session: FirSession): Visibility = classChainVisibilities(symbol, session).fold(Visibilities.Public, ::mostRestrictive)

  /** The visibilities of [symbol] and all of its enclosing classifiers. */
  private fun classChainVisibilities(symbol: FirRegularClassSymbol, session: FirSession): List<Visibility> = buildList {
    add(symbol.visibility)
    var outerId = symbol.classId.outerClassId
    while (outerId != null) {
      val outer = session.symbolProvider.getClassLikeSymbolByClassId(outerId) as? FirRegularClassSymbol
      if (outer != null) add(outer.visibility)
      outerId = outerId.outerClassId
    }
  }

  /** One PRISM focus per direct sealed subclass (algo §6). */
  private fun prismFoci(symbol: FirRegularClassSymbol, session: FirSession, resolveFocusTypes: Boolean): List<FirFocus> {
    val inheritors = symbol.getSealedClassInheritors(session, resolveSupertypes = resolveFocusTypes)

    if (resolveFocusTypes) {
      check(inheritors.allAreResolved) { "Supertypes must be resolved to compute focus types of $symbol" }
    }

    return inheritors.symbols.map { sub -> prismFocus(symbol, sub, resolveFocusTypes, session) }
  }

  private fun prismFocus(symbol: FirRegularClassSymbol, sub: FirClassSymbol<*>, resolveFocusTypes: Boolean, session: FirSession): FirFocus = FirFocus(
    kind = OpticKind.PRISM,
    opticName = lowercaseFirst(sub.classId.shortClassName),
    focusType = if (resolveFocusTypes) sub.constructType(Array(sub.typeParameterSymbols.size) { ConeStarProjection }, false) else null,
    subclass = sub,
    // The subclass's (direct) supertype that mentions the sealed parent, e.g. `Parent<String, C>`.
    refinedSource = if (resolveFocusTypes) {
      sub.resolvedSuperTypes.map { it.fullyExpandedType(session) }.firstOrNull { it.classId == symbol.classId }
    } else {
      null
    },
    // the prism mentions the subclass in its type
    visibility = if (resolveFocusTypes && sub is FirRegularClassSymbol) memberVisibility(classChainVisibilities(sub, session)) else Visibilities.Public,
  )

  /** One focus per primary-constructor value parameter (LENS for data, ISO for value classes). */
  private fun constructorFoci(symbol: FirRegularClassSymbol, session: FirSession, kind: OpticKind, resolveFocusTypes: Boolean): List<FirFocus> {
    val ctor = symbol.primaryConstructorIfAny(session) ?: return emptyList()
    return ctor.valueParameterSymbols.map { param: FirValueParameterSymbol ->
      FirFocus(
        kind = kind,
        opticName = param.name,
        focusType = if (resolveFocusTypes) param.resolvedReturnType else null,
        componentName = param.name,
        // `get` reads the property, `set`/`reverseGet` call the constructor
        visibility = if (resolveFocusTypes) memberVisibility(listOfNotNull(ctor.visibility, symbol.declaredProperty(param.name)?.visibility)) else Visibilities.Public,
      )
    }
  }

  /**
   * The *direct* subclasses of this sealed class.
   *
   * If [resolveSupertypes] is `false`, we do not inspect supertypes, because we might be in an earlier phase where this
   * information is not available yet (and it is unsafe to do so). Instead, every class in the package is a candidate, and,
   * if there are any, the returned [SealedInheritors.allAreResolved] will be `false`. Attempting to resolve supertypes
   * _during_ the `SUPER_TYPES` phase could deadlock the IDE, for example.
   *
   * During generation, [resolveSupertypes] should be `true`. We then use the inheritors computed by the compiler itself
   * (phase `SEALED_CLASS_INHERITORS`), falling back to inspecting the direct supertypes of the classes in the package
   * if that phase has not been reached. The returned [SealedInheritors.allAreResolved] _should_ be `true`.
   */
  @OptIn(DirectDeclarationsAccess::class, SymbolInternals::class)
  private fun FirRegularClassSymbol.getSealedClassInheritors(session: FirSession, resolveSupertypes: Boolean): SealedInheritors {
    if (this.rawStatus.modality != Modality.SEALED) return SealedInheritors.Empty

    if (resolveSupertypes) {
      lazyResolveToPhase(FirResolvePhase.SEALED_CLASS_INHERITORS)
      if (fir.resolvePhase >= FirResolvePhase.SEALED_CLASS_INHERITORS) {
        val symbols = fir.getSealedClassInheritors(session).mapNotNullTo(linkedSetOf()) {
          session.symbolProvider.getClassLikeSymbolByClassId(it) as? FirClassSymbol<*>
        }
        return SealedInheritors(symbols, allAreResolved = true)
      }
    }

    val thePackage = this.packageFqName()
    val classNames = session.firProvider.getClassNamesInPackage(thePackage)

    val worklist = ArrayDeque(
      classNames.map { session.symbolProvider.getClassLikeSymbolByClassId(ClassId(thePackage, it)) },
    )

    var allAreResolved = true

    val symbols = buildSet {
      while (worklist.isNotEmpty()) {
        val current = worklist.removeFirst() as? FirClassSymbol<*> ?: continue

        if (current != this@getSealedClassInheritors) {
          if (resolveSupertypes) {
            current.lazyResolveToPhase(FirResolvePhase.SUPER_TYPES)

            if (current.fir.superTypeRefs.all { it is FirResolvedTypeRef }) {
              val direct = current.resolvedSuperTypes.any { it.fullyExpandedType(session).classId == this@getSealedClassInheritors.classId }
              if (direct) add(current)
            } else {
              allAreResolved = false
              add(current)
            }
          } else {
            allAreResolved = false
            add(current)
          }
        }

        for (symbol in current.declarationSymbols) {
          if (symbol is FirClassLikeSymbol<*>) worklist.add(symbol)
        }
      }
    }

    return SealedInheritors(symbols, allAreResolved)
  }
}

fun FirAnnotation.checkEvenIfUnresolved(classId: ClassId): Boolean = when (val ref = annotationTypeRef) {
  is FirResolvedTypeRef -> ref.coneType.classId == classId
  is FirUserTypeRef -> ref.isWrittenAs(classId)
  else -> false
}

/**
 * Whether [this] user type is written with the class names of [classId] (possibly qualified), as
 * opposed to an import alias or a type alias.
 */
fun FirUserTypeRef.isWrittenAs(classId: ClassId): Boolean {
  var current: ClassId? = classId
  var position = qualifier.size - 1
  while (current != null) {
    if (position < 0) return false
    if (qualifier[position].name != current.shortClassName) return false

    current = current.outerClassId
    position--
  }
  return true
}

/**
 * The subclasses of a sealed class. When [allAreResolved] is `false`, some supertypes weren't resolved
 * yet and [symbols] is a superset: it also has every class whose supertypes we couldn't check.
 */
private class SealedInheritors(val symbols: Set<FirClassSymbol<*>>, val allAreResolved: Boolean) {
  companion object {
    val Empty = SealedInheritors(emptySet(), allAreResolved = true)
  }
}
