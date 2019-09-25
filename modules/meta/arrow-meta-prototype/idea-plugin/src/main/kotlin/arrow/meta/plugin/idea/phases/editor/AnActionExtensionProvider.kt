package arrow.meta.plugin.idea.phases.editor

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.TimerListener

interface AnActionExtensionProvider {
  fun register(actionId: String, action: AnAction): Unit? =
    ActionManager.getInstance()?.registerAction(actionId, action)

  fun unregister(actionId: String): Unit? =
    ActionManager.getInstance()?.unregisterAction(actionId)

  fun replace(actionId: String, newAction: AnAction): Unit? =
    ActionManager.getInstance()?.replaceAction(actionId, newAction)

  fun addTimerListener(delay: Int, listener: TimerListener): Unit? =
    ActionManager.getInstance()?.addTimerListener(delay, listener)

  fun addTransparentTimerListener(delay: Int, listener: TimerListener): Unit? =
    ActionManager.getInstance()?.addTransparentTimerListener(delay, listener)

  fun removeTimerListener(listener: TimerListener): Unit? =
    ActionManager.getInstance()?.removeTimerListener(listener)

  fun removeTransparentTimerListener(listener: TimerListener): Unit? =
    ActionManager.getInstance()?.removeTransparentTimerListener(listener)

  fun allActionIds(prefix: String): List<String> =
    ActionManager.getInstance().getActionIds(prefix).toList()
}
