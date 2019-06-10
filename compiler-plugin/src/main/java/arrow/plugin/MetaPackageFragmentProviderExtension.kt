package arrow.plugin

import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.PackageFragmentProviderExtension
import org.jetbrains.kotlin.storage.StorageManager

class MetaPackageFragmentProviderExtension : PackageFragmentProviderExtension {
  override fun getPackageFragmentProvider(
    project: Project,
    module: ModuleDescriptor,
    storageManager: StorageManager,
    trace: BindingTrace,
    moduleInfo: ModuleInfo?,
    lookupTracker: LookupTracker
  ): PackageFragmentProvider? {
    println("PackageFragmentProviderExtension.getPackageFragmentProvider")
    return null
  }
}