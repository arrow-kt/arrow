package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticKind
import arrow.optics.plugin.OpticsNames
import arrow.optics.plugin.mostRestrictive
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
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
import org.jetbrains.kotlin.fir.resolve.substitution.substitutorByMap
import org.jetbrains.kotlin.fir.symbols.ConeTypeParameterLookupTag
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.fir.types.impl.ConeTypeParameterTypeImpl
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(DirectDeclarationsAccess::class, SymbolInternals::class, ExperimentalContracts::class)
class OpticsCompanionGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {
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

  override fun getNestedClassifiersNames(classSymbol: FirClassSymbol<*>, context: NestedClassGenerationContext): Set<Name> {
    if (classSymbol !is FirRegularClassSymbol) return emptySet()
    if (!session.predicateBasedProvider.matches(predicate, classSymbol)) return emptySet()
    return setOf(SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT)
  }

  override fun generateNestedClassLikeDeclaration(owner: FirClassSymbol<*>, name: Name, context: NestedClassGenerationContext): FirClassLikeSymbol<*>? {
    if (owner !is FirRegularClassSymbol) return null
    if (!session.predicateBasedProvider.matches(predicate, owner)) return null
    if (owner.companionObjectSymbol != null) return null
    if (name != SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT) return null
    return createCompanionObject(owner, Key) {
      this.visibility = owner.rawStatus.visibility
    }.symbol
  }

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
  private fun fociFor(companion: FirClassSymbol<*>): List<FirFocus> {
    val source = sourceClassOf(companion) ?: return emptyList()
    return FirOpticsExtractor.foci(source, session)
  }

  /** The `arrow.optics` poly-interface backing a focus of the given kind. */
  private fun polyClassOf(kind: OpticKind) = when (kind) {
    OpticKind.LENS -> OpticsNames.PLENS
    OpticKind.ISO -> OpticsNames.PISO
    OpticKind.PRISM -> OpticsNames.PPRISM
  }

  override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
    val names = fociFor(classSymbol).mapTo(mutableSetOf()) { it.opticName }
    if (classSymbol.isGeneratedOpticsCompanion()) names += SpecialNames.INIT
    return names
  }

  override fun generateProperties(callableId: CallableId, context: MemberGenerationContext?): List<FirPropertySymbol> {
    val owner = context?.owner ?: return emptyList()
    val source = sourceClassOf(owner) ?: return emptyList()
    if (source.typeParameterSymbols.isNotEmpty()) return emptyList() // generic -> function form
    val focus = fociFor(owner).firstOrNull { it.opticName == callableId.callableName } ?: return emptyList()
    val sourceType = source.constructType(emptyArray(), false)
    val opticType = polyClassOf(focus.kind).constructClassLikeType(
      arrayOf(sourceType, sourceType, focus.focusType, focus.focusType),
    )
    val vis = mostRestrictive(source.visibility, owner.visibility)
    val property = createMemberProperty(owner, Key, callableId.callableName, opticType, isVal = true, hasBackingField = false) {
      visibility = vis
    }
    return listOf(property.symbol)
  }

  override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
    val owner = context?.owner ?: return emptyList()
    val source = sourceClassOf(owner) ?: return emptyList()
    if (source.typeParameterSymbols.isEmpty()) return emptyList() // monomorphic -> property form
    val focus = fociFor(owner).firstOrNull { it.opticName == callableId.callableName } ?: return emptyList()
    val vis = mostRestrictive(source.visibility, owner.visibility)

    // A PRISM on a generic parent quantifies over the *subclass's* type parameters and uses the
    // subclass's refined supertype as its source (algo §6). Lenses/isos mirror the parent's parameters.
    val function = if (focus.kind == OpticKind.PRISM) {
      val subParams = focus.subclass?.typeParameterSymbols.orEmpty()
      createMemberFunction(
        owner, Key, callableId.callableName,
        returnTypeProvider = { functionTypeParameters ->
          val funCones = functionTypeParameters.coneTypes()
          val substitutor = substitutorByMap(subParams.zip(funCones).toMap(), session)
          val sourceType = focus.refinedSource?.let { substitutor.substituteOrSelf(it) }
            ?: source.constructType(emptyArray(), false)
          val focusType = focus.subclass?.constructType(funCones.toTypedArray(), false) ?: focus.focusType
          polyClassOf(focus.kind).constructClassLikeType(
            arrayOf(sourceType, sourceType, focusType, focusType),
          )
        },
      ) {
        subParams.forEach { tp -> typeParameter(tp.name) }
        visibility = vis
      }
    } else {
      val sourceTypeParams = source.typeParameterSymbols
      createMemberFunction(
        owner, Key, callableId.callableName,
        returnTypeProvider = { functionTypeParameters ->
          val funCones = functionTypeParameters.coneTypes()
          val substitutor = substitutorByMap(sourceTypeParams.zip(funCones).toMap(), session)
          val substFocus = substitutor.substituteOrSelf(focus.focusType)
          val sourceType = source.constructType(funCones.toTypedArray(), false)
          polyClassOf(focus.kind).constructClassLikeType(
            arrayOf(sourceType, sourceType, substFocus, substFocus),
          )
        },
      ) {
        sourceTypeParams.forEach { tp -> typeParameter(tp.name) }
        visibility = vis
      }
    }
    return listOf(function.symbol)
  }

  private fun List<org.jetbrains.kotlin.fir.declarations.FirTypeParameterRef>.coneTypes(): List<ConeKotlinType> =
    map { ConeTypeParameterTypeImpl(ConeTypeParameterLookupTag(it.symbol), isMarkedNullable = false) }

  override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
    val owner = context.owner
    if (!owner.isGeneratedOpticsCompanion()) return emptyList()
    return listOf(createDefaultPrivateConstructor(owner, Key).symbol)
  }

  object Key : GeneratedDeclarationKey()
}
