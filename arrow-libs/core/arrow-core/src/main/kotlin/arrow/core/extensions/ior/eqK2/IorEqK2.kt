package arrow.core.extensions.ior.eqK2

import arrow.Kind
import arrow.core.ForIor
import arrow.core.Ior.Companion
import arrow.core.extensions.IorEqK2
import arrow.typeclasses.Eq

/**
 * cached extension
 */
@PublishedApi()
internal val eqK2_singleton: IorEqK2 = object : arrow.core.extensions.IorEqK2 {}

@JvmName("eqK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0",
  ReplaceWith(
    "this.eqv(arg1)"
  ),
  level = DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForIor, A>, B>.eqK(
  arg1: Kind<Kind<ForIor, A>, B>,
  arg2: Eq<A>,
  arg3: Eq<B>
): Boolean = arrow.core.Ior.eqK2().run {
  this@eqK.eqK<A, B>(arg1, arg2, arg3) as kotlin.Boolean
}

@JvmName("liftEq")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0",
  level = DeprecationLevel.WARNING
)
fun <A, B> liftEq(arg0: Eq<A>, arg1: Eq<B>): Eq<Kind<Kind<ForIor, A>, B>> = arrow.core.Ior
  .eqK2()
  .liftEq<A, B>(arg0, arg1) as arrow.typeclasses.Eq<arrow.Kind<arrow.Kind<arrow.core.ForIor, A>,
    B>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0",
  level = DeprecationLevel.WARNING
)
inline fun Companion.eqK2(): IorEqK2 = eqK2_singleton
