package arrow.meta.typeclasses

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor

@AutoService(CommandLineProcessor::class)
class TypeClassesCommandLineProcessor : CommandLineProcessor {

  override val pluginId: String = "arrow.meta.plugin.compiler"

  override val pluginOptions: Collection<CliOption> = emptyList()

}
