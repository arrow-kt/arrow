package arrow.optics.plugin.fir

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.utils.isCompanion
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.NestedClassGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createCompanionObject
import org.jetbrains.kotlin.fir.plugin.createDefaultPrivateConstructor
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(DirectDeclarationsAccess::class, SymbolInternals::class, ExperimentalContracts::class)
class OpticsCompanionGenerator(session: FirSession) : FirDeclarationGenerationExtension(session) {
  companion object {
    val OPTICS_ANNOTATION_FQNAME = FqName.fromSegments(listOf("arrow", "optics", "optics"))

    val predicate = DeclarationPredicate.create {
      annotated(setOf(OPTICS_ANNOTATION_FQNAME))
    }
  }

  override fun FirDeclarationPredicateRegistrar.registerPredicates() {
    register(predicate)
  }

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
    return createCompanionObject(owner, Key).symbol
  }

  fun FirClassSymbol<*>.isGeneratedOpticsCompanion(): Boolean {
    contract {
      returns(true) implies (this@isGeneratedOpticsCompanion is FirRegularClassSymbol)
    }
    return isCompanion && this is FirRegularClassSymbol && (origin as? FirDeclarationOrigin.Plugin)?.key == Key
  }

  override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
    if (!classSymbol.isGeneratedOpticsCompanion()) return emptySet()
    return setOf(SpecialNames.INIT)
  }

  override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
    val owner = context.owner
    if (!owner.isGeneratedOpticsCompanion()) return emptyList()
    return listOf(createDefaultPrivateConstructor(owner, Key).symbol)
  }

  object Key : GeneratedDeclarationKey()
}
