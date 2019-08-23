package arrow.meta.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

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

/**
 * The compilation-level Gradle plugin for applying the compiler plugin to the Kotlin compiler configuration.
 */
class ArrowKotlinGradleSubplugin : KotlinGradleSubplugin<AbstractCompile> {
    companion object {
        private const val GENERATED_ARTIFACT_NAME = "gradle-plugin"
        private const val GENERATED_GROUP_ID = "io.arrow-kt"
        private const val GENERATED_VERSION = "0.0.1"
        private const val GENERATED_COMPILER_PLUGIN_ID = "arrow.meta.plugin.compiler"
    }

    override fun isApplicable(project: Project, task: AbstractCompile) = ArrowGradlePlugin.isEnabled(project)

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
    ): List<SubpluginOption> = emptyList()

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(GENERATED_GROUP_ID, GENERATED_ARTIFACT_NAME, GENERATED_VERSION)

    override fun getCompilerPluginId() = GENERATED_COMPILER_PLUGIN_ID
}
