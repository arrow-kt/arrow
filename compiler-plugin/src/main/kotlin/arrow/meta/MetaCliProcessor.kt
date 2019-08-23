package arrow.meta

import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor

//@AutoService(CommandLineProcessor::class)
class MetaCliProcessor : CommandLineProcessor {

  override val pluginId: String = "arrow.meta.compiler.plugin"

  override val pluginOptions: Collection<CliOption> = emptyList()

}
