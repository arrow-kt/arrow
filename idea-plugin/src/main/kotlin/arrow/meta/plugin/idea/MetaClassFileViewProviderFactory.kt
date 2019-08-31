package arrow.meta.plugin.idea

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.ClassFileViewProvider
import com.intellij.psi.FileViewProvider
import com.intellij.psi.FileViewProviderFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.compiled.ClassFileDecompilers

class MetaClassFileViewProviderFactory : FileViewProviderFactory {
  override fun createFileViewProvider(file: VirtualFile, language: Language, manager: PsiManager, eventSystemEnabled: Boolean): FileViewProvider {
    println("MetaClassFileViewProviderFactory.createFileViewProvider")
    val decompiler = ClassFileDecompilers.find(file)
    return if (decompiler is ClassFileDecompilers.Full) {
      decompiler.createFileViewProvider(file, manager, eventSystemEnabled)
    } else ClassFileViewProvider(manager, file, eventSystemEnabled)
  }
}