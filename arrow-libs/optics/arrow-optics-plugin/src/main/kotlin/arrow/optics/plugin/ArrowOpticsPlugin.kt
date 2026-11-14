package arrow.optics.plugin

import org.gradle.api.Project
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitSupportPlugin
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

@Suppress("unused")
class ArrowOpticsPlugin : DevKitSupportPlugin(PluginInfo.PLUGIN_INFO) {
  override fun Project.applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
    kotlinCompilation.compileTaskProvider.configure {
      // Run this compiler plugin before Compose plugin.
      it.compilerOptions.freeCompilerArgs.add("-Xcompiler-plugin-order=${PluginInfo.PLUGIN_ID}>androidx.compose.compiler.plugins.kotlin")
    }
    return providers.provider { emptyList() }
  }
}
