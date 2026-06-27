package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticKind
import arrow.optics.plugin.OpticsClassKind
import arrow.optics.plugin.OpticsNames
import arrow.optics.plugin.dslVariantsFor
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createTopLevelProperty
import org.jetbrains.kotlin.fir.symbols.ConeTypeParameterLookupTag
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.fir.types.impl.ConeTypeParameterTypeImpl
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Generates the DSL composition extensions (algo §8) as top-level extension properties:
 * `val <__S> OuterOptic<__S, Source>.focus: OuterOptic<__S, Focus> get() = this + Source.focus`.
 *
 * These cannot be companion members (their receiver is an arbitrary outer optic), so they remain
 * top-level extensions. Only monomorphic sources are supported for now.
 */
@OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
class OpticsDslGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {
  object Key : GeneratedDeclarationKey()

  private val lookupPredicate = LookupPredicate.create {
    annotated(setOf(OpticsNames.OPTICS_ANNOTATION_FQNAME))
  }
  private val declarationPredicate = DeclarationPredicate.create {
    annotated(setOf(OpticsNames.OPTICS_ANNOTATION_FQNAME))
  }

  override fun FirDeclarationPredicateRegistrar.registerPredicates() {
    register(declarationPredicate)
  }

  /** Monomorphic `@optics`-annotated source classes for which the DSL target is enabled. */
  private fun annotatedSources(): List<FirRegularClassSymbol> = session.predicateBasedProvider.getSymbolsByPredicate(lookupPredicate)
    .filterIsInstance<FirRegularClassSymbol>()
    .filter { it.typeParameterSymbols.isEmpty() && FirOpticsExtractor.dslEnabled(it, session) }

  /**
   * The foci that get DSL composition helpers. Per algo §8.4 a sealed type contributes only its
   * prism family — its shared-property lenses (§5.2) do *not* get DSL variants.
   */
  private fun dslFoci(source: FirRegularClassSymbol): List<FirFocus> {
    val isSealed = FirOpticsExtractor.classKind(source) == OpticsClassKind.SEALED
    return FirOpticsExtractor.foci(source, session)
      .filter { !(isSealed && it.kind == OpticKind.LENS) }
  }

  override fun getTopLevelCallableIds(): Set<CallableId> = buildSet {
    annotatedSources().forEach { source ->
      val pkg = source.classId.packageFqName
      dslFoci(source).forEach { add(CallableId(pkg, it.opticName)) }
    }
  }

  override fun hasPackage(packageFqName: FqName): Boolean = annotatedSources().any { it.classId.packageFqName == packageFqName }

  override fun generateProperties(callableId: CallableId, context: MemberGenerationContext?): List<FirPropertySymbol> {
    if (context != null) return emptyList() // only top-level
    val result = mutableListOf<FirPropertySymbol>()
    annotatedSources().forEach { source ->
      if (source.classId.packageFqName != callableId.packageName) return@forEach
      val sourceType = source.constructType(emptyArray(), false)
      val fileName = "${source.classId.shortClassName.asString()}Optics"
      dslFoci(source)
        .filter { it.opticName == callableId.callableName }
        .forEach { focus ->
          dslVariantsFor(focus.kind).forEach { dslKind ->
            val poly = OpticsNames.polyClassFor(dslKind)
            val property = createTopLevelProperty(
              key = Key,
              callableId = callableId,
              returnTypeProvider = { tps ->
                val s = ConeTypeParameterTypeImpl(ConeTypeParameterLookupTag(tps[0].symbol), isMarkedNullable = false)
                poly.constructClassLikeType(arrayOf(s, s, focus.focusType, focus.focusType))
              },
              isVal = true,
              hasBackingField = false,
              containingFileName = fileName,
            ) {
              typeParameter(Name.identifier("__S"))
              extensionReceiverType { tps ->
                val s = ConeTypeParameterTypeImpl(ConeTypeParameterLookupTag(tps[0].symbol), isMarkedNullable = false)
                poly.constructClassLikeType(arrayOf(s, s, sourceType, sourceType))
              }
              visibility = FirOpticsExtractor.effectiveVisibility(source, session)
            }
            result += property.symbol
          }
        }
    }
    return result
  }
}
