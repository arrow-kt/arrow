package arrow.meta.phases.analysis

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile

interface PreprocessedVirtualFileFactory : ExtensionPhase {
  fun CompilerContext.isPassThrough(): Boolean
  fun CompilerContext.createPreprocessedFile(file: VirtualFile?): VirtualFile?
  fun CompilerContext.createPreprocessedLightFile(file: LightVirtualFile?): LightVirtualFile?
}