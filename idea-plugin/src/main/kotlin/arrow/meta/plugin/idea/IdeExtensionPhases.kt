package arrow.meta.plugin.idea

import arrow.meta.extensions.CompilerContext
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.utils.Noop
import arrow.meta.utils.ide
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement

interface IdeExtensionPhase : ExtensionPhase {

  interface EditorHighlightErrorFilter : IdeExtensionPhase {
    fun CompilerContext.shouldHighlightErrorElement(psiErrorElement: PsiErrorElement): Boolean
  }

}

fun highlight(
  shouldHighlightErrorElement: CompilerContext.(shouldHighlightErrorElement: PsiElement) -> Boolean = Noop.boolean2True
): ExtensionPhase =
  ide {
    object : IdeExtensionPhase.EditorHighlightErrorFilter {
      override fun CompilerContext.shouldHighlightErrorElement(psiErrorElement: PsiErrorElement): Boolean =
        shouldHighlightErrorElement(psiErrorElement)
    }
  } ?: ExtensionPhase.Empty