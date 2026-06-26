package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticsNames
import arrow.optics.plugin.dslVariantsFor
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.utils.visibility
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createTopLevelProperty
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.fir.types.impl.ConeTypeParameterTypeImpl
import org.jetbrains.kotlin.fir.symbols.ConeTypeParameterLookupTag
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
class OpticsDslGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {

  private val lookupPredicate = LookupPredicate.create {
    annotated(setOf(OpticsNames.OPTICS_ANNOTATION_FQNAME))
  }
  private val declarationPredicate = DeclarationPredicate.create {
    annotated(setOf(OpticsNames.OPTICS_ANNOTATION_FQNAME))
  }

  override fun FirDeclarationPredicateRegistrar.registerPredicates() {
    register(declarationPredicate)
  }

  private val DSL_S = Name.identifier("__S")

  /** Monomorphic `@optics`-annotated source classes for which the DSL target is enabled. */
  private fun annotatedSources(): List<FirRegularClassSymbol> =
    session.predicateBasedProvider.getSymbolsByPredicate(lookupPredicate)
      .filterIsInstance<FirRegularClassSymbol>()
      .filter { it.typeParameterSymbols.isEmpty() && FirOpticsExtractor.dslEnabled(it, session) }

  override fun getTopLevelCallableIds(): Set<CallableId> = buildSet {
    annotatedSources().forEach { source ->
      val pkg = source.classId.packageFqName
      FirOpticsExtractor.foci(source, session).forEach { add(CallableId(pkg, it.opticName)) }
    }
  }

  override fun hasPackage(packageFqName: FqName): Boolean =
    annotatedSources().any { it.classId.packageFqName == packageFqName }

  override fun generateProperties(callableId: CallableId, context: MemberGenerationContext?): List<FirPropertySymbol> {
    if (context != null) return emptyList() // only top-level
    val result = mutableListOf<FirPropertySymbol>()
    annotatedSources().forEach { source ->
      if (source.classId.packageFqName != callableId.packageName) return@forEach
      val sourceType = source.constructType(emptyArray(), false)
      val fileName = "${source.classId.shortClassName.asString()}Optics"
      FirOpticsExtractor.foci(source, session)
        .filter { it.opticName == callableId.callableName }
        .forEach { focus ->
          dslVariantsFor(focus.kind).forEach { dslKind ->
            val poly = OpticsNames.polyClassFor(dslKind)
            val property = createTopLevelProperty(
              Key,
              callableId,
              returnTypeProvider = { tps ->
                val s = sCone(tps)
                poly.constructClassLikeType(arrayOf(s, s, focus.focusType, focus.focusType))
              },
              isVal = true,
              hasBackingField = false,
              containingFileName = fileName,
            ) {
              typeParameter(DSL_S)
              extensionReceiverType { tps ->
                val s = sCone(tps)
                poly.constructClassLikeType(arrayOf(s, s, sourceType, sourceType))
              }
              visibility = source.visibility
            }
            result += property.symbol
          }
        }
    }
    return result
  }

  private fun sCone(tps: List<org.jetbrains.kotlin.fir.declarations.FirTypeParameterRef>): ConeKotlinType =
    ConeTypeParameterTypeImpl(ConeTypeParameterLookupTag(tps[0].symbol), isMarkedNullable = false)

  object Key : GeneratedDeclarationKey()
}
