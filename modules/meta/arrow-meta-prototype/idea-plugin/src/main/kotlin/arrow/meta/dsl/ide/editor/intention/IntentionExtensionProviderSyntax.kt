package arrow.meta.dsl.ide.editor.intention

import arrow.meta.dsl.platform.ideRegistry
import arrow.meta.internal.Noop
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
  fun IdeMetaPlugin.addIntention(
    intention: IntentionAction
  ): ExtensionPhase =
    ideRegistry {
      register(intention)?.run {
        println("ADDED Intention")
      }
    }

  /**
   * TODO: This Bails if there is no html and intentionDescription. Try to add them in the Function and not with the resource Folder. But this is fine for now
   */
  fun IdeMetaPlugin.addIntention(
    category: String,
    intention: IntentionAction
  ): ExtensionPhase =
    ideRegistry {
      register(intention, category)?.run {
        println("ADDED Intention with MetaData")
      }
    }

  fun IdeMetaPlugin.unregisterIntention(
    intention: IntentionAction
  ): ExtensionPhase =
    ideRegistry {
      unregister(intention)?.run {
        println("Unregistered Intention")
      }
    }

  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> IdeMetaPlugin.addIntention(
    text: String = "",
    kClass: Class<K> = KtElement::class.java as Class<K>,
    isApplicableTo: (element: K, caretOffset: Int) -> Boolean = Noop.boolean2False,
    applyTo: (element: K, editor: Editor?) -> Unit = Noop.effect2,
    priority: PriorityAction.Priority = PriorityAction.Priority.LOW
  ): ExtensionPhase =
    addIntention(ktIntention(text, kClass, isApplicableTo, applyTo, priority))

  fun IdeMetaPlugin.setIntentionAsEnabled(enabled: Boolean, intention: IntentionAction): ExtensionPhase =
    ideRegistry {
      intention.setEnabled(enabled)
    }

  /**
   * You can use this in [addQuickFixContributor] for @param intentions
   * @param text == familyName for creating MetaData for an Intentions
   */
  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> IntentionExtensionProviderSyntax.ktIntention(
    text: String = "",
    kClass: Class<K> = KtElement::class.java as Class<K>,
    isApplicableTo: (element: K, caretOffset: Int) -> Boolean = Noop.boolean2False,
    applyTo: (element: K, editor: Editor?) -> Unit = Noop.effect2,
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
  fun IntentionExtensionProviderSyntax.kotlinIntention(
    createAction: (diagnostic: Diagnostic) -> IntentionAction? = Noop.nullable1(),
    isApplicableForCodeFragment: Boolean = false,
    doCreateActionsForAllProblems: (sameTypeDiagnostics: Collection<Diagnostic>) -> List<IntentionAction> = Noop.emptyList1()
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

