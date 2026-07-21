package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticKind
import arrow.optics.plugin.OpticsClassKind
import arrow.optics.plugin.OpticsNames
import arrow.optics.plugin.dslVariantsFor
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirTypeParameterRef
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createTopLevelFunction
import org.jetbrains.kotlin.fir.plugin.createTopLevelProperty
import org.jetbrains.kotlin.fir.resolve.substitution.ConeSubstitutor
import org.jetbrains.kotlin.fir.resolve.substitution.substitutorByMap
import org.jetbrains.kotlin.fir.symbols.ConeTypeParameterLookupTag
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.fir.types.impl.ConeTypeParameterTypeImpl
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Generates the DSL composition extensions (algo section 8) as top-level extensions:
 * `val <__S> OuterOptic<__S, Source>.focus: OuterOptic<__S, Focus> get() = this + Source.focus`
 * for monomorphic sources, and the analogous *function* form for generic sources:
 * `fun <__S, A, B> OuterOptic<__S, Source<A, B>>.focus(): OuterOptic<__S, Focus> = this + Source.focus()`.
 *
 * These cannot be companion members (their receiver is an arbitrary outer optic), so they remain
 * top-level extensions.
 */
@OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
class OpticsDslGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {
  object Key : GeneratedDeclarationKey()

  override fun FirDeclarationPredicateRegistrar.registerPredicates() {
    register(OpticsPredicates.optics, OpticsPredicates.opticsLookup)
  }

  /** `@optics`-annotated source classes for which the DSL target is enabled. */
  private fun annotatedSources(): List<FirRegularClassSymbol> = session.predicateBasedProvider.getSymbolsByPredicate(OpticsPredicates.opticsLookup)
    .filterIsInstance<FirRegularClassSymbol>()
    .filter { FirOpticsExtractor.dslEnabled(it) }

  /**
   * The foci that get DSL composition helpers. Per algo section 8.4 a sealed type contributes only its
   * prism family - its shared-property lenses (section 5.2) do *not* get DSL variants.
   */
  private fun dslFoci(source: FirRegularClassSymbol, resolveFocusTypes: Boolean): List<FirFocus> {
    val isSealed = FirOpticsExtractor.classKind(source) == OpticsClassKind.SEALED
    return FirOpticsExtractor.foci(source, resolveFocusTypes, session)
      .filter { !(isSealed && it.kind == OpticKind.LENS) }
  }

  override fun getTopLevelCallableIds(): Set<CallableId> = buildSet {
    annotatedSources().forEach { source ->
      val pkg = source.classId.packageFqName
      dslFoci(source, resolveFocusTypes = false).forEach { add(CallableId(pkg, it.opticName)) }
    }
  }

  override fun hasPackage(packageFqName: FqName): Boolean = annotatedSources().any { it.classId.packageFqName == packageFqName }

  override fun generateProperties(callableId: CallableId, context: MemberGenerationContext?): List<FirPropertySymbol> {
    if (context != null) return emptyList() // only top-level
    val result = mutableListOf<FirPropertySymbol>()
    annotatedSources().forEach { source ->
      if (source.typeParameterSymbols.isNotEmpty()) return@forEach // generic -> function form
      if (source.classId.packageFqName != callableId.packageName) return@forEach
      val sourceType = source.constructType(emptyArray(), false)
      val fileName = "${source.classId.shortClassName.asString()}Optics"
      dslFoci(source, resolveFocusTypes = true)
        .filter { it.opticName == callableId.callableName }
        .forEach { focus ->
          requireNotNull(focus.focusType) { "focusType must be resolved at this stage" }
          for (dslKind in dslVariantsFor(focus.kind)) {
            val property = createTopLevelProperty(
              key = Key,
              callableId = callableId,
              returnTypeProvider = { tps ->
                val s = coneOf(tps[0])
                OpticsNames.polyClassFor(dslKind).constructClassLikeType(arrayOf(s, s, focus.focusType, focus.focusType))
              },
              isVal = true,
              hasBackingField = false,
              containingFileName = fileName,
            ) {
              typeParameter(Name.identifier("__S"))
              extensionReceiverType { tps ->
                val s = coneOf(tps[0])
                OpticsNames.polyClassFor(dslKind).constructClassLikeType(arrayOf(s, s, sourceType, sourceType))
              }
              visibility = FirOpticsExtractor.effectiveVisibility(source, session)
            }
            result += property.symbol
          }
        }
    }
    return result
  }

