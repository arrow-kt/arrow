package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticsClassKind
import arrow.optics.plugin.OpticsNames
import arrow.optics.plugin.OpticsTargetKind
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.diagnostics.warning0
import org.jetbrains.kotlin.diagnostics.warning1
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassIdSafe
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirUserTypeRef
import org.jetbrains.kotlin.psi.KtElement

class OpticsCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
  override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
    override val regularClassCheckers = setOf(OpticsChecker)
  }
}

/**
 * Reports every situation in which `@optics` cannot generate (some of) what was asked for (algo §12),
 * so that nothing fails silently.
 */
object OpticsChecker : FirRegularClassChecker(MppCheckerKind.Common) {
  private val BASE_TARGETS = setOf(OpticsTargetKind.ISO, OpticsTargetKind.LENS, OpticsTargetKind.PRISM, OpticsTargetKind.COPY)

  context(context: CheckerContext, reporter: DiagnosticReporter)
  override fun check(declaration: FirRegularClass) {
    val session = context.session
    val opticsAnnotations = declaration.annotations.filter {
      val id = it.toAnnotationClassIdSafe(session)
      id == OpticsNames.OPTICS_ANNOTATION || id == OpticsNames.OPTICS_COPY_ANNOTATION
    }
    if (opticsAnnotations.isEmpty()) return

    // FIR predicates (and our own early checks) only see the name as written.
    var aliased = false
    for (annotation in opticsAnnotations) {
      val written = (annotation.annotationTypeRef as? FirResolvedTypeRef)?.delegatedTypeRef as? FirUserTypeRef ?: continue
      if (!written.isWrittenAs(annotation.toAnnotationClassIdSafe(session) ?: continue)) {
        reporter.reportOn(annotation.source, OpticsDiagnostics.ALIASED_ANNOTATION, context)
        aliased = true
      }
    }
    val opticsAnnotation = opticsAnnotations.firstOrNull { it.toAnnotationClassIdSafe(session) == OpticsNames.OPTICS_ANNOTATION } ?: return
    if (aliased) return

    if (!FirOpticsExtractor.runtimeAvailable(session)) {
      reporter.reportOn(opticsAnnotation.source, OpticsDiagnostics.MISSING_RUNTIME, context)
      return
    }

    if (checkSerializable(declaration, session)) return

    val symbol = declaration.symbol
    if (FirOpticsExtractor.classKind(symbol) == OpticsClassKind.INELIGIBLE) {
      reporter.reportOn(opticsAnnotation.source, OpticsDiagnostics.INELIGIBLE_CLASS, context)
      return
    }

    if (FirOpticsExtractor.effectiveTargets(symbol).none { it in BASE_TARGETS }) {
      reporter.reportOn(opticsAnnotation.source, OpticsDiagnostics.NO_TARGETS, context)
      return
    }

    val foci = FirOpticsExtractor.allFoci(symbol, resolveFocusTypes = true, session)
    val clashing = FirOpticsExtractor.clashingNames(foci)
    for (name in clashing) {
      reporter.reportOn(opticsAnnotation.source, OpticsDiagnostics.NAME_CLASH, name.asString(), context)
    }

    if (!FirOpticsExtractor.unsafeMarkerAvailable(session)) {
      for (focus in foci) {
        if (focus.potentiallyUnsafe && focus.opticName !in clashing) {
          reporter.reportOn(opticsAnnotation.source, OpticsDiagnostics.UNSAFE_MARKER_MISSING, focus.opticName.asString(), context)
        }
      }
    }

    val userNames = FirOpticsExtractor.userMemberNames(declaration.companionObjectSymbol)
    for (focus in foci) {
      if (focus.opticName in userNames && focus.opticName !in clashing) {
        reporter.reportOn(opticsAnnotation.source, OpticsDiagnostics.USER_MEMBER_CLASH, focus.opticName.asString(), context)
      }
    }

    if (FirOpticsExtractor.dslEnabled(symbol)) {
      val skipped = FirOpticsExtractor.dslFoci(symbol, resolveFocusTypes = true, session)
        .filter { FirOpticsExtractor.dslVisibility(symbol, it, session) == null }
      if (skipped.isNotEmpty()) {
        reporter.reportOn(opticsAnnotation.source, OpticsDiagnostics.DSL_SKIPPED, skipped.joinToString { it.opticName.asString() }, context)
      }
    }
  }

