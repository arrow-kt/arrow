package arrow.core.extensions.ior.eqK

import arrow.Kind
import arrow.core.ForIor
import arrow.core.Ior.Companion
import arrow.core.extensions.IorEqK
import arrow.typeclasses.Eq

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
fun <A> Kind<Kind<ForIor, A>, A>.eqK(
  EQA: Eq<A>,
  arg1: Kind<Kind<ForIor, A>, A>,
  arg2: Eq<A>
): Boolean = arrow.core.Ior.eqK<A>(EQA).run {
  this@eqK.eqK<A>(arg1, arg2) as kotlin.Boolean
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
fun <A> liftEq(EQA: Eq<A>, arg0: Eq<A>): Eq<Kind<Kind<ForIor, A>, A>> = arrow.core.Ior
   .eqK<A>(EQA)
   .liftEq<A>(arg0) as arrow.typeclasses.Eq<arrow.Kind<arrow.Kind<arrow.core.ForIor, A>, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0",
  level = DeprecationLevel.WARNING
)
inline fun <A> Companion.eqK(EQA: Eq<A>): IorEqK<A> = object : arrow.core.extensions.IorEqK<A> {
    override fun EQA(): arrow.typeclasses.Eq<A> = EQA }
