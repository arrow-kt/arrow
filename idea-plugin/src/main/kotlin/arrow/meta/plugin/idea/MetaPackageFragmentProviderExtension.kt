package arrow.meta.plugin.idea

import arrow.meta.MetaPlugin
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.idea.compiler.configuration.KotlinCompilerSettings
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.PackageFragmentProviderExtension
import org.jetbrains.kotlin.storage.StorageManager

val metaPlugin = MetaPlugin()

class MetaPackageFragmentProviderExtension : PackageFragmentProviderExtension {
  private var registered = false
  override fun getPackageFragmentProvider(
    project: Project,
    module: ModuleDescriptor,
    storageManager: StorageManager,
    trace: BindingTrace,
    moduleInfo: ModuleInfo?,
    lookupTracker: LookupTracker
  ): PackageFragmentProvider? {
    println("MetaPackageFragmentProviderExtension.getPackageFragmentProvider")
    if (!registered) {
      val compilerSettings = project.service<KotlinCompilerSettings>()
      val configuration = CompilerConfiguration()
      metaPlugin.registerIdeProjectComponents(project, configuration)
      registered = true
      println("MetaPackageFragmentProviderExtension.registerIdeProjectComponents DONE")
    }
    return null
  }
}