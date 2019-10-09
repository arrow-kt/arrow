package arrow.meta.plugin.idea

import arrow.meta.MetaPlugin
import arrow.meta.Plugin
import arrow.meta.dsl.ide.IdeSyntax
import arrow.meta.phases.CompilerContext
import arrow.meta.plugin.idea.internal.registry.IdeInternalRegistry
import arrow.meta.plugin.idea.plugins.dummy.dummyIdePlugin
import kotlin.contracts.ExperimentalContracts

class IdeMetaPlugin : MetaPlugin(), IdeInternalRegistry, IdeSyntax {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<Plugin> {
    return super.intercept(ctx) + dummyIdePlugin
  }
}
