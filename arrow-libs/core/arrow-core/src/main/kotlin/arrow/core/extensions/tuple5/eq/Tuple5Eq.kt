package arrow.core.extensions.tuple5.eq

import arrow.core.Tuple5
import arrow.core.Tuple5.Companion
import arrow.core.extensions.Tuple5Eq
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
  "neqv(EQA, EQB, EQC, EQD, EQE, arg1)",
  "arrow.core.neqv"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  arg1: Tuple5<A, B, C, D, E>
): Boolean = arrow.core.Tuple5.eq<A, B, C, D, E>(EQA, EQB, EQC, EQD, EQE).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Eq.tuple5(EQA, EQB, EQC, EQD, EQE)",
    "arrow.core.Eq",
    "arrow.core.tuple5"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B, C, D, E> Companion.eq(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>
): Tuple5Eq<A, B, C, D, E> = object : arrow.core.extensions.Tuple5Eq<A, B, C, D, E> { override fun
    EQA(): arrow.typeclasses.Eq<A> = EQA

  override fun EQB(): arrow.typeclasses.Eq<B> = EQB

  override fun EQC(): arrow.typeclasses.Eq<C> = EQC

  override fun EQD(): arrow.typeclasses.Eq<D> = EQD

  override fun EQE(): arrow.typeclasses.Eq<E> = EQE }
