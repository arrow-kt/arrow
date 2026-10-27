package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticKind
import arrow.optics.plugin.OpticsClassKind
import arrow.optics.plugin.OpticsNames
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.fir.FirImplementationDetail
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.caches.createCache
import org.jetbrains.kotlin.fir.caches.firCachesFactory
import org.jetbrains.kotlin.fir.caches.getValue
import org.jetbrains.kotlin.fir.declarations.FirTypeParameterRef
import org.jetbrains.kotlin.fir.declarations.utils.hasBackingFieldAttr
import org.jetbrains.kotlin.fir.declarations.utils.isCompanion
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.impl.FirEmptyAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.NestedClassGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.DeclarationBuildingContext
import org.jetbrains.kotlin.fir.plugin.createCompanionObject
import org.jetbrains.kotlin.fir.plugin.createDefaultPrivateConstructor
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.plugin.createMemberProperty
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.substitution.ConeSubstitutor
import org.jetbrains.kotlin.fir.resolve.substitution.substitutorByMap
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.fir.types.isNullableAny
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class OpticsCompanionGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {
  /** Key of the generated companion object (and its constructor). */
  object Key : GeneratedDeclarationKey()

  /** Keys of the generated base optics, one per kind, so that the IR phase knows which body to build. */
  object LensKey : GeneratedDeclarationKey()
  object IsoKey : GeneratedDeclarationKey()
  object PrismKey : GeneratedDeclarationKey()

  companion object {
    fun keyFor(kind: OpticKind): GeneratedDeclarationKey = when (kind) {
      OpticKind.LENS -> LensKey
      OpticKind.ISO -> IsoKey
      OpticKind.PRISM -> PrismKey
    }

    val opticKeys: Set<GeneratedDeclarationKey> = setOf(LensKey, IsoKey, PrismKey)
  }

  override fun FirDeclarationPredicateRegistrar.registerPredicates() {
    register(OpticsPredicates.optics)
  }

  private val runtimeAvailable by lazy { FirOpticsExtractor.runtimeAvailable(session) }

  private val resolvedFoci = session.firCachesFactory.createCache { source: FirRegularClassSymbol ->
    FirOpticsExtractor.foci(source, resolveFocusTypes = true, session)
  }

  // ---- companion object creation (for classes that lack one) -------------------------

  // NOTE: the companion-object phase runs before declaration *status* is resolved, so eligibility
  // only inspects flags available in the raw FIR (see `FirOpticsExtractor.classKind`). Ineligible
  // classes get no companion; `OpticsChecker` reports them.

  @OptIn(SymbolInternals::class)
  fun FirRegularClassSymbol.matches() = session.predicateBasedProvider.matches(OpticsPredicates.optics, this) &&
    runtimeAvailable &&
    FirOpticsExtractor.classKind(this) != OpticsClassKind.INELIGIBLE &&
    (this.annotations.none { it.checkEvenIfUnresolved(OpticsNames.SERIALIZABLE_ANNOTATION) } || this.companionObjectSymbol != null)

  override fun getNestedClassifiersNames(classSymbol: FirClassSymbol<*>, context: NestedClassGenerationContext): Set<Name> {
    if (classSymbol !is FirRegularClassSymbol) return emptySet()
    if (!classSymbol.matches()) return emptySet()
    return setOf(SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT)
  }

  @OptIn(SymbolInternals::class)
  override fun generateNestedClassLikeDeclaration(owner: FirClassSymbol<*>, name: Name, context: NestedClassGenerationContext): FirClassLikeSymbol<*>? {
    if (owner !is FirRegularClassSymbol) return null
    if (!owner.matches()) return null
    if (owner.companionObjectSymbol != null) return null
    if (name != SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT) return null
    // The companion is public: the source class's own visibility already restricts access to it,
    // whereas a `private` companion of a `private` class would not even be visible in its file.
    return createCompanionObject(owner, Key).symbol
  }

  @OptIn(ExperimentalContracts::class)
  fun FirClassSymbol<*>.isGeneratedOpticsCompanion(): Boolean {
    contract {
      returns(true) implies (this@isGeneratedOpticsCompanion is FirRegularClassSymbol)
    }
    return isCompanion && this is FirRegularClassSymbol && keyOf(origin) == Key
  }

  // ---- base optic member generation --------------------------------------------------

  /** The `@optics`-annotated source class enclosing [companion], if eligible. */
  private fun sourceClassOf(companion: FirClassSymbol<*>): FirRegularClassSymbol? {
    if (!companion.isCompanion) return null
    val outerId = companion.classId.outerClassId ?: return null
    val source = session.symbolProvider.getClassLikeSymbolByClassId(outerId) as? FirRegularClassSymbol ?: return null
    if (!session.predicateBasedProvider.matches(OpticsPredicates.optics, source)) return null
    if (!runtimeAvailable) return null
    return source
  }

  /** The single focus named [name] to generate as a member of [companion], if any. */
  private fun focusFor(companion: FirClassSymbol<*>, source: FirRegularClassSymbol, name: Name): FirFocus? {
    if (name in FirOpticsExtractor.userMemberNames(companion)) return null
    return resolvedFoci.getValue(source).singleOrNull { it.opticName == name }
  }

  override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
    val names = sourceClassOf(classSymbol)?.let { source ->
      FirOpticsExtractor.foci(source, resolveFocusTypes = false, session).mapTo(mutableSetOf()) { it.opticName } - FirOpticsExtractor.userMemberNames(classSymbol)
    }.orEmpty().toMutableSet()
    if (classSymbol.isGeneratedOpticsCompanion()) names += SpecialNames.INIT
    return names
  }

  @OptIn(FirImplementationDetail::class)
  override fun generateProperties(callableId: CallableId, context: MemberGenerationContext?): List<FirPropertySymbol> {
    val owner = context?.owner ?: return emptyList()
    val source = sourceClassOf(owner) ?: return emptyList()
    if (source.typeParameterSymbols.isNotEmpty()) return emptyList() // generic -> function form
    val focus = focusFor(owner, source, callableId.callableName) ?: return emptyList()
    val sourceType = source.constructType(emptyArray(), false)
    requireNotNull(focus.focusType) { "focusType must be resolved at this stage" }
    val opticType = OpticsNames.monoClassOf(focus.kind).constructClassLikeType(arrayOf(sourceType, focus.focusType))
    val property = createMemberProperty(owner, keyFor(focus.kind), callableId.callableName, opticType, isVal = true, hasBackingField = false) {
      visibility = focus.visibility
    }
    // `hasBackingField = false` does not reach Fir2Ir for a body-less default getter, which would
    // otherwise create a backing field for the property (review 1.2).
    property.hasBackingFieldAttr = false
    return listOf(property.symbol)
  }

  override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
    val owner = context?.owner ?: return emptyList()
    val source = sourceClassOf(owner) ?: return emptyList()
    if (source.typeParameterSymbols.isEmpty()) return emptyList() // monomorphic -> property form
    val focus = focusFor(owner, source, callableId.callableName) ?: return emptyList()

    // A PRISM on a generic parent quantifies over the *subclass's* type parameters and uses the
    // subclass's refined supertype as its source (algo §6). Lenses/isos mirror the parent's parameters.
    val function = if (focus.kind == OpticKind.PRISM) {
      opticFunction(owner, callableId, focus.kind, focus.visibility, focus.subclass?.typeParameterSymbols.orEmpty()) { substitutor, funCones ->
        val sourceType = focus.refinedSource?.let { substitutor.substituteOrSelf(it) }
          ?: error("Cannot find the supertype ${source.classId} of ${focus.subclass?.classId}")
        val focusType = focus.subclass?.constructType(funCones.toTypedArray(), false) ?: focus.focusType
        requireNotNull(focusType) { "focusType must be resolved at this stage" }
        sourceType to focusType
      }
    } else {
      opticFunction(owner, callableId, focus.kind, focus.visibility, source.typeParameterSymbols) { substitutor, funCones ->
        requireNotNull(focus.focusType) { "focusType must be resolved at this stage" }
        source.constructType(funCones.toTypedArray(), false) to substitutor.substituteOrSelf(focus.focusType)
      }
    }
    if (focus.potentiallyUnsafe) {
      function.replaceAnnotations(
        function.annotations + buildAnnotation {
          annotationTypeRef = OpticsNames.POTENTIALLY_UNSAFE_COVARIANT_OPTIC.constructClassLikeType().toFirResolvedTypeRef()
          argumentMapping = FirEmptyAnnotationArgumentMapping
        },
      )
    }
    return listOf(function.symbol)
  }

  /**
   * Build a generic base-optic function quantified over [typeParams]. [sourceAndFocus] receives a
   * substitutor mapping the declared parameters to the function's freshly-introduced ones plus those
   * fresh cone types, and returns the `(source, focus)` pair used as `Poly<source, source, focus, focus>`.
   */
  private fun opticFunction(
    owner: FirClassSymbol<*>,
    callableId: CallableId,
    kind: OpticKind,
    vis: Visibility,
    typeParams: List<FirTypeParameterSymbol>,
    sourceAndFocus: (ConeSubstitutor, List<ConeKotlinType>) -> Pair<ConeKotlinType, ConeKotlinType>,
  ) = createMemberFunction(
    owner,
    keyFor(kind),
    callableId.callableName,
    returnTypeProvider = { functionTypeParameters ->
      val funCones = functionTypeParameters.coneTypes()
      val substitutor = substitutorByMap(typeParams.zip(funCones).toMap(), session)
      val (sourceType, focusType) = sourceAndFocus(substitutor, funCones)
      OpticsNames.monoClassOf(kind).constructClassLikeType(arrayOf(sourceType, focusType))
    },
  ) {
    copyTypeParameters(typeParams, session)
    visibility = vis
  }

  private fun List<FirTypeParameterRef>.coneTypes(): List<ConeKotlinType> = map { coneOf(it) }

  override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
    val owner = context.owner
    if (!owner.isGeneratedOpticsCompanion()) return emptyList()
    return listOf(createDefaultPrivateConstructor(owner, Key).symbol)
  }
}

/** The cone type referring to the type parameter [tp]. */
fun coneOf(tp: FirTypeParameterRef): ConeKotlinType = tp.symbol.constructType()

/**
 * Declare a type parameter for each of [typeParams], in order, keeping their upper bounds (algo §3.5).
 * Bounds may mention any of the parameters, so they are expressed in terms of the declaration's own
 * type parameters (the first `typeParams.size` ones).
 */
fun DeclarationBuildingContext<*>.copyTypeParameters(typeParams: List<FirTypeParameterSymbol>, session: FirSession) {
  typeParams.forEach { tp ->
    typeParameter(tp.name) {
      tp.resolvedBounds.map { it.coneType }.filterNot { it.isNullableAny }.forEach { declaredBound ->
        bound { tps ->
          val substitutor = substitutorByMap(typeParams.zip(tps.take(typeParams.size).map { coneOf(it) }).toMap(), session)
          substitutor.substituteOrSelf(declaredBound)
        }
      }
    }
  }
}
