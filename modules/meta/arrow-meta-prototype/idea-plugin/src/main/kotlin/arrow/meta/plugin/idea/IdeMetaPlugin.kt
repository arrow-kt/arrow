package arrow.meta.plugin.idea

import arrow.meta.MetaPlugin
import arrow.meta.dsl.ide.IdeSyntax
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.internal.registry.IdeInternalRegistry
import com.intellij.codeInsight.intention.PriorityAction
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtNamedFunction

class IdeMetaPlugin : MetaPlugin(), IdeInternalRegistry, IdeSyntax {
  override fun intercept(): List<Pair<Name, List<ExtensionPhase>>> {
    return super.intercept() + t
  }
}

val IdeMetaPlugin.t: Pair<Name, List<ExtensionPhase>>
  get() = Name.identifier("IntentionTest") to
    meta(
      /*addIcon(
        icon = KotlinIcons.SUSPEND_CALL,
        matchOn = { psiElement, flag ->
          psiElement.safeAs<KtNamedFunction>()?.name == "foo"
        }
      ),*/
      addIntention(
        intention = addKtIntention(
          text = "TestIntention",
          kClass = KtNamedFunction::class.java,
          isApplicableTo = { element, caretOffset ->
            element.name == "foo"
          },
          applyTo = { element, editor ->
            element.setName("CHANGED")
          },
          priority = PriorityAction.Priority.HIGH
        )
      )
      ,
      unregisterIntention(
        intention = availableIntentions().first()
      )
    )
