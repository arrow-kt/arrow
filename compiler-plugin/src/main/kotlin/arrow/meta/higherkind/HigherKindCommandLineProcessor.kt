package arrow.meta.higherkind

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor

@AutoService(CommandLineProcessor::class)
class HigherKindCommandLineProcessor : CommandLineProcessor {

  override val pluginId: String = "arrow.meta.plugin.compiler"

  override val pluginOptions: Collection<CliOption> = emptyList()

}
