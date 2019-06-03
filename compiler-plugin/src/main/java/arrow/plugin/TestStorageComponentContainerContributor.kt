package arrow.plugin

import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.diagnostics.reportFromPlugin
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.TargetPlatform
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext

// This class can be used to check for certain pre-conditions.
// In this example we bail in check if any class is OPEN or ABSTRACT.
class TestStorageComponentContainerContributor : DeclarationChecker, StorageComponentContainerContributor {

  override fun registerModuleComponents(container: StorageComponentContainer, platform: TargetPlatform, moduleDescriptor: ModuleDescriptor) {
    container.useInstance(TestStorageComponentContainerContributor())
  }

  override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
    if (descriptor !is ClassDescriptor || declaration !is KtClass) return
    if (descriptor.kind != ClassKind.CLASS) return

    if (declaration.hasModifier(KtTokens.OPEN_KEYWORD) || declaration.hasModifier(KtTokens.ABSTRACT_KEYWORD)) {
      val element = declaration.nameIdentifier ?: declaration
      context.trace.reportFromPlugin(ARROW_ERROR.on(element), ArrowDefaultErrorMessages)
    }
  }
}
