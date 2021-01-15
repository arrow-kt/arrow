package arrow.core.extensions.ior.eqK

import arrow.Kind
import arrow.core.ForIor
import arrow.core.Ior.Companion
import arrow.core.extensions.IorEqK
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
  "eqK(EQA, arg1, arg2)",
  "arrow.core.eqK"
  ),
  DeprecationLevel.WARNING
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
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "liftEq(EQA, arg0)",
  "arrow.core.Ior.liftEq"
  ),
  DeprecationLevel.WARNING
)
fun <A> liftEq(EQA: Eq<A>, arg0: Eq<A>): Eq<Kind<Kind<ForIor, A>, A>> = arrow.core.Ior
   .eqK<A>(EQA)
   .liftEq<A>(arg0) as arrow.typeclasses.Eq<arrow.Kind<arrow.Kind<arrow.core.ForIor, A>, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.eqK(EQA: Eq<A>): IorEqK<A> = object : arrow.core.extensions.IorEqK<A> {
    override fun EQA(): arrow.typeclasses.Eq<A> = EQA }
