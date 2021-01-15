package arrow.core.extensions.tuple4.eq

import arrow.core.Tuple4
import arrow.core.Tuple4.Companion
import arrow.core.extensions.Tuple4Eq
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
  "neqv(EQA, EQB, EQC, EQD, arg1)",
  "arrow.core.neqv"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> Tuple4<A, B, C, D>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  arg1: Tuple4<A, B, C, D>
): Boolean = arrow.core.Tuple4.eq<A, B, C, D>(EQA, EQB, EQC, EQD).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B, C, D> Companion.eq(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>
): Tuple4Eq<A, B, C, D> = object : arrow.core.extensions.Tuple4Eq<A, B, C, D> { override fun EQA():
    arrow.typeclasses.Eq<A> = EQA

  override fun EQB(): arrow.typeclasses.Eq<B> = EQB

  override fun EQC(): arrow.typeclasses.Eq<C> = EQC

  override fun EQD(): arrow.typeclasses.Eq<D> = EQD }
