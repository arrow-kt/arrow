package arrow.meta.plugin.idea

import arrow.meta.plugins.typeclasses.hasExtensionDefaultValue
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.highlighter.KotlinPsiChecker
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

class MetaKotlinPsiChecker : KotlinPsiChecker() {

  override fun annotate(element: PsiElement, holder: AnnotationHolder) {
    element.safeAs<KtParameter>()?.let { ktParameter ->
      if (ktParameter.hasExtensionDefaultValue()) {
        println("MetaKotlinPsiChecker.ktParameter found: $ktParameter")
      }
    }
//    println("MetaKotlinPsiChecker.annotate: $element")
    super.annotate(element, holder)
  }

  override fun isForceHighlightParents(file: PsiFile): Boolean {
//    println("MetaKotlinPsiChecker.isForceHighlightParents: $file")
    return super.isForceHighlightParents(file)
  }

  override fun shouldSuppressUnusedParameter(parameter: KtParameter): Boolean {
//    println("MetaKotlinPsiChecker.shouldSuppressUnusedParameter: $parameter")
    return super.shouldSuppressUnusedParameter(parameter)
  }
}