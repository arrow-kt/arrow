package arrow.optics.plugin.fir

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@Suppress("unused") // Used via reflection.
class OpticsCommandLineProcessor : CommandLineProcessor {
  override val pluginId: String = "arrow.optics.plugin"
  override val pluginOptions: Collection<CliOption> = emptyList()

  override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
    error("Unexpected config option: '${option.optionName}'")
  }
}

class OpticsPluginComponentRegistrar : CompilerPluginRegistrar() {
  override val supportsK2: Boolean
    get() = true

  override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
    FirExtensionRegistrarAdapter.registerExtension(OpticsPluginRegistrar())
  }
}

class OpticsPluginRegistrar : FirExtensionRegistrar() {
  override fun ExtensionRegistrarContext.configurePlugin() {
    +::OpticsCompanionGenerator
  }
}
