package arrow.core.extensions.list.monoidal

import arrow.core.extensions.ListKMonoidal
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("identity")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("emptyList<A>()"))
fun <A> identity(): List<A> = arrow.core.extensions.list.monoidal.List
   .monoidal()
   .identity<A>() as kotlin.collections.List<A>

/**
 * cached extension
 */
@PublishedApi()
internal val monoidal_singleton: ListKMonoidal = object : arrow.core.extensions.ListKMonoidal {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Monoidal typeclasses is deprecated. Use concrete methods on List")
  inline fun monoidal(): ListKMonoidal = monoidal_singleton}
