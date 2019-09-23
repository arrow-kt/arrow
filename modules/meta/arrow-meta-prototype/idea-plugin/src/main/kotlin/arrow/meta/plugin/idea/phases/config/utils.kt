package arrow.meta.plugin.idea.phases.config

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager

fun currentProject(): Project? =
  ProjectManager.getInstance().openProjects.firstOrNull { project ->
    val window = WindowManager.getInstance().suggestParentWindow(project)
    (window != null && window.isActive)
  }

fun Project.buildFolders(): List<VirtualFile> =
  ModuleManager.getInstance(this).modules
    .flatMap { ModuleRootManager.getInstance(it).excludeRoots.toList() }
    .filter { it.name == "build" }

