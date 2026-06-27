package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticKind
import arrow.optics.plugin.OpticsClassKind
import arrow.optics.plugin.OpticsNames
import arrow.optics.plugin.OpticsTargetKind
import arrow.optics.plugin.computeTargets
import arrow.optics.plugin.lowercaseFirst
import arrow.optics.plugin.mostRestrictive
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.getSealedClassInheritors
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.primaryConstructorIfAny
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassId
import org.jetbrains.kotlin.fir.declarations.utils.isAbstract
import org.jetbrains.kotlin.fir.declarations.utils.isData
import org.jetbrains.kotlin.fir.declarations.utils.isInlineOrValue
import org.jetbrains.kotlin.fir.declarations.utils.isSealed
import org.jetbrains.kotlin.fir.declarations.utils.modality
import org.jetbrains.kotlin.fir.declarations.utils.visibility
import org.jetbrains.kotlin.fir.expressions.FirAnnotationCall
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeStarProjection
import org.jetbrains.kotlin.fir.types.ConeTypeProjection
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.fir.types.isMarkedNullable
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid
import org.jetbrains.kotlin.name.ClassId
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
  fun dslEnabled(symbol: FirRegularClassSymbol, session: FirSession): Boolean = OpticsTargetKind.DSL in effectiveTargets(symbol, session)

  /** The effective target set (algo §2.3): requested ∩ kind-allowed, plus COPY when present. */
  fun effectiveTargets(symbol: FirRegularClassSymbol, session: FirSession): Set<OpticsTargetKind> {
    val requested = requestedTargets(symbol, session)
    val hasCopy = symbol.hasAnnotation(OpticsNames.OPTICS_COPY_ANNOTATION, session)
    return computeTargets(classKind(symbol), requested, hasCopy)
  }

  /**
   * Parse the `targets` array of `@optics(...)`, collecting the referenced [OpticsTargetKind]s.
   *
   * We walk the *raw* annotation argument expressions (only the annotation's class id is forced to
   * resolve) and read the referenced enum-entry names. This is robust to the resolution phase at
   * which generation runs — relying on `resolvedAnnotationsWithArguments.argumentMapping` is not, as
   * the argument mapping may not yet be populated when `getCallableNamesForClass` runs (review §2.6).
   */
  private fun requestedTargets(symbol: FirRegularClassSymbol, session: FirSession): Set<OpticsTargetKind> {
    val annotation = symbol.resolvedAnnotationsWithClassIds
      .firstOrNull { it.toAnnotationClassId(session) == OpticsNames.OPTICS_ANNOTATION }
    if (annotation !is FirAnnotationCall) return emptySet()
    val found = mutableSetOf<OpticsTargetKind>()
    val collector = object : FirVisitorVoid() {
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
    }
    annotation.argumentList.arguments.forEach { it.accept(collector, null) }
    return found
  }

  /**
   * LENS foci for abstract properties that are uniform across every (data-class) subclass of a
   * sealed type (algo §5.2). Only monomorphic parents for now.
   */
  private fun sealedLensFoci(symbol: FirRegularClassSymbol, session: FirSession): List<FirFocus> {
    if (symbol.typeParameterSymbols.isNotEmpty()) return emptyList()
    val abstractProps = mutableListOf<FirPropertySymbol>()
    symbol.processAllDeclarations(session) {
      if (it is FirPropertySymbol && it.isAbstract && it.receiverParameterSymbol == null) {
        abstractProps.add(it)
      }
    }
    if (abstractProps.isEmpty()) return emptyList()

    val subclasses = symbol.getSealedClassInheritors(session).mapNotNull {
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

  /**
   * Structural type equality for the §5.2 uniformity check: classifier, nullability, and (recursively)
   * the type arguments together with their projection kind, so e.g. `List<String>` and `List<Int>`
   * are *not* considered uniform.
   */
  private fun sameType(a: ConeKotlinType, b: ConeKotlinType): Boolean {
    if (a.classId != b.classId || a.isMarkedNullable != b.isMarkedNullable) return false
    val aArgs = a.typeArguments
    val bArgs = b.typeArguments
    if (aArgs.size != bArgs.size) return false
    return aArgs.indices.all { i ->
      val at = aArgs[i].type
      val bt = bArgs[i].type
      if (at == null || bt == null) {
        aArgs[i].kind == bArgs[i].kind // star projections
      } else {
        aArgs[i].kind == bArgs[i].kind && sameType(at, bt)
      }
    }
  }

  /**
   * The most-restrictive visibility of [symbol] and all of its enclosing classifiers (algo §3.3).
   * Used directly for the top-level DSL/copy extensions, and combined with the companion's own
   * visibility for the base companion members.
   */
  fun effectiveVisibility(symbol: FirRegularClassSymbol, session: FirSession): Visibility {
    var result: Visibility = symbol.visibility
    var outerId = symbol.classId.outerClassId
    while (outerId != null) {
      val outer = session.symbolProvider.getClassLikeSymbolByClassId(outerId) as? FirRegularClassSymbol
      if (outer != null) result = mostRestrictive(result, outer.visibility)
      outerId = outerId.outerClassId
    }
    return result
  }

  /** One PRISM focus per sealed subclass (algo §6). */
  private fun prismFoci(symbol: FirRegularClassSymbol, session: FirSession): List<FirFocus> = symbol.getSealedClassInheritors(session).mapNotNull { classId ->
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

  @OptIn(SymbolInternals::class)
  private fun FirRegularClassSymbol.getSealedClassInheritors(session: FirSession): List<ClassId> = fir.getSealedClassInheritors(session)
}
