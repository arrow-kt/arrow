package arrow.meta.quotes

import org.jetbrains.kotlin.psi.KtDeclaration

/**
 * A tree transformation given an existing ktElement
 */
data class QuoteTransformation<out K>(
  val oldDescriptor: K,
  val newDeclarations: List<KtDeclaration>
)