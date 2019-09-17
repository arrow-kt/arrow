package arrow.meta.phases.config

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.config.CompilerConfiguration

interface Config : ExtensionPhase {
  fun CompilerContext.updateConfiguration(configuration: CompilerConfiguration): Unit
}