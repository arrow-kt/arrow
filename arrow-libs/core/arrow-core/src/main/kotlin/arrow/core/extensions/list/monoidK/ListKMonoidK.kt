package arrow.core.extensions.list.monoidK

import arrow.Kind
import arrow.core.ForListK
import arrow.core.extensions.ListKMonoidK
import arrow.typeclasses.Monoid
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("listMonoid<A>()", "arrow.core.listMonoid"))
fun <A> algebra(): Monoid<Kind<ForListK, A>> = arrow.core.extensions.list.monoidK.List
  .monoidK()
  .algebra<A>() as arrow.typeclasses.Monoid<arrow.Kind<arrow.core.ForListK, A>>

/**
 * cached extension
 */
@PublishedApi()
internal val monoidK_singleton: ListKMonoidK = object : arrow.core.extensions.ListKMonoidK {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
  inline fun monoidK(): ListKMonoidK = monoidK_singleton
}
