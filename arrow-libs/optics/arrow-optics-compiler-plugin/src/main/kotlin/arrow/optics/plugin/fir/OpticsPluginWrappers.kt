@file:OptIn(ExperimentalCompilerApi::class)

package arrow.optics.plugin.fir

import arrow.optics.plugin.ir.OpticsIrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitCLP
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

private const val PLUGIN_ID = "arrow.optics.plugin"

@Suppress("unused") // Used via reflection.
class OpticsCommandLineProcessor : DevKitCLP {
  // override val pluginId: String get() = PLUGIN_ID
  override val pluginOptions: Collection<CliOption> get() = emptyList()

  override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
    error("Unexpected config option: '${option.optionName}'")
  }
}

class OpticsPluginComponentRegistrarForTests : CompilerPluginRegistrar() {
  override val supportsK2: Boolean get() = true
  override val pluginId: String get() = PLUGIN_ID

  override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
    registerBothExtensions()
  }
}

internal class OpticsPluginComponentRegistrar : DevKitComponentRegistrar {
  override fun CompilerPluginRegistrar.ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
    registerBothExtensions()
  }
}

internal fun CompilerPluginRegistrar.ExtensionStorage.registerBothExtensions() {
  FirExtensionRegistrarAdapter.registerExtension(OpticsPluginRegistrar())
  IrGenerationExtension.registerExtension(OpticsIrGenerationExtension())
}

class OpticsPluginRegistrar : FirExtensionRegistrar() {
  override fun ExtensionRegistrarContext.configurePlugin() {
    +::OpticsCompanionGenerator
    +::OpticsDslGenerator
    +::OpticsCopyGenerator
    +::OpticsCheckersExtension
  }
}
