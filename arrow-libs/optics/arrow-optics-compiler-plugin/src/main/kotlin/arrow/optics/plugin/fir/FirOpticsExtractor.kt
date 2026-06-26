package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticKind
import arrow.optics.plugin.OpticsClassKind
import arrow.optics.plugin.OpticsNames
import arrow.optics.plugin.OpticsTargetKind
import arrow.optics.plugin.computeTargets
import arrow.optics.plugin.lowercaseFirst
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.getSealedClassInheritors
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassId
import org.jetbrains.kotlin.fir.declarations.primaryConstructorIfAny
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid
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
  /** The focus type as used for a *monomorphic* parent (field type for a lens, `Sub<*>` for a prism). */
  val focusType: ConeKotlinType,
  /** For lenses/isos: the source component (constructor parameter / property) name. */
  val componentName: Name? = null,
  /** For prisms: the subclass symbol (used for generic parents, algo §6). */
  val subclass: FirRegularClassSymbol? = null,
  /** For prisms with a generic parent: the subclass's supertype, e.g. `Parent<String, C>`. */
  val refinedSource: ConeKotlinType? = null,
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

  /** Base optic foci to generate as companion members of [symbol], honouring the requested targets. */
  fun foci(symbol: FirRegularClassSymbol, session: FirSession): List<FirFocus> {
    val targets = effectiveTargets(symbol, session)
    val all = when (classKind(symbol)) {
      OpticsClassKind.DATA -> constructorFoci(symbol, session, OpticKind.LENS)
      OpticsClassKind.VALUE -> constructorFoci(symbol, session, OpticKind.ISO)
      OpticsClassKind.SEALED -> prismFoci(symbol, session) + sealedLensFoci(symbol, session)
      else -> emptyList()
    }
    return all.filter { targetOf(it.kind) in targets }
  }

  private fun targetOf(kind: OpticKind): OpticsTargetKind = when (kind) {
    OpticKind.LENS -> OpticsTargetKind.LENS
    OpticKind.ISO -> OpticsTargetKind.ISO
    OpticKind.PRISM -> OpticsTargetKind.PRISM
  }

  /** Whether the DSL composition extensions should be generated for [symbol]. */
  fun dslEnabled(symbol: FirRegularClassSymbol, session: FirSession): Boolean =
    OpticsTargetKind.DSL in effectiveTargets(symbol, session)

  /** The effective target set (algo §2.3): requested ∩ kind-allowed, plus COPY when present. */
  fun effectiveTargets(symbol: FirRegularClassSymbol, session: FirSession): Set<OpticsTargetKind> {
    val requested = requestedTargets(symbol, session)
    val hasCopy = symbol.hasAnnotation(OpticsNames.OPTICS_COPY_ANNOTATION, session)
    return computeTargets(classKind(symbol), requested, hasCopy)
  }

  /** Parse the `targets` array of `@optics(...)`, collecting the referenced [OpticsTargetKind]s. */
  private fun requestedTargets(symbol: FirRegularClassSymbol, session: FirSession): Set<OpticsTargetKind> {
    val annotation = symbol.resolvedAnnotationsWithArguments
      .firstOrNull { it.toAnnotationClassId(session) == OpticsNames.OPTICS_ANNOTATION } ?: return emptySet()
    val targetsExpr = annotation.argumentMapping.mapping[Name.identifier("targets")] ?: return emptySet()
    val found = mutableSetOf<OpticsTargetKind>()
    targetsExpr.acceptChildren(object : FirVisitorVoid() {
      override fun visitElement(element: FirElement) {
        element.acceptChildren(this)
      }

      override fun visitPropertyAccessExpression(propertyAccessExpression: FirPropertyAccessExpression) {
        when (propertyAccessExpression.calleeReference.name.asString()) {
          "ISO" -> found += OpticsTargetKind.ISO
          "LENS" -> found += OpticsTargetKind.LENS
          "PRISM" -> found += OpticsTargetKind.PRISM
          "DSL" -> found += OpticsTargetKind.DSL
          // OPTIONAL is silently dropped (algo §2.3)
        }
        propertyAccessExpression.acceptChildren(this)
      }
    })
    return found
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

  /** One PRISM focus per sealed subclass (algo §6). */
  private fun prismFoci(symbol: FirRegularClassSymbol, session: FirSession): List<FirFocus> {
    val inheritorIds = symbol.fir.getSealedClassInheritors(session)
    return inheritorIds.mapNotNull { classId ->
      val sub = session.symbolProvider.getClassLikeSymbolByClassId(classId) as? FirRegularClassSymbol
        ?: return@mapNotNull null
      val starArgs: Array<ConeTypeProjection> =
        Array(sub.typeParameterSymbols.size) { ConeStarProjection }
      // The subclass's supertype that mentions the sealed parent, e.g. `Parent<String, C>`.
      val refined = sub.resolvedSuperTypes.firstOrNull { it.classId == symbol.classId }
      FirFocus(
        kind = OpticKind.PRISM,
        opticName = lowercaseFirst(classId.shortClassName),
        focusType = sub.constructType(starArgs, false),
        subclass = sub,
        refinedSource = refined,
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
