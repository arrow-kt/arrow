package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticsNames
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createTopLevelFunction
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
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
 * Generates the `@optics.copy` builder (algo §9) as a top-level extension:
 * `fun Source.copy(block: context(Copy<Source>) Source.Companion.(Source) -> Unit): Source`.
 * Only monomorphic sources are supported for now.
 */
@OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
class OpticsCopyGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {
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

  @OptIn(SymbolInternals::class)
  private fun copySources(): List<FirRegularClassSymbol> = session.predicateBasedProvider.getSymbolsByPredicate(lookupPredicate)
    .filterIsInstance<FirRegularClassSymbol>()
    .filter { it.typeParameterSymbols.isEmpty() && it.annotations.any { it.checkEvenIfUnresolved(OpticsNames.OPTICS_COPY_ANNOTATION) } }

  override fun getTopLevelCallableIds(): Set<CallableId> = copySources().mapTo(mutableSetOf()) {
    CallableId(it.classId.packageFqName, Name.identifier("copy"))
  }

  override fun hasPackage(packageFqName: FqName): Boolean = copySources().any { it.classId.packageFqName == packageFqName }

  override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
    if (context != null) return emptyList()
    return copySources()
      .filter { it.classId.packageFqName == callableId.packageName }
      .mapNotNull { source ->
        val companion = source.resolvedCompanionObjectSymbol ?: return@mapNotNull null
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
        val function = createTopLevelFunction(
          key = Key,
          callableId = callableId,
          returnType = sourceType,
          containingFileName = "${source.classId.shortClassName.asString()}Copy",
        ) {
          extensionReceiverType(sourceType)
          valueParameter(Name.identifier("block"), blockType)
          visibility = FirOpticsExtractor.effectiveVisibility(source, session)
        }
        function.symbol
      }
  }
}
