package arrow.meta.plugin.idea

import arrow.meta.MetaPlugin
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.platform.TargetPlatform
import java.util.concurrent.atomic.AtomicBoolean

val metaPlugin = MetaPlugin()

private val registered = AtomicBoolean(false)

class MetaStorageComponentContainerContributor : StorageComponentContainerContributor {

  override fun registerModuleComponents(container: StorageComponentContainer, platform: TargetPlatform, moduleDescriptor: ModuleDescriptor) {
    registerIdeStack()
    super.registerModuleComponents(container, platform, moduleDescriptor)
  }

  @Synchronized
  private fun registerIdeStack() {
    if (!registered.get()) {
      val project = currentProject()
      if (project != null) {
        val configuration = CompilerConfiguration()
        metaPlugin.registerIdeProjectComponents(project, configuration)
        println("registerIdeProjectComponents DONE")
        registered.set(true)
      }
    }
  }
}

fun currentProject(): Project? =
  ProjectManager.getInstance().openProjects.firstOrNull { project ->
    val window = WindowManager.getInstance().suggestParentWindow(project)
    (window != null && window.isActive)
  }

fun Project.buildFolders(): List<VirtualFile> =
  ModuleManager.getInstance(this).modules
    .flatMap { ModuleRootManager.getInstance(it).excludeRoots.toList() }
    .filter { it.name == "build" }

