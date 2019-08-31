package arrow.meta.plugin.idea

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.completion.CompletionInformationProvider

class MetaCompletionInformationProvider : CompletionInformationProvider {
  override fun getContainerAndReceiverInformation(descriptor: DeclarationDescriptor): String? {
    println("MetaCompletionInformationProvider.getContainerAndReceiverInformation $descriptor")
    return null
  }
}