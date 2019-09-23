package arrow.meta.plugin.idea

import arrow.meta.MetaPlugin
import arrow.meta.dsl.ide.IdeSyntax
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.internal.registry.IdeInternalRegistry
import org.jetbrains.kotlin.idea.KotlinIcons
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

class IdeMetaPlugin : MetaPlugin(), IdeInternalRegistry, IdeSyntax {
  override fun intercept(): List<Pair<Name, List<ExtensionPhase>>> {
    return super.intercept() + t
  }
}

val IdeMetaPlugin.t: Pair<Name, List<ExtensionPhase>>
  get() = Name.identifier("ToBla intention") to
    meta(
      addIcon(
        icon = KotlinIcons.SUSPEND_CALL,
        matchOn = { psiElement, flag ->
          psiElement.safeAs<KtNamedFunction>()?.name == "foo"
        }
      )
    )
