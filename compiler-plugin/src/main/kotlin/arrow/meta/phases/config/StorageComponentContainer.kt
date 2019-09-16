package arrow.meta.phases.config

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext

interface StorageComponentContainer : ExtensionPhase {
  fun CompilerContext.registerModuleComponents(
    container: org.jetbrains.kotlin.container.StorageComponentContainer,
    moduleDescriptor: ModuleDescriptor
  ): Unit

  fun CompilerContext.check(
    declaration: KtDeclaration,
    descriptor: DeclarationDescriptor,
    context: DeclarationCheckerContext
  ): Unit
}