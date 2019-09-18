package arrow.meta.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * The project-level Gradle plugin behavior that is used specifying the plugin's configuration through the
 * [ArrowExtension] class.
 */
class ArrowGradlePlugin : Plugin<Project> {
    companion object {
        fun isEnabled(project: Project) = project.plugins.findPlugin(ArrowGradlePlugin::class.java) != null

        fun getArrowExtension(project: Project): ArrowExtension {
            return project.extensions.getByType(ArrowExtension::class.java)
        }
    }

    override fun apply(project: Project) {
        project.extensions.create("arrow", ArrowExtension::class.java)
    }
}

