package arrow.meta.plugin.idea

import arrow.meta.MetaPlugin
import arrow.meta.Plugin
import arrow.meta.dsl.ide.IdeSyntax
import arrow.meta.phases.CompilerContext
import arrow.meta.plugin.idea.internal.registry.IdeInternalRegistry
import arrow.meta.plugin.idea.plugins.comprehensions.comprehensionsIdePlugin
import arrow.meta.plugin.idea.plugins.higherkinds.higherKindsIdePlugin
import arrow.meta.plugin.idea.plugins.initial.initialIdeSetUp
import arrow.meta.plugin.idea.plugins.nothing.nothingIdePlugin
import arrow.meta.plugin.idea.plugins.optics.opticsIdePlugin
import kotlin.contracts.ExperimentalContracts

class IdeMetaPlugin : MetaPlugin(), IdeInternalRegistry, IdeSyntax {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<Plugin> =
    super.intercept(ctx) +
      initialIdeSetUp +
      nothingIdePlugin +
      comprehensionsIdePlugin +
      opticsIdePlugin +
      higherKindsIdePlugin
}