  override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
    if (context != null) return emptyList() // only top-level
    val result = mutableListOf<FirNamedFunctionSymbol>()
    annotatedSources().forEach { source ->
      if (source.typeParameterSymbols.isEmpty()) return@forEach // monomorphic -> property form
      if (source.classId.packageFqName != callableId.packageName) return@forEach
      val fileName = "${source.classId.shortClassName.asString()}Optics"
      dslFoci(source, resolveFocusTypes = true)
        .filter { it.opticName == callableId.callableName }
        .forEach { focus ->
          // A prism on a generic parent quantifies over the *subclass's* type parameters and uses the
          // subclass's refined supertype as its source (algo section 6); lenses/isos mirror the parent's.
          val declaredParams: List<FirTypeParameterSymbol> =
            if (focus.kind == OpticKind.PRISM) focus.subclass?.typeParameterSymbols.orEmpty() else source.typeParameterSymbols
          for (dslKind in dslVariantsFor(focus.kind)) {
            val function = createTopLevelFunction(
              key = Key,
              callableId = callableId,
              returnTypeProvider = { tps ->
                val (_, focusType) = dslSourceAndFocus(source, focus, declaredParams, tps)
                val s = coneOf(tps.last())
                OpticsNames.polyClassFor(dslKind).constructClassLikeType(arrayOf(s, s, focusType, focusType))
              },
              containingFileName = fileName,
            ) {
              declaredParams.forEach { typeParameter(it.name) }
              typeParameter(Name.identifier("__S"))
              extensionReceiverType { tps ->
                val (sourceType, _) = dslSourceAndFocus(source, focus, declaredParams, tps)
                val s = coneOf(tps.last())
                OpticsNames.polyClassFor(dslKind).constructClassLikeType(arrayOf(s, s, sourceType, sourceType))
              }
              visibility = FirOpticsExtractor.effectiveVisibility(source, session)
            }
            result += function.symbol
          }
        }
    }
    return result
  }

  /**
   * The `(sourceType, focusType)` of a generic DSL extension, expressed in terms of the function's
   * own type parameters. [tps] is the full type-parameter list `[<declaredParams>, __S]`.
   */
  private fun dslSourceAndFocus(
    source: FirRegularClassSymbol,
    focus: FirFocus,
    declaredParams: List<FirTypeParameterSymbol>,
    tps: List<FirTypeParameterRef>,
  ): Pair<ConeKotlinType, ConeKotlinType> {
    val funCones = tps.dropLast(1).map { coneOf(it) }
    val substitutor: ConeSubstitutor = substitutorByMap(declaredParams.zip(funCones).toMap(), session)
    return if (focus.kind == OpticKind.PRISM) {
      val sourceType = focus.refinedSource?.let { substitutor.substituteOrSelf(it) }
        ?: source.constructType(emptyArray(), false)
      val focusType = focus.subclass?.constructType(funCones.toTypedArray(), false)
        ?: requireNotNull(focus.focusType) { "focusType must be resolved at this stage" }
      sourceType to focusType
    } else {
      requireNotNull(focus.focusType) { "focusType must be resolved at this stage" }
      source.constructType(funCones.toTypedArray(), false) to substitutor.substituteOrSelf(focus.focusType)
    }
  }

  private fun coneOf(tp: FirTypeParameterRef): ConeKotlinType = ConeTypeParameterTypeImpl(ConeTypeParameterLookupTag(tp.symbol), isMarkedNullable = false)
}
