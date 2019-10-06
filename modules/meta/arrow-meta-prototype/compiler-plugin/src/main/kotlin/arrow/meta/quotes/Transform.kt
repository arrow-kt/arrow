package arrow.meta.quotes

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement

/**
 * The resulting action from matching on transformation
 */
sealed class Transform<out K : KtElement> {

  data class Replace<out K : KtElement>(
    val replacing: PsiElement,
    val newDeclarations: List<Scope<KtElement>>,
    val replacementId: String? = null
  ) : Transform<K>()

  object Empty : Transform<Nothing>()

  companion object {
    fun <K : KtElement> replace(
      replacing: PsiElement,
      newDeclarations: List<Scope<KtElement>>
    ): Transform<K> =
      Replace(replacing, newDeclarations)

    fun <K : KtElement> replace(
      replacing: PsiElement,
      newDeclaration: Scope<KtElement>
    ): Transform<K> =
      replace(replacing, listOf(newDeclaration))

    fun <K : KtElement> remove(replacing: PsiElement): Transform<K> =
      replace(replacing, emptyList())

    val empty: Transform<Nothing> = Empty
  }

}
