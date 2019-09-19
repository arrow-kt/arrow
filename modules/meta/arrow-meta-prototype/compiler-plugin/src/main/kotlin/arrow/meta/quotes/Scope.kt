package arrow.meta.quotes

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtElement

open class Scope<K : KtElement>(open val value: K, open val context: QuasiQuoteContext) {

  operator fun <K: KtElement> ScopedList<K>.rangeTo(other: String): Name =
    Name.identifier((value.map { it.text } + other).joinToString(", "))

  override fun toString(): String =
    value.text
}
