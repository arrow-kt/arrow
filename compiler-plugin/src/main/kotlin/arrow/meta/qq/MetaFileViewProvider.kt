package arrow.meta.qq

import com.intellij.openapi.editor.Document
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.SingleRootFileViewProvider

class MetaFileViewProvider(
  psiManager: PsiManager,
  virtualFile: VirtualFile,
  val transformation: (Document?) -> Document?
) : SingleRootFileViewProvider(psiManager, virtualFile) {
  override fun getDocument(): Document? = transformation(super.getDocument())
}