package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticKind
import arrow.optics.plugin.OpticsClassKind
import arrow.optics.plugin.lowercaseFirst
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.getSealedClassInheritors
import org.jetbrains.kotlin.fir.declarations.primaryConstructorIfAny
import org.jetbrains.kotlin.fir.declarations.utils.isAbstract
import org.jetbrains.kotlin.fir.declarations.utils.isData
import org.jetbrains.kotlin.fir.declarations.utils.isInlineOrValue
import org.jetbrains.kotlin.fir.declarations.utils.modality
import org.jetbrains.kotlin.fir.declarations.utils.isSealed
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeStarProjection
import org.jetbrains.kotlin.fir.types.ConeTypeProjection
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.fir.types.isMarkedNullable
import org.jetbrains.kotlin.name.Name

/** A single base-optic focus discovered on the source class, as seen from FIR. */
data class FirFocus(
  val kind: OpticKind,
  val opticName: Name,
  /** The focus type (e.g. the field type for a lens, the subtype for a prism). */
  val focusType: ConeKotlinType,
  /** For lenses/isos: the source component (constructor parameter / property) name. */
  val componentName: Name? = null,
)

/** Reads `@optics`-annotated FIR class symbols and extracts the foci to generate. */
@OptIn(SymbolInternals::class, DirectDeclarationsAccess::class)
object FirOpticsExtractor {

  fun classKind(symbol: FirRegularClassSymbol): OpticsClassKind = when {
    symbol.isData -> OpticsClassKind.DATA
    symbol.isInlineOrValue && symbol.classKind == ClassKind.CLASS -> OpticsClassKind.VALUE
    symbol.isSealed || symbol.modality == Modality.SEALED -> OpticsClassKind.SEALED
    else -> OpticsClassKind.INELIGIBLE
  }

  /** Base optic foci to generate as companion members of [symbol]. */
  fun foci(symbol: FirRegularClassSymbol, session: FirSession): List<FirFocus> =
    when (classKind(symbol)) {
      OpticsClassKind.DATA -> constructorFoci(symbol, session, OpticKind.LENS)
      OpticsClassKind.VALUE -> constructorFoci(symbol, session, OpticKind.ISO)
      OpticsClassKind.SEALED -> prismFoci(symbol, session) + sealedLensFoci(symbol, session)
      else -> emptyList()
    }

  /**
   * LENS foci for abstract properties that are uniform across every (data-class) subclass of a
   * sealed type (algo §5.2). Only monomorphic parents for now.
   */
  private fun sealedLensFoci(symbol: FirRegularClassSymbol, session: FirSession): List<FirFocus> {
    if (symbol.typeParameterSymbols.isNotEmpty()) return emptyList()
    val abstractProps = symbol.fir.declarations
      .filterIsInstance<org.jetbrains.kotlin.fir.declarations.FirProperty>()
      .filter { it.isAbstract && it.receiverParameter == null }
      .map { it.symbol }
    if (abstractProps.isEmpty()) return emptyList()

    val subclasses = symbol.fir.getSealedClassInheritors(session).mapNotNull {
      session.symbolProvider.getClassLikeSymbolByClassId(it) as? FirRegularClassSymbol
    }
    if (subclasses.isEmpty() || subclasses.any { !it.isData }) return emptyList()

    return abstractProps.mapNotNull { prop ->
      val propType = prop.resolvedReturnType
      val uniform = subclasses.all { sub ->
        val ctorParam = sub.primaryConstructorIfAny(session)
          ?.valueParameterSymbols?.firstOrNull { it.name == prop.name }
        ctorParam != null && sameType(ctorParam.resolvedReturnType, propType)
      }
      if (!uniform) return@mapNotNull null
      FirFocus(
        kind = OpticKind.LENS,
        opticName = prop.name,
        focusType = propType,
        componentName = prop.name,
      )
    }
  }

  /** Structural type equality sufficient for uniformity checks (classifier + nullability). */
  private fun sameType(a: ConeKotlinType, b: ConeKotlinType): Boolean =
    a.classId == b.classId && a.isMarkedNullable == b.isMarkedNullable

  /** One PRISM focus per sealed subclass (algo §6). Generic parents are handled separately (TODO). */
  private fun prismFoci(symbol: FirRegularClassSymbol, session: FirSession): List<FirFocus> {
    if (symbol.typeParameterSymbols.isNotEmpty()) return emptyList()
    val inheritorIds = symbol.fir.getSealedClassInheritors(session)
    return inheritorIds.mapNotNull { classId ->
      val sub = session.symbolProvider.getClassLikeSymbolByClassId(classId) as? FirRegularClassSymbol
        ?: return@mapNotNull null
      val args: Array<ConeTypeProjection> =
        Array(sub.typeParameterSymbols.size) { ConeStarProjection }
      FirFocus(
        kind = OpticKind.PRISM,
        opticName = lowercaseFirst(classId.shortClassName),
        focusType = sub.constructType(args, false),
      )
    }
  }

  /** One focus per primary-constructor value parameter (LENS for data, ISO for value classes). */
  private fun constructorFoci(symbol: FirRegularClassSymbol, session: FirSession, kind: OpticKind): List<FirFocus> {
    val ctor = symbol.primaryConstructorIfAny(session) ?: return emptyList()
    return ctor.valueParameterSymbols.map { param: FirValueParameterSymbol ->
      FirFocus(
        kind = kind,
        opticName = param.name,
        focusType = param.resolvedReturnType,
        componentName = param.name,
      )
    }
  }
}
