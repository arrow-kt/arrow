package arrow.meta.dsl.ide.editor.action

import arrow.meta.dsl.platform.ide
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.phases.editor.AnActionExtensionProvider
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.TimerListener
import com.intellij.openapi.application.ModalityState
import javax.swing.Icon

// TODO: Check Default of actionId
interface AnActionSyntax : AnActionExtensionProvider {
  fun IdeMetaPlugin.addAnAction(
    actionId: String = "",
    action: AnAction
  ): ExtensionPhase =
    ide {
      register(actionId, action)
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.replaceAnAction(
    actionId: String,
    newAction: AnAction
  ): ExtensionPhase =
    ide {
      replace(actionId, newAction)
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.unregisterAnAction(
    actionId: String
  ): ExtensionPhase =
    ide {
      println("Unregistered $actionId")
      unregister(actionId)
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.addTimerListener(
    delay: Int,
    modalityState: ModalityState,
    run: () -> Unit
  ): ExtensionPhase =
    ide {
      println("TimerListener is registered")
      addTimerListener(delay, this@AnActionSyntax.timerListener(modalityState, run))
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.addTransparentTimerListener(
    delay: Int,
    modalityState: ModalityState,
    run: () -> Unit
  ): ExtensionPhase = // TODO("Check impl)
    ide {
      addTransparentTimerListener(delay, this@AnActionSyntax.timerListener(modalityState, run))
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.removeTransparentTimerListener(
    listener: TimerListener
  ): ExtensionPhase =
    ide {
      removeTimerListener(listener)
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.removeTimerListener(
    listener: TimerListener
  ): ExtensionPhase =
    ide {
      removeTransparentTimerListener(listener)
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  /**
   * TODO: Add more costume attributes: ShortCuts etc.
   * [http://www.jetbrains.org/intellij/sdk/docs/tutorials/action_system/working_with_custom_actions.html
   */
  fun AnActionSyntax.addAnAction(
    actionPerformed: (e: AnActionEvent) -> Unit,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit = { _ -> },
    update: (e: AnActionEvent) -> Unit = { _ -> },
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null
  ): AnAction =
    object : AnAction() {
      override fun actionPerformed(e: AnActionEvent) = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean) =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent) = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent) =
        beforeActionPerformedUpdate(e)
    }


  fun AnActionSyntax.addAnAction(
    icon: Icon,
    actionPerformed: (e: AnActionEvent) -> Unit,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit = { _ -> },
    update: (e: AnActionEvent) -> Unit = { _ -> },
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null
  ): AnAction =
    object : AnAction(icon) {
      override fun actionPerformed(e: AnActionEvent) = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean) =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent) = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent) =
        beforeActionPerformedUpdate(e)
    }


  fun AnActionSyntax.addAnAction(
    title: String,
    actionPerformed: (e: AnActionEvent) -> Unit,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit = { _ -> },
    update: (e: AnActionEvent) -> Unit = { _ -> },
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null
  ): AnAction =
    object : AnAction(title) {
      override fun actionPerformed(e: AnActionEvent) = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean) =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent) = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent) =
        beforeActionPerformedUpdate(e)
    }


  fun AnActionSyntax.addAnAction(
    title: String,
    description: String,
    icon: Icon,
    actionPerformed: (e: AnActionEvent) -> Unit,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit = { _ -> },
    update: (e: AnActionEvent) -> Unit = { _ -> },
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null
  ): AnAction =
    object : AnAction(title, description, icon) {
      override fun actionPerformed(e: AnActionEvent) = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean) =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent) = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent) =
        beforeActionPerformedUpdate(e)
    }

  fun AnActionSyntax.timerListener(
    modalityState: ModalityState,
    run: () -> Unit
  ): TimerListener =
    object : TimerListener {
      override fun run() = run()

      override fun getModalityState(): ModalityState = modalityState
    }
}
