package arrow.meta.dsl.ide.editor.intention

import arrow.meta.dsl.platform.ide
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.phases.editor.IntentionExtensionProvider
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.openapi.editor.Editor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.intentions.SelfTargetingIntention
import org.jetbrains.kotlin.idea.quickfix.KotlinIntentionActionsFactory
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.idea.quickfix.QuickFixContributor
import org.jetbrains.kotlin.psi.KtElement

interface IntentionExtensionProviderSyntax : IntentionExtensionProvider {
  // TODO: Maybe Add IdeExtensionRegistry Phase, but rn it's isomorphic to Empty
  fun IdeMetaPlugin.addIntention(
    intention: IntentionAction
  ): ExtensionPhase =
    ide {
      println("ADDED Intention")
      register(intention)?.run {
        ExtensionPhase.Empty
      }
    } ?: ExtensionPhase.Empty

  /**
   * TODO: This Bails if there is no html and intentionDescription. Try to add them in the Function and not with the resource Folder. But this is fine for now
   */
  fun IdeMetaPlugin.addIntention(
    category: String,
    intention: IntentionAction
  ): ExtensionPhase =
    ide {
      println("ADDED Intention with MetaData")
      register(intention, category)?.run {
        ExtensionPhase.Empty
      }
    } ?: ExtensionPhase.Empty


  fun IdeMetaPlugin.unregisterIntention(
    intention: IntentionAction
  ): ExtensionPhase =
    ide {
      println("Unregistered Intention")
      unregister(intention)?.run {
        ExtensionPhase.Empty
      }
    } ?: ExtensionPhase.Empty

  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> IdeMetaPlugin.addIntention(
    text: String = "",
    kClass: Class<K> = KtElement::class.java as Class<K>,
    isApplicableTo: (element: K, caretOffset: Int) -> Boolean =
      { _, _ -> false },
    applyTo: (element: K, editor: Editor?) -> Unit =
      { _, _ -> },
    priority: PriorityAction.Priority = PriorityAction.Priority.LOW
  ): ExtensionPhase =
    addIntention(addKtIntention(text, kClass, isApplicableTo, applyTo, priority))

  fun IdeMetaPlugin.setIntentionAsEnabled(enabled: Boolean, intention: IntentionAction): ExtensionPhase =
    ide {
      intention.setEnabled(enabled)
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  /**
   * You can use this in [addQuickFixContributor] for @param intentions
   * @param text == familyName for creating MetaData for an Intentions
   */
  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> IntentionExtensionProviderSyntax.addKtIntention(
    text: String = "",
    kClass: Class<K> = KtElement::class.java as Class<K>,
    isApplicableTo: (element: K, caretOffset: Int) -> Boolean =
      { _, _ -> false },
    applyTo: (element: K, editor: Editor?) -> Unit =
      { _, _ -> },
    priority: PriorityAction.Priority = PriorityAction.Priority.LOW
  ): SelfTargetingIntention<K> =
    object : SelfTargetingIntention<K>(kClass, text), PriorityAction {
      override fun applyTo(element: K, editor: Editor?) =
        applyTo(element, editor)

      override fun isApplicableTo(element: K, caretOffset: Int): Boolean =
        isApplicableTo(element, caretOffset)

      override fun getPriority(): PriorityAction.Priority =
        priority
    }

  /**
   * Defaults from [KotlinIntentionActionsFactory]
   * Solely for [QuickFixContributor]
   */
  fun IntentionExtensionProviderSyntax.addKotlinIntention(
    createAction: (diagnostic: Diagnostic) -> IntentionAction? = { null },
    isApplicableForCodeFragment: Boolean = false,
    doCreateActionsForAllProblems: (sameTypeDiagnostics: Collection<Diagnostic>) -> List<IntentionAction> =
      { emptyList<IntentionAction>() }
  ): KotlinSingleIntentionActionFactory =
    object : KotlinSingleIntentionActionFactory() {
      override fun createAction(diagnostic: Diagnostic): IntentionAction? =
        createAction(diagnostic)

      override fun doCreateActionsForAllProblems(sameTypeDiagnostics: Collection<Diagnostic>): List<IntentionAction> =
        doCreateActionsForAllProblems(sameTypeDiagnostics)

      override fun isApplicableForCodeFragment(): Boolean =
        isApplicableForCodeFragment
    }
}

