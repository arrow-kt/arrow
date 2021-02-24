package arrow.core.extensions.list.eqK

import arrow.Kind
import arrow.core.ForListK
import arrow.core.extensions.ListKEqK
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("eqK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <A> List<A>.eqK(arg1: List<A>, arg2: Eq<A>): Boolean =
  arrow.core.extensions.list.eqK.List.eqK().run {
    arrow.core.ListK(this@eqK).eqK<A>(arrow.core.ListK(arg1), arg2) as kotlin.Boolean
  }

@JvmName("liftEq")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <A> liftEq(arg0: Eq<A>): Eq<Kind<ForListK, A>> = arrow.core.extensions.list.eqK.List
  .eqK()
  .liftEq<A>(arg0) as arrow.typeclasses.Eq<arrow.Kind<arrow.core.ForListK, A>>

/**
 * cached extension
 */
@PublishedApi()
internal val eqK_singleton: ListKEqK = object : arrow.core.extensions.ListKEqK {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
  inline fun eqK(): ListKEqK = eqK_singleton
}
