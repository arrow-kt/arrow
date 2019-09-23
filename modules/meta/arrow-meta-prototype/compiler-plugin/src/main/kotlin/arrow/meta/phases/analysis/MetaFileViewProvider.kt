package arrow.meta.phases.analysis

import org.jetbrains.kotlin.com.intellij.openapi.editor.Document
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.psi.SingleRootFileViewProvider

class MetaFileViewProvider(
  psiManager: PsiManager,
  virtualFile: VirtualFile,
  val transformation: (Document?) -> Document?
) : SingleRootFileViewProvider(psiManager, virtualFile) {
  override fun getDocument(): Document? = transformation(super.getDocument())
}