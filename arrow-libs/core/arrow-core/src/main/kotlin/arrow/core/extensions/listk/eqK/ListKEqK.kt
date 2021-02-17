package arrow.core.extensions.listk.eqK

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK.Companion
import arrow.core.extensions.ListKEqK
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val eqK_singleton: ListKEqK = object : arrow.core.extensions.ListKEqK {}

@JvmName("eqK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <A> Kind<ForListK, A>.eqK(arg1: Kind<ForListK, A>, arg2: Eq<A>): Boolean =
  arrow.core.ListK.eqK().run {
    this@eqK.eqK<A>(arg1, arg2) as kotlin.Boolean
  }

@JvmName("liftEq")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <A> liftEq(arg0: Eq<A>): Eq<Kind<ForListK, A>> = arrow.core.ListK
  .eqK()
  .liftEq<A>(arg0) as arrow.typeclasses.Eq<arrow.Kind<arrow.core.ForListK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
inline fun Companion.eqK(): ListKEqK = eqK_singleton
