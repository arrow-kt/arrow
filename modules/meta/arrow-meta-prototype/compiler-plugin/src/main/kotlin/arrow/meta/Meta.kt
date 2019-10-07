package arrow.meta

import arrow.meta.dsl.MetaPluginSyntax
import arrow.meta.internal.registry.InternalRegistry
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

data class Plugin(
  val name: String,
  val meta: CompilerContext.() -> List<ExtensionPhase>
)

operator fun String.invoke(phases: CompilerContext.() -> List<ExtensionPhase>): Plugin =
  Plugin(this, phases)

interface Meta : ComponentRegistrar, MetaPluginSyntax, InternalRegistry {

  override fun intercept(ctx: CompilerContext): List<Plugin>

  override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) =
    super.registerProjectComponents(project, configuration)

}