  /**
   * `@Serializable` also generates a companion object, so `@optics` does not create one (review 2.3).
   * Returns `true` if a problem was reported.
   */
  context(context: CheckerContext, reporter: DiagnosticReporter)
  private fun checkSerializable(declaration: FirRegularClass, session: FirSession): Boolean {
    val companion = declaration.companionObjectSymbol
    if (companion != null && keyOf(companion.origin) == null) return false

    var reported = false
    for (annotation in declaration.annotations) {
      if (annotation.toAnnotationClassIdSafe(session) == OpticsNames.SERIALIZABLE_ANNOTATION) {
        reporter.reportOn(annotation.source, OpticsDiagnostics.INCOMPATIBLE_ANNOTATION, context)
        reported = true
      } else if (companion == null && annotation.isWrittenAsSerializable()) {
        // generation only sees the name, so any annotation called `Serializable` prevents the companion
        reporter.reportOn(annotation.source, OpticsDiagnostics.SERIALIZABLE_NAME, context)
        reported = true
      }
    }
    return reported
  }

  private fun FirAnnotation.isWrittenAsSerializable(): Boolean {
    val written = (annotationTypeRef as? FirResolvedTypeRef)?.delegatedTypeRef as? FirUserTypeRef ?: return false
    return written.qualifier.lastOrNull()?.name == OpticsNames.SERIALIZABLE_ANNOTATION.shortClassName
  }
}

object OpticsDiagnostics : KtDiagnosticsContainer() {
  val INCOMPATIBLE_ANNOTATION by error0<KtElement>()
  val SERIALIZABLE_NAME by error0<KtElement>()
  val ALIASED_ANNOTATION by error0<KtElement>()
  val MISSING_RUNTIME by error0<KtElement>()
  val INELIGIBLE_CLASS by error0<KtElement>()
  val NO_TARGETS by warning0<KtElement>()
  val NAME_CLASH by error1<KtElement, String>()
  val USER_MEMBER_CLASH by warning1<KtElement, String>()
  val DSL_SKIPPED by warning1<KtElement, String>()
  val UNSAFE_MARKER_MISSING by warning1<KtElement, String>()

  override fun getRendererFactory() = object : BaseDiagnosticRendererFactory() {
    override val MAP by KtDiagnosticFactoryToRendererMap("Arrow Optics") {
      it.put(
        INCOMPATIBLE_ANNOTATION,
        "This annotation is not compatible with @optics because it also creates a companion object. Please create a companion object explicitly.",
      )
      it.put(
        SERIALIZABLE_NAME,
        "@optics does not create a companion object for classes with an annotation named 'Serializable'. Please create a companion object explicitly.",
      )
      it.put(
        ALIASED_ANNOTATION,
        "@optics and @optics.copy must be referred to by their own name; import aliases and type aliases are not supported.",
      )
      it.put(
        MISSING_RUNTIME,
        "@optics requires the arrow-optics library, which was not found in the classpath.",
      )
      it.put(
        INELIGIBLE_CLASS,
        "Only data classes, value classes with a single field, and sealed classes and interfaces can be annotated with @optics.",
      )
      it.put(
        NO_TARGETS,
        "No optics will be generated for this class: none of the requested targets applies to it (the DSL target also requires the base optic target).",
      )
      it.put(
        NAME_CLASH,
        "Several optics would be named ''{0}''; none of them is generated.",
        CommonRenderers.STRING,
      )
      it.put(
        USER_MEMBER_CLASH,
        "The optic ''{0}'' is not generated because the companion object already declares a member with that name.",
        CommonRenderers.STRING,
      )
      it.put(
        UNSAFE_MARKER_MISSING,
        "The lens ''{0}'' is not generated because setting it may not respect covariant type arguments, and the opt-in marker 'arrow.optics.PotentiallyUnsafeCovariantOptic' was not found in the classpath. Please update arrow-annotations.",
        CommonRenderers.STRING,
      )
      it.put(
        DSL_SKIPPED,
        "The DSL extensions for {0} are not generated because they would have to be private or protected top-level declarations.",
        CommonRenderers.STRING,
      )
    }
  }
}
