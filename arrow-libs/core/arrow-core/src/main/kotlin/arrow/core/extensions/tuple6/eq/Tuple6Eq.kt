package arrow.core.extensions.tuple6.eq

import arrow.core.Tuple6
import arrow.core.Tuple6.Companion
import arrow.core.extensions.Tuple6Eq
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
fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  arg1: Tuple6<A, B, C, D, E, F>
): Boolean = arrow.core.Tuple6.eq<A, B, C, D, E, F>(EQA, EQB, EQC, EQD, EQE, EQF).run {
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
inline fun <A, B, C, D, E, F> Companion.eq(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>
): Tuple6Eq<A, B, C, D, E, F> = object : arrow.core.extensions.Tuple6Eq<A, B, C, D, E, F> { override
    fun EQA(): arrow.typeclasses.Eq<A> = EQA

  override fun EQB(): arrow.typeclasses.Eq<B> = EQB

  override fun EQC(): arrow.typeclasses.Eq<C> = EQC

  override fun EQD(): arrow.typeclasses.Eq<D> = EQD

  override fun EQE(): arrow.typeclasses.Eq<E> = EQE

  override fun EQF(): arrow.typeclasses.Eq<F> = EQF }
