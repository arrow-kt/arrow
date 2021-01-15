package arrow.core.extensions.tuple3.eq

import arrow.core.Tuple3
import arrow.core.Tuple3.Companion
import arrow.core.extensions.Tuple3Eq
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "neqv(EQA, EQB, EQC, arg1)",
  "arrow.core.neqv"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Tuple3<A, B, C>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  arg1: Tuple3<A, B, C>
): Boolean = arrow.core.Tuple3.eq<A, B, C>(EQA, EQB, EQC).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B, C> Companion.eq(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>
): Tuple3Eq<A, B, C> = object : arrow.core.extensions.Tuple3Eq<A, B, C> { override fun EQA():
    arrow.typeclasses.Eq<A> = EQA

  override fun EQB(): arrow.typeclasses.Eq<B> = EQB

  override fun EQC(): arrow.typeclasses.Eq<C> = EQC }
