package arrow.meta.plugin.idea

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.highlighter.KotlinPsiChecker
import org.jetbrains.kotlin.psi.KtParameter

class MetaKotlinPsiChecker : KotlinPsiChecker() {
  override fun annotate(element: PsiElement, holder: AnnotationHolder) {
    //println("MetaKotlinPsiChecker.annotate: $element")
    super.annotate(element, holder)
  }

  override fun isForceHighlightParents(file: PsiFile): Boolean {
    //println("MetaKotlinPsiChecker.isForceHighlightParents: $file")
    return super.isForceHighlightParents(file)
  }

  override fun shouldSuppressUnusedParameter(parameter: KtParameter): Boolean {
    //println("MetaKotlinPsiChecker.shouldSuppressUnusedParameter: $parameter")
    return super.shouldSuppressUnusedParameter(parameter)
  }
}