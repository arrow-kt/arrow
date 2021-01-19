package arrow.core.extensions.tuple8.eq

import arrow.core.Tuple8
import arrow.core.Tuple8.Companion
import arrow.core.extensions.Tuple8Eq
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
  "neqv(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH, arg1)",
  "arrow.core.neqv"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  arg1: Tuple8<A, B, C, D, E, F, G, H>
): Boolean = arrow.core.Tuple8.eq<A, B, C, D, E, F, G,
    H>(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Eq.tuple8(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH)",
    "arrow.core.Eq",
    "arrow.core.tuple8"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B, C, D, E, F, G, H> Companion.eq(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>
): Tuple8Eq<A, B, C, D, E, F, G, H> = object : arrow.core.extensions.Tuple8Eq<A, B, C, D, E, F, G,
    H> { override fun EQA(): arrow.typeclasses.Eq<A> = EQA

  override fun EQB(): arrow.typeclasses.Eq<B> = EQB

  override fun EQC(): arrow.typeclasses.Eq<C> = EQC

  override fun EQD(): arrow.typeclasses.Eq<D> = EQD

  override fun EQE(): arrow.typeclasses.Eq<E> = EQE

  override fun EQF(): arrow.typeclasses.Eq<F> = EQF

  override fun EQG(): arrow.typeclasses.Eq<G> = EQG

  override fun EQH(): arrow.typeclasses.Eq<H> = EQH }
