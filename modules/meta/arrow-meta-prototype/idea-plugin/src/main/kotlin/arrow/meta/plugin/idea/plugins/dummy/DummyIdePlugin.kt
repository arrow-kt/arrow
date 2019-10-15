package arrow.meta.plugin.idea.plugins.dummy

import arrow.meta.Plugin
import arrow.meta.dsl.ide.editor.inspection.ExtendedReturnsCheck
import arrow.meta.invoke
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.codeInspection.ProblemHighlightType
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.util.ReturnsCheck

/**
 * Unrelated to [arrow.meta.plugins.dummy.dummy]
 */
val IdeMetaPlugin.dummyIdePlugin: Plugin
  get() = "DummyIdePlugin" {
    meta(
      addApplicableInspection(
        defaultFixText = "Impure",
        inspectionHighlightType = { ProblemHighlightType.ERROR },
        kClass = KtNamedFunction::class.java,
        highlightingRange = { f -> f.textRange },
        inspectionText = { f -> "Function should be suspended" },
        applyTo = { f, project, editor ->
          f.addModifier(KtTokens.SUSPEND_KEYWORD)
        },
        isApplicable = { f: KtNamedFunction ->
          f.nameIdentifier != null && !f.hasModifier(KtTokens.SUSPEND_KEYWORD) &&
            f.resolveToDescriptorIfAny()?.run {
              !isSuspend && (ReturnsCheck.ReturnsUnit.check(this) || ExtendedReturnsCheck.ReturnsNothing.check(this)
                || ExtendedReturnsCheck.ReturnsNullableNothing.check(this))
            } == true
        }
      )
    )
  }
