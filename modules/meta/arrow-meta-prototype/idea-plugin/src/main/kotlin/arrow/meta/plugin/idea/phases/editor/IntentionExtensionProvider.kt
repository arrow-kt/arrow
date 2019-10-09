package arrow.meta.plugin.idea.phases.editor

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.IntentionManager
import com.intellij.codeInsight.intention.impl.config.IntentionManagerSettings

interface IntentionExtensionProvider {
  fun register(intention: IntentionAction, category: String): Unit? =
    IntentionManager.getInstance()?.registerIntentionAndMetaData(intention, category)

  fun register(intention: IntentionAction): Unit? =
    IntentionManager.getInstance()?.addAction(intention)

  fun unregister(intention: IntentionAction): Unit? =
    IntentionManager.getInstance()?.unregisterIntention(intention)

  fun availableIntentions(): List<IntentionAction> =
    IntentionManager.getInstance()?.availableIntentionActions?.toList() ?: emptyList()

  fun IntentionAction.isEnabled(): Boolean =
    IntentionManagerSettings.getInstance().isEnabled(this)

  fun IntentionAction.setEnabled(enabled: Boolean): Unit =
    IntentionManagerSettings.getInstance().setEnabled(this, enabled)
}

