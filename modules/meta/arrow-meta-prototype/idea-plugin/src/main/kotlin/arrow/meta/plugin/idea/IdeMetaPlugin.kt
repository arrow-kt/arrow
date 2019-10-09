package arrow.meta.plugin.idea

import arrow.meta.MetaPlugin
import arrow.meta.Plugin
import arrow.meta.dsl.ide.IdeSyntax
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.plugin.idea.internal.registry.IdeInternalRegistry
import org.jetbrains.kotlin.idea.KotlinIcons
import org.jetbrains.kotlin.psi.KtThrowExpression
import kotlin.contracts.ExperimentalContracts

class IdeMetaPlugin : MetaPlugin(), IdeInternalRegistry, IdeSyntax {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<Plugin> {
    return super.intercept(ctx) + icon
  }
}

val IdeMetaPlugin.icon: Plugin
  get() = "ImpureLineMarker" {
    meta(
      addLineMarkerProvider(
        icon = KotlinIcons.SUSPEND_CALL,
        matchOn = {
          it is KtThrowExpression
        },
        message = "KtThrow LineMarker Example"
      )
    )
  }
