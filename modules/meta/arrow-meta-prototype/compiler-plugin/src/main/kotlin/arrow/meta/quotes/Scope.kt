package arrow.meta.quotes

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtElement

open class Scope<out K : KtElement>(open val value: K?) {

  operator fun <K: KtElement> ScopedList<K>.rangeTo(other: String): Name =
    Name.identifier((value.map { it.text } + other).joinToString(", "))

  override fun toString(): String =
    if (value != null) value?.text ?: "" else "" //java null snicking in
}
