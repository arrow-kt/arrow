package arrow.core.extensions.tuple10.eq

import arrow.core.Tuple10
import arrow.core.Tuple10.Companion
import arrow.core.extensions.Tuple10Eq
import arrow.typeclasses.Eq
import arrow.typeclasses.EqDeprecation
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
  EqDeprecation,
  ReplaceWith("this != arg1"),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  EQI: Eq<I>,
  EQJ: Eq<J>,
  arg1: Tuple10<A, B, C, D, E, F, G, H, I, J>
): Boolean = arrow.core.Tuple10.eq<A, B, C, D, E, F, G, H, I,
  J>(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH, EQI, EQJ).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  EqDeprecation,
  level = DeprecationLevel.WARNING
)
inline fun <A, B, C, D, E, F, G, H, I, J> Companion.eq(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  EQI: Eq<I>,
  EQJ: Eq<J>
): Tuple10Eq<A, B, C, D, E, F, G, H, I, J> = object : arrow.core.extensions.Tuple10Eq<A, B, C, D, E,
    F, G, H, I, J> {
  override fun EQA(): arrow.typeclasses.Eq<A> = EQA

  override fun EQB(): arrow.typeclasses.Eq<B> = EQB

  override fun EQC(): arrow.typeclasses.Eq<C> = EQC

  override fun EQD(): arrow.typeclasses.Eq<D> = EQD

  override fun EQE(): arrow.typeclasses.Eq<E> = EQE

  override fun EQF(): arrow.typeclasses.Eq<F> = EQF

  override fun EQG(): arrow.typeclasses.Eq<G> = EQG

  override fun EQH(): arrow.typeclasses.Eq<H> = EQH

  override fun EQI(): arrow.typeclasses.Eq<I> = EQI

  override fun EQJ(): arrow.typeclasses.Eq<J> = EQJ
}
