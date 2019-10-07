package arrow.meta.quotes

import org.jetbrains.kotlin.psi.KtElement

data class ScopedList<K : KtElement>(
  val value: List<K>,
  val prefix: String = "",
  val separator: String = ", ",
  val postfix: String = "",
  val forceRenderSurroundings: Boolean = false
) {
  override fun toString(): String =
    if (value.isEmpty())
      if (forceRenderSurroundings) prefix + postfix
      else ""
    else value.filterNot { it.text == "null" }.joinToString( //some java values
      separator = separator,
      prefix = prefix,
      postfix = postfix
    ) { it.text }

  companion object {
    operator fun <K : KtElement> invoke(
      value: List<Scope<K>>,
      prefix: String = "",
      separator: String = ", ",
      postfix: String = "",
      forceRenderSurroundings: Boolean = false
    ): ScopedList<K> =
      ScopedList(
        prefix= prefix,
        separator =  separator,
        postfix = postfix,
        forceRenderSurroundings = forceRenderSurroundings,
        value = value.map { it }
      )
  }
}
