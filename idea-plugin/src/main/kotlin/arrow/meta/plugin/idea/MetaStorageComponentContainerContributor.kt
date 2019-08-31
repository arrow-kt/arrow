package arrow.meta.plugin.idea

import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.platform.TargetPlatform

class MetaStorageComponentContainerContributor : StorageComponentContainerContributor {
  override fun registerModuleComponents(container: StorageComponentContainer, platform: TargetPlatform, moduleDescriptor: ModuleDescriptor) {
    println("MetaStorageComponentContainerContributor.registerModuleComponents")
    super.registerModuleComponents(container, platform, moduleDescriptor)
  }
}