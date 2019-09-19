package arrow.meta.quotes

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtElement

open class Scope<K : KtElement>(open val value: K) {

  operator fun <K: KtElement> List<Scope<K>>.rangeTo(other: String): Name =
    Name.identifier((map { it.value.text } + other).joinToString(", "))

  override fun toString(): String =
    value.text
}
