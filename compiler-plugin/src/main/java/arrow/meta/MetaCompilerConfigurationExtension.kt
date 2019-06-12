package arrow.meta

import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.CompilerConfigurationExtension

class MetaCompilerConfigurationExtension : CompilerConfigurationExtension {
  override fun updateConfiguration(configuration: CompilerConfiguration) {
    println("CompilerConfigurationExtension.updateConfiguration")
  }
}
