package arrow.meta.plugin.idea

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.config.CompilerConfiguration

/**
 * This project component registers MetaPlugin for the current project.
 */
class MetaPluginRegistrarComponent(val project: Project) : ProjectComponent {
  companion object {
    val metaPlugin = IdeMetaPlugin()
    private val LOG = Logger.getInstance("#arrow.metaRegistrar")
  }

  override fun getComponentName(): String = "arrow.meta.registrar"

  override fun initComponent() {
    LOG.info("initComponent()")

    val start = System.currentTimeMillis()
    val configuration = CompilerConfiguration()
    metaPlugin.registerMetaComponents(project, configuration)

    LOG.info("initComponent() took ${System.currentTimeMillis() - start}ms")
  }

  override fun disposeComponent() {
    // TODO: make sure that all registered components are disposed
  }

  override fun projectClosed() {}

  override fun projectOpened() {}
}
