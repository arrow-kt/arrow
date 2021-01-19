package arrow.core.extensions.tuple9.eq

import arrow.core.Tuple9
import arrow.core.Tuple9.Companion
import arrow.core.extensions.Tuple9Eq
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
  "neqv(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH, EQI, arg1)",
  "arrow.core.neqv"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H, I> Tuple9<A, B, C, D, E, F, G, H, I>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  EQI: Eq<I>,
  arg1: Tuple9<A, B, C, D, E, F, G, H, I>
): Boolean = arrow.core.Tuple9.eq<A, B, C, D, E, F, G, H,
    I>(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH, EQI).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Eq.tuple9(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH, EQI)",
    "arrow.core.Eq",
    "arrow.core.tuple9"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B, C, D, E, F, G, H, I> Companion.eq(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  EQI: Eq<I>
): Tuple9Eq<A, B, C, D, E, F, G, H, I> = object : arrow.core.extensions.Tuple9Eq<A, B, C, D, E, F,
    G, H, I> { override fun EQA(): arrow.typeclasses.Eq<A> = EQA

  override fun EQB(): arrow.typeclasses.Eq<B> = EQB

  override fun EQC(): arrow.typeclasses.Eq<C> = EQC

  override fun EQD(): arrow.typeclasses.Eq<D> = EQD

  override fun EQE(): arrow.typeclasses.Eq<E> = EQE

  override fun EQF(): arrow.typeclasses.Eq<F> = EQF

  override fun EQG(): arrow.typeclasses.Eq<G> = EQG

  override fun EQH(): arrow.typeclasses.Eq<H> = EQH

  override fun EQI(): arrow.typeclasses.Eq<I> = EQI }
