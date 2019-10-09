package arrow.meta.dsl.ide.editor.quickFix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.diagnostic.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory

interface QuickFixUtilitySyntax {

}

/**
 * Contributor -> quickFixes.register(DiFac, Inten
 */
data class MetaQuickFixIntention(
  val respondOn: DiagnosticFactory<*>,
  val intentions: List<IntentionAction> = emptyList()
)

data class MetaQuickFixIntentionForKotlin(
  val respondOn: DiagnosticFactory<*>,
  val intentions: List<(Diagnostic) -> KotlinSingleIntentionActionFactory> = emptyList()
)
