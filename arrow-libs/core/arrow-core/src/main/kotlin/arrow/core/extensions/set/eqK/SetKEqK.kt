package arrow.core.extensions.set.eqK

import arrow.Kind
import arrow.core.ForSetK
import arrow.core.extensions.SetKEqK
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Set
import kotlin.jvm.JvmName

@JvmName("eqK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "eqv(arg2, arg1)",
    "arrow.core.eqv"
  ),
  DeprecationLevel.WARNING
)
fun <A> Set<A>.eqK(arg1: Set<A>, arg2: Eq<A>): Boolean =
  arrow.core.extensions.set.eqK.Set.eqK().run {
    arrow.core.SetK(this@eqK).eqK<A>(arrow.core.SetK(arg1), arg2) as kotlin.Boolean
  }

@JvmName("liftEq")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <A> liftEq(arg0: Eq<A>): Eq<Kind<ForSetK, A>> = arrow.core.extensions.set.eqK.Set
  .eqK()
  .liftEq<A>(arg0) as arrow.typeclasses.Eq<arrow.Kind<arrow.core.ForSetK, A>>

/**
 * cached extension
 */
@PublishedApi()
internal val eqK_singleton: SetKEqK = object : arrow.core.extensions.SetKEqK {}

object Set {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
  inline fun eqK(): SetKEqK = eqK_singleton
}
