package arrow.meta.plugin.idea

import com.intellij.compiler.server.BuildProcessParametersProvider

class MetaBuildProcessParametersProvider : BuildProcessParametersProvider() {
  override fun getVMArguments(): MutableList<String> {
    println("MetaBuildProcessParametersProvider.getVMArguments")
    return super.getVMArguments()
  }

  override fun getLauncherClassPath(): MutableList<String> =
    super.getLauncherClassPath().also {
      println("MetaBuildProcessParametersProvider.getLauncherClassPath: $it")
    }

  override fun isProcessPreloadingEnabled(): Boolean =
    super.isProcessPreloadingEnabled().also {
      println("MetaBuildProcessParametersProvider.isProcessPreloadingEnabled: $it")
    }

  override fun getClassPath(): MutableList<String> =
    super.getClassPath().also {
      println("MetaBuildProcessParametersProvider.getClassPath: $it")
    }
}