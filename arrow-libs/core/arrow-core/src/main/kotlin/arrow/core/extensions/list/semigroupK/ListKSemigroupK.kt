package arrow.core.extensions.list.semigroupK

import arrow.Kind
import arrow.core.ForListK
import arrow.core.extensions.ListKSemigroupK
import arrow.typeclasses.Semigroup
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A> List<A>.combineK(arg1: List<A>): List<A> =
  arrow.core.extensions.list.semigroupK.List.semigroupK().run {
    arrow.core.ListK(this@combineK).combineK<A>(arrow.core.ListK(arg1)) as kotlin.collections.List<A>
  }

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("listMonoid<A>()", "arrow.core.listMonoid"))
fun <A> algebra(): Semigroup<Kind<ForListK, A>> = arrow.core.extensions.list.semigroupK.List
  .semigroupK()
  .algebra<A>() as arrow.typeclasses.Semigroup<arrow.Kind<arrow.core.ForListK, A>>

/**
 * cached extension
 */
@PublishedApi()
internal val semigroupK_singleton: ListKSemigroupK = object : arrow.core.extensions.ListKSemigroupK
{}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
  inline fun semigroupK(): ListKSemigroupK = semigroupK_singleton
}
