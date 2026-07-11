package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticKind
import arrow.optics.plugin.OpticsNames
import arrow.optics.plugin.mostRestrictive
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirTypeParameterRef
import org.jetbrains.kotlin.fir.declarations.utils.isCompanion
import org.jetbrains.kotlin.fir.declarations.utils.visibility
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.NestedClassGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createCompanionObject
import org.jetbrains.kotlin.fir.plugin.createDefaultPrivateConstructor
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.plugin.createMemberProperty
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.substitution.ConeSubstitutor
import org.jetbrains.kotlin.fir.resolve.substitution.substitutorByMap
import org.jetbrains.kotlin.fir.symbols.ConeTypeParameterLookupTag
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.fir.types.impl.ConeTypeParameterTypeImpl
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class OpticsCompanionGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {
  object Key : GeneratedDeclarationKey()

  companion object {
    val OPTICS_ANNOTATION_FQNAME = OpticsNames.OPTICS_ANNOTATION_FQNAME

    val predicate = DeclarationPredicate.create {
      annotated(setOf(OPTICS_ANNOTATION_FQNAME))
    }
  }

  override fun FirDeclarationPredicateRegistrar.registerPredicates() {
    register(predicate)
  }

  // ---- companion object creation (for classes that lack one) -------------------------

  // NOTE: the companion-object phase runs before declaration *status* is resolved, so eligibility
  // (which inspects `modality`/`isSealed`) cannot be checked here without crashing the compiler.
  // Ineligible classes are therefore filtered later, during member generation (`foci` returns none),
  // so they simply receive no optics — see `TargetTests."ineligible class generates no optics"`.

  override fun getNestedClassifiersNames(classSymbol: FirClassSymbol<*>, context: NestedClassGenerationContext): Set<Name> {
    if (classSymbol !is FirRegularClassSymbol) return emptySet()
    if (!session.predicateBasedProvider.matches(predicate, classSymbol)) return emptySet()
    return setOf(SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT)
  }

  @OptIn(SymbolInternals::class)
  override fun generateNestedClassLikeDeclaration(owner: FirClassSymbol<*>, name: Name, context: NestedClassGenerationContext): FirClassLikeSymbol<*>? {
    if (owner !is FirRegularClassSymbol) return null
    if (!session.predicateBasedProvider.matches(predicate, owner)) return null
    if (owner.companionObjectSymbol != null) return null
    if (name != SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT) return null
    return createCompanionObject(owner, Key) {
      this.visibility = owner.rawStatus.visibility
    }.symbol
  }

  @OptIn(ExperimentalContracts::class)
  fun FirClassSymbol<*>.isGeneratedOpticsCompanion(): Boolean {
    contract {
      returns(true) implies (this@isGeneratedOpticsCompanion is FirRegularClassSymbol)
    }
    return isCompanion && this is FirRegularClassSymbol && (origin as? FirDeclarationOrigin.Plugin)?.key == Key
  }

  // ---- base optic member generation --------------------------------------------------

  /** The `@optics`-annotated source class enclosing [companion], if eligible. */
  private fun sourceClassOf(companion: FirClassSymbol<*>): FirRegularClassSymbol? {
    if (!companion.isCompanion) return null
    val outerId = companion.classId.outerClassId ?: return null
    val source = session.symbolProvider.getClassLikeSymbolByClassId(outerId) as? FirRegularClassSymbol ?: return null
    if (!session.predicateBasedProvider.matches(predicate, source)) return null
    return source
  }

  /** Base optic foci to generate as members of [companion]. */
  private fun fociFor(companion: FirClassSymbol<*>, resolveFocusTypes: Boolean): List<FirFocus> {
    val source = sourceClassOf(companion) ?: return emptyList()
    return FirOpticsExtractor.foci(source, resolveFocusTypes, session)
  }

  override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
    val names = fociFor(classSymbol, resolveFocusTypes = false).mapTo(mutableSetOf()) { it.opticName }
    if (classSymbol.isGeneratedOpticsCompanion()) names += SpecialNames.INIT
    return names
  }

  override fun generateProperties(callableId: CallableId, context: MemberGenerationContext?): List<FirPropertySymbol> {
    val owner = context?.owner ?: return emptyList()
    val source = sourceClassOf(owner) ?: return emptyList()
    if (source.typeParameterSymbols.isNotEmpty()) return emptyList() // generic -> function form
    val focus = fociFor(owner, resolveFocusTypes = true).firstOrNull { it.opticName == callableId.callableName } ?: return emptyList()
    val sourceType = source.constructType(emptyArray(), false)
    requireNotNull(focus.focusType) { "focusType must be resolved at this stage" }
    val opticType = OpticsNames.monoClassOf(focus.kind).constructClassLikeType(arrayOf(sourceType, focus.focusType))
    val vis = mostRestrictive(FirOpticsExtractor.effectiveVisibility(source, session), owner.visibility)
    val property = createMemberProperty(owner, Key, callableId.callableName, opticType, isVal = true, hasBackingField = false) {
      visibility = vis
    }
    return listOf(property.symbol)
  }

  override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
    val owner = context?.owner ?: return emptyList()
    val source = sourceClassOf(owner) ?: return emptyList()
    if (source.typeParameterSymbols.isEmpty()) return emptyList() // monomorphic -> property form
    val focus = fociFor(owner, resolveFocusTypes = true).firstOrNull { it.opticName == callableId.callableName } ?: return emptyList()
    val vis = mostRestrictive(FirOpticsExtractor.effectiveVisibility(source, session), owner.visibility)

    // A PRISM on a generic parent quantifies over the *subclass's* type parameters and uses the
    // subclass's refined supertype as its source (algo §6). Lenses/isos mirror the parent's parameters.
    val function = if (focus.kind == OpticKind.PRISM) {
      opticFunction(owner, callableId, focus.kind, vis, focus.subclass?.typeParameterSymbols.orEmpty()) { substitutor, funCones ->
        val sourceType = focus.refinedSource?.let { substitutor.substituteOrSelf(it) }
          ?: source.constructType(emptyArray(), false)
        val focusType = focus.subclass?.constructType(funCones.toTypedArray(), false) ?: focus.focusType
        requireNotNull(focusType) { "focusType must be resolved at this stage" }
        sourceType to focusType
      }
    } else {
      opticFunction(owner, callableId, focus.kind, vis, source.typeParameterSymbols) { substitutor, funCones ->
        requireNotNull(focus.focusType) { "focusType must be resolved at this stage" }
        source.constructType(funCones.toTypedArray(), false) to substitutor.substituteOrSelf(focus.focusType)
      }
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
    Key,
    callableId.callableName,
    returnTypeProvider = { functionTypeParameters ->
      val funCones = functionTypeParameters.coneTypes()
      val substitutor = substitutorByMap(typeParams.zip(funCones).toMap(), session)
      val (sourceType, focusType) = sourceAndFocus(substitutor, funCones)
      OpticsNames.monoClassOf(kind).constructClassLikeType(arrayOf(sourceType, focusType))
    },
  ) {
    typeParams.forEach { tp -> typeParameter(tp.name) }
    visibility = vis
  }

  private fun List<FirTypeParameterRef>.coneTypes(): List<ConeKotlinType> = map { ConeTypeParameterTypeImpl(ConeTypeParameterLookupTag(it.symbol), isMarkedNullable = false) }

  override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
    val owner = context.owner
    if (!owner.isGeneratedOpticsCompanion()) return emptyList()
    return listOf(createDefaultPrivateConstructor(owner, Key).symbol)
  }
}
