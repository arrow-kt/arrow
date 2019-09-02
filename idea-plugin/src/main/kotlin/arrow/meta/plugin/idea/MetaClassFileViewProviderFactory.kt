package arrow.meta.plugin.idea

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.FileViewProviderFactory
import com.intellij.psi.PsiManager

class MetaClassFileViewProviderFactory(val delegate: FileViewProviderFactory) : FileViewProviderFactory by delegate {
  override fun createFileViewProvider(file: VirtualFile, language: Language, manager: PsiManager, eventSystemEnabled: Boolean): FileViewProvider {
    println("MetaClassFileViewProviderFactory.createFileViewProvider")
    return delegate.createFileViewProvider(file, language, manager, eventSystemEnabled)
  }
}