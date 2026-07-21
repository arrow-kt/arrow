package arrow.optics.plugin.fir

import arrow.optics.plugin.OpticsNames
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.psi.KtElement

class OpticsSerializableCheckerExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
  override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
    override val regularClassCheckers = setOf(OpticsSerializableChecker)
  }
}

object OpticsSerializableChecker : FirRegularClassChecker(MppCheckerKind.Common) {
  context(context: CheckerContext, reporter: DiagnosticReporter)
  override fun check(declaration: FirRegularClass) {
    val companion = declaration.companionObjectSymbol
    if (companion != null && keyOf(companion.origin) == null) return
    if (declaration.annotations.none { it.checkEvenIfUnresolved(OpticsNames.OPTICS_ANNOTATION) }) return

    for (annotation in declaration.annotations) {
      if (!annotation.checkEvenIfUnresolved(OpticsNames.SERIALIZABLE_ANNOTATION)) continue
      reporter.reportOn(annotation.source, OpticsSerializableDiagnostics.INCOMPATIBLE_ANNOTATION, context)
    }
  }
}

object OpticsSerializableDiagnostics : KtDiagnosticsContainer() {
  val INCOMPATIBLE_ANNOTATION by error0<KtElement>()

  override fun getRendererFactory() = object : BaseDiagnosticRendererFactory() {
    override val MAP by KtDiagnosticFactoryToRendererMap("Arrow Optics") {
      it.put(
        INCOMPATIBLE_ANNOTATION,
        "This annotation is not compatible with @optics because it also creates a companion object. Please create a companion object explicitly.",
      )
    }
  }
}
