package arrow.meta.extensions

import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor

class MetaCommandLineProcessor : CommandLineProcessor {

  override val pluginId: String = "arrow.meta.compiler.plugin"

  override val pluginOptions: Collection<CliOption> = emptyList()

}
