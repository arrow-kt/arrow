package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticsNames
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.CompilerConeAttributes
import org.jetbrains.kotlin.fir.types.ConeAttributes
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Generates the `@optics.copy` builder (algo §9) as a **member** of the source class:
 * `fun copy(block: context(Copy<Source>) Source.Companion.(Source) -> Unit): Source`.
 * Only monomorphic sources are supported for now.
 */
class OpticsCopyGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {
  object Key : GeneratedDeclarationKey()

  private val declarationPredicate = DeclarationPredicate.create {
    annotated(setOf(OpticsNames.OPTICS_ANNOTATION_FQNAME))
  }

  override fun FirDeclarationPredicateRegistrar.registerPredicates() {
    register(declarationPredicate)
  }

  private val COPY_NAME = Name.identifier("copy")

  /** A monomorphic class carrying both `@optics` and `@optics.copy`, onto which we add `copy`. */
  @OptIn(SymbolInternals::class)
  private fun isCopySource(classSymbol: FirClassSymbol<*>): Boolean =
    classSymbol is FirRegularClassSymbol &&
      classSymbol.typeParameterSymbols.isEmpty() &&
      session.predicateBasedProvider.matches(declarationPredicate, classSymbol) &&
      classSymbol.annotations.any { it.checkEvenIfUnresolved(OpticsNames.OPTICS_COPY_ANNOTATION) }

  override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> =
    if (isCopySource(classSymbol)) setOf(COPY_NAME) else emptySet()

  override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
    val source = context?.owner as? FirRegularClassSymbol ?: return emptyList()
    if (callableId.callableName != COPY_NAME || !isCopySource(source)) return emptyList()
    val companion = source.resolvedCompanionObjectSymbol ?: return emptyList()

    val sourceType = source.constructType(emptyArray(), false)
    val companionType = companion.constructType(emptyArray(), false)
    val copyType = OpticsNames.COPY.constructClassLikeType(arrayOf(sourceType), false)
    // context(Copy<Source>) Source.Companion.(Source) -> Unit  ==>  kotlin.Function3 with attributes.
    val blockAttributes = ConeAttributes.create(
      listOf(CompilerConeAttributes.ExtensionFunctionType, CompilerConeAttributes.ContextFunctionTypeParams(1)),
    )
    val blockType = ClassId(FqName("kotlin"), Name.identifier("Function3"))
      .constructClassLikeType(
        typeArguments = arrayOf(copyType, companionType, sourceType, session.builtinTypes.unitType.coneType),
        isMarkedNullable = false,
        blockAttributes,
      )
    val function = createMemberFunction(source, Key, COPY_NAME, sourceType) {
      valueParameter(Name.identifier("block"), blockType)
      visibility = FirOpticsExtractor.effectiveVisibility(source, session)
      // Give the function a body up front so the synthetic data-class `copy` body generator (which
      // runs in Fir2Ir, before our IR pass) does not treat this body-less generated `copy` as the
      // data-class copy and try to fill it as one. The real body is installed in the IR phase.
      withGeneratedDefaultBody()
    }
    return listOf(function.symbol)
  }
}
