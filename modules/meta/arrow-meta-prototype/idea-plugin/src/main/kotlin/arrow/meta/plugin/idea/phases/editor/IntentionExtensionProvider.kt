package arrow.meta.plugin.idea.phases.editor

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.IntentionManager

interface IntentionExtensionProvider {
  fun registerIntention(intention: IntentionAction, category: String): Unit? =
    IntentionManager.getInstance()?.registerIntentionAndMetaData(intention, category)

  fun registerIntention(intention: IntentionAction): Unit? =
    IntentionManager.getInstance()?.addAction(intention)

  fun unregisterIntention(intention: IntentionAction): Unit? =
    IntentionManager.getInstance()?.unregisterIntention(intention)
}

