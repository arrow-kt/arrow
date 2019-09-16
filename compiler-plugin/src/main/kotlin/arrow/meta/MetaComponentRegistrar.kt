package arrow.meta

import arrow.meta.dsl.MetaPluginSyntax
import arrow.meta.internal.registry.InternalRegistry
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.name.Name

interface MetaComponentRegistrar : ComponentRegistrar, MetaPluginSyntax, InternalRegistry {

  override fun intercept(): List<Pair<Name, List<ExtensionPhase>>>

  override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) =
    super.registerProjectComponents(project, configuration)

}
