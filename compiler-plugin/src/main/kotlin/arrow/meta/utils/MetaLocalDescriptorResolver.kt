package arrow.meta.utils

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.lazy.LocalDescriptorResolver

class MetaLocalDescriptorResolver(val delegate: LocalDescriptorResolver): LocalDescriptorResolver by delegate {
  override fun resolveLocalDeclaration(declaration: KtDeclaration): DeclarationDescriptor {
    println("MetaLocalDescriptorResolver.resolveLocalDeclaration: $declaration")
    return delegate.resolveLocalDeclaration(declaration)
  }
}