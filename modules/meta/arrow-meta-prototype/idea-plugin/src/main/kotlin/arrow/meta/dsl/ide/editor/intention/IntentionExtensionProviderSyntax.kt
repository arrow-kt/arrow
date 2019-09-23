package arrow.meta.dsl.ide.editor.intention

import arrow.meta.dsl.platform.ide
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.phases.editor.IntentionExtensionProvider
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.editor.Editor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.intentions.SelfTargetingIntention
import org.jetbrains.kotlin.idea.quickfix.KotlinIntentionActionsFactory
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.idea.quickfix.QuickFixContributor

// in storage:: analyzer:: (ApplicationManager.getApplication())?.getComponent(IntentionManager::class.java)

interface IntentionExtensionProviderSyntax : IntentionExtensionProvider {
  // TODO: Test impl
  fun IdeMetaPlugin.addIntention(
    intention: IntentionAction
  ): ExtensionPhase =
    ide {
      storageComponent(
        registerModuleComponents = { container, moduleDescriptor ->
          // analyzer?.run {
          registerIntention(intention)
          println("ADDED Intention")
          //}
        },
        check = { _, _, _ ->
        }
      )
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.addIntention(
    category: String,
    intention: IntentionAction
  ): ExtensionPhase =
    ide {
      storageComponent(
        registerModuleComponents = { container, moduleDescriptor ->
          // analyzer?.run {
          registerIntention(intention, category)
          println("ADDED Intention")
          //}
        },
        check =
        { _, _, _ ->
        }
      )
    } ?: ExtensionPhase.Empty


  fun IdeMetaPlugin.unregisterIntention(
    intention: IntentionAction
  ): ExtensionPhase =
    ide {
      storageComponent(
        registerModuleComponents = { container, moduleDescriptor ->
          // analyzer?.run {
          PluginManagerCore.getPlugins()
          this@IntentionExtensionProviderSyntax.unregisterIntention(intention)
          println("Unregistered Intention")
          //}
        },
        check = { _, _, _ ->
        }
      )
    } ?: ExtensionPhase.Empty

  /**
   * Use this in [addQuickFixContributor] for @param intentions
   */
  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> IntentionExtensionProviderSyntax.addIntention(
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
    addIntention(object : SelfTargetingIntention<K>(kClass, text), PriorityAction {
      override fun applyTo(element: K, editor: Editor?) =
        applyTo(element, editor)

      override fun isApplicableTo(element: K, caretOffset: Int): Boolean =
        isApplicableTo(element, caretOffset)

      override fun getPriority(): PriorityAction.Priority =
        priority
    })


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

