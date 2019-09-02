package arrow.meta.plugin.idea

import arrow.meta.MetaPlugin
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.impl.PsiManagerImpl
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.platform.TargetPlatform

val metaPlugin = MetaPlugin()

private var registered = false

class MetaStorageComponentContainerContributor : StorageComponentContainerContributor {

  override fun registerModuleComponents(container: StorageComponentContainer, platform: TargetPlatform, moduleDescriptor: ModuleDescriptor) {
    if (!registered) {
      val project = currentProject()
      if (project != null) {
        val configuration = CompilerConfiguration()
        metaPlugin.registerIdeProjectComponents(project, configuration)
        registered = true
        println("registerIdeProjectComponents DONE")
      }
    }
    super.registerModuleComponents(container, platform, moduleDescriptor)
  }
}

private fun currentProject(): Project? =
  ProjectManager.getInstance().openProjects.firstOrNull { project ->
    val window = WindowManager.getInstance().suggestParentWindow(project)
    (window != null && window.isActive)
  }
