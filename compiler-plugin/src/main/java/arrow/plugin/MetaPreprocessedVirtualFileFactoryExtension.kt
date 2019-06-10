package arrow.plugin

import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.extensions.PreprocessedVirtualFileFactoryExtension

class MetaPreprocessedVirtualFileFactoryExtension : PreprocessedVirtualFileFactoryExtension {
  override fun createPreprocessedFile(file: VirtualFile?): VirtualFile? {
    println("PreprocessedVirtualFileFactoryExtension.createPreprocessedFile: ${file?.name}")
    return file
  }

  override fun createPreprocessedLightFile(file: LightVirtualFile?): LightVirtualFile? {
    println("PreprocessedVirtualFileFactoryExtension.createPreprocessedLightFile: ${file?.name}")
    return file
  }

  override fun isPassThrough(): Boolean {
    return true
  }
}
