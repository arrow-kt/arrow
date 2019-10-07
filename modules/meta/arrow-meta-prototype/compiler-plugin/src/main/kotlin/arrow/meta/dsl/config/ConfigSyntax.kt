package arrow.meta.dsl.config

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.config.Config
import arrow.meta.dsl.platform.cli
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext

interface ConfigSyntax {
  fun updateConfig(updateConfiguration: CompilerContext.(configuration: CompilerConfiguration) -> Unit): Config =
    object : Config {
      override fun CompilerContext.updateConfiguration(configuration: CompilerConfiguration) {
        updateConfiguration(configuration)
      }
    }

  fun storageComponent(
    registerModuleComponents: CompilerContext.(container: StorageComponentContainer, moduleDescriptor: ModuleDescriptor) -> Unit,
    check: CompilerContext.(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) -> Unit
  ): arrow.meta.phases.config.StorageComponentContainer =
    object : arrow.meta.phases.config.StorageComponentContainer {
      override fun CompilerContext.check(
        declaration: KtDeclaration,
        descriptor: DeclarationDescriptor,
        context: DeclarationCheckerContext
      ) {
        check(declaration, descriptor, context)
      }

      override fun CompilerContext.registerModuleComponents(container: StorageComponentContainer, moduleDescriptor: ModuleDescriptor) {
        registerModuleComponents(container, moduleDescriptor)
      }
    }

  fun enableIr(): ExtensionPhase =
    cli {
      updateConfig { configuration ->
        configuration.put(JVMConfigurationKeys.IR, true)
      }
    } ?: ExtensionPhase.Empty

}
