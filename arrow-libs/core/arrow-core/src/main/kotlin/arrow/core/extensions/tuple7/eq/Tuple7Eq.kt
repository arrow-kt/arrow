package arrow.core.extensions.tuple7.eq

import arrow.core.Tuple7
import arrow.core.Tuple7.Companion
import arrow.core.extensions.Tuple7Eq
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
fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  arg1: Tuple7<A, B, C, D, E, F, G>
): Boolean = arrow.core.Tuple7.eq<A, B, C, D, E, F, G>(EQA, EQB, EQC, EQD, EQE, EQF, EQG).run {
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
inline fun <A, B, C, D, E, F, G> Companion.eq(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>
): Tuple7Eq<A, B, C, D, E, F, G> = object : arrow.core.extensions.Tuple7Eq<A, B, C, D, E, F, G> {
    override fun EQA(): arrow.typeclasses.Eq<A> = EQA

  override fun EQB(): arrow.typeclasses.Eq<B> = EQB

  override fun EQC(): arrow.typeclasses.Eq<C> = EQC

  override fun EQD(): arrow.typeclasses.Eq<D> = EQD

  override fun EQE(): arrow.typeclasses.Eq<E> = EQE

  override fun EQF(): arrow.typeclasses.Eq<F> = EQF

  override fun EQG(): arrow.typeclasses.Eq<G> = EQG }
