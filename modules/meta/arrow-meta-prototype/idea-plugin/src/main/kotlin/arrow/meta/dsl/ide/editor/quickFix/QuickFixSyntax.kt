package arrow.meta.dsl.ide.editor.quickFix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory
import org.jetbrains.kotlin.idea.quickfix.KotlinIntentionActionsFactory
import org.jetbrains.kotlin.idea.quickfix.KotlinQuickFixAction
import org.jetbrains.kotlin.idea.quickfix.QuickFixContributor
import org.jetbrains.kotlin.idea.quickfix.QuickFixes

interface QuickFixSyntax : QuickFixUtilitySyntax {
  /*fun addQuickFix(
  match: (PsiElement) -> Boolean,
  priority: PriorityAction = object : LowPriorityAction {},
  familyName: String,
  text: String,
  invoke: (project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) -> Unit
  ): LocalQuickFixOnPsiElement =
  object : LocalQuickFixOnPsiElement(element), PriorityAction by priority {
    override fun getFamilyName(): String = familyName
    override fun getText(): String = text

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement): Unit =
      invoke(project, file, startElement, endElement)
  }*/

  /**
   * Use [QuickFixContributor.EP_NAME], Still WIP
   */
  fun <K : PsiElement> addQuickFixContributor(
    kotlinIntentions: List<MetaQuickFixIntentionForKotlin> = emptyList(),
    intentions: List<MetaQuickFixIntention> = emptyList()
  ): QuickFixContributor =
    object : QuickFixContributor {
      override fun registerQuickFixes(quickFixes: QuickFixes) {
        fun DiagnosticFactory<*>.registerIntentions(vararg factory: KotlinIntentionActionsFactory) {

        }

        fun DiagnosticFactory<*>.registerActions(vararg action: IntentionAction) {
          quickFixes.register(this, *action)
        }
      }
    }

  fun <K : PsiElement> addKotlinQuickFixBase(
    element: K
  ): KotlinQuickFixAction<K> = TODO()
  // object : KotlinQuickFixAction<K>(element)
}
