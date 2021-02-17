package arrow.core.extensions.const.eqK

import arrow.Kind
import arrow.core.Const.Companion
import arrow.core.ForConst
import arrow.core.extensions.ConstEqK
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Suppress
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
    "eqv(EQA, arg1)",
    "arrow.core.eqv"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.eqK(
  EQA: Eq<A>,
  arg1: Kind<Kind<ForConst, A>, A>,
  arg2: Eq<A>
): Boolean = arrow.core.Const.eqK<A>(EQA).run {
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
fun <A> liftEq(EQA: Eq<A>, arg0: Eq<A>): Eq<Kind<Kind<ForConst, A>, A>> = arrow.core.Const
  .eqK<A>(EQA)
  .liftEq<A>(arg0) as arrow.typeclasses.Eq<arrow.Kind<arrow.Kind<arrow.core.ForConst, A>, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
inline fun <A> Companion.eqK(EQA: Eq<A>): ConstEqK<A> = object : arrow.core.extensions.ConstEqK<A> {
  override fun EQA(): arrow.typeclasses.Eq<A> = EQA
}
