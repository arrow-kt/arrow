package arrow.meta.plugin.idea

import arrow.meta.MetaPlugin
import arrow.meta.dsl.ide.IdeSyntax
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.internal.registry.IdeInternalRegistry
import org.jetbrains.kotlin.idea.KotlinIcons
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtThrowExpression

class IdeMetaPlugin : MetaPlugin(), IdeInternalRegistry, IdeSyntax {
  override fun intercept(): List<Pair<Name, List<ExtensionPhase>>> {
    return super.intercept() + icon
  }
}

val IdeMetaPlugin.icon: Pair<Name, List<ExtensionPhase>>
  get() = Name.identifier("ImpureLineMarker") to
    meta(
      addLineMarkerProvider(
        matchOn = {
          it is KtThrowExpression
        },
        slowLineMarker = {
          addLineMarkerInfo(
            KotlinIcons.SUSPEND_CALL,
            it,
            message = "KtThrow LineMarker Example"
          )
        }
      )
    )
