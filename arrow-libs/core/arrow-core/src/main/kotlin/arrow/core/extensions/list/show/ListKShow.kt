package arrow.core.extensions.list.show

import arrow.core.extensions.ListKShow
import arrow.core.k
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("show")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ShowDeprecation, ReplaceWith("toString()"))
fun <A> List<A>.show(SA: Show<A>): String =
  k().show(SA)

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(ShowDeprecation)
  inline fun <A> show(SA: Show<A>): ListKShow<A> = object : arrow.core.extensions.ListKShow<A> {
    override fun SA(): arrow.typeclasses.Show<A> = SA
  }
}
