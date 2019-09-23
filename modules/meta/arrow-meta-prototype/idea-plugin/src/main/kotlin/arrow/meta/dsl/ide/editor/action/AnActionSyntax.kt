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
  fun IdeMetaPlugin.addTimerListener(
    delay: Int,
    modalityState: ModalityState,
    run: () -> Unit
  ): ExtensionPhase = // TODO("Check impl")
    ide {
      addTimerListener(delay, object : TimerListener {
        override fun run() = run()

        override fun getModalityState(): ModalityState = modalityState
      })
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.addTransparentTimerListener(
    delay: Int,
    modalityState: ModalityState,
    run: () -> Unit
  ): ExtensionPhase = // TODO("Check impl)
    ide {
      addTransparentTimerListener(delay, object : TimerListener {
        override fun run() = run()

        override fun getModalityState(): ModalityState = modalityState
      })
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.addAnAction(
    actionId: String = "",
    action: AnAction
  ): ExtensionPhase = // TODO("Check impl")
    ide {
      registerAction(actionId, action)
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.replaceAnAction(
    actionId: String,
    newAction: AnAction
  ): ExtensionPhase = // TODO("Check impl")
    ide {
      replaceAction(actionId, newAction)
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.removeTransparentTimerListener(
    listener: TimerListener
  ): ExtensionPhase = // TODO("Check impl")
    ide {
      removeTimerListener(listener)
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun IdeMetaPlugin.removeTimerListener(
    listener: TimerListener
  ): ExtensionPhase = // TODO("Check impl")
    ide {
      removeTransparentTimerListener(listener)
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun AnActionExtensionProvider.addAnAction(
    actionPerformed: (e: AnActionEvent) -> Unit,
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    update: (e: AnActionEvent) -> Unit = { _ -> },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit
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


  fun AnActionExtensionProvider.addAnAction(
    icon: Icon,
    actionPerformed: (e: AnActionEvent) -> Unit,
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    update: (e: AnActionEvent) -> Unit = { _ -> },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit
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


  fun AnActionExtensionProvider.addAnAction(
    title: String,
    actionPerformed: (e: AnActionEvent) -> Unit,
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    update: (e: AnActionEvent) -> Unit = { _ -> },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit
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


  fun AnActionExtensionProvider.addAnAction(
    title: String,
    description: String,
    icon: Icon,
    actionPerformed: (e: AnActionEvent) -> Unit,
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    update: (e: AnActionEvent) -> Unit = { _ -> },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit
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
}
