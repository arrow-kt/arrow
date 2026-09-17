@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")
@file:Suppress("LEAKED_IN_PLACE_LAMBDA")

package arrow.core.raise.context

import arrow.core.EitherNel
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.raise.RaiseDSL
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_LEAST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public typealias RaiseAccumulate<A> = arrow.core.raise.RaiseAccumulate<A>

@RaiseDSL
context(raise: RaiseAccumulate<Error>)
public inline fun <Error, A, B> Iterable<A>.mapOrAccumulate(
  transform: context(RaiseAccumulate<Error>) (A) -> B
): List<B> = raise.mapOrAccumulate(this, transform)

@Suppress("WRONG_INVOCATION_KIND") @RaiseDSL // at least once is given by the type
context(raise: RaiseAccumulate<Error>)
public inline fun <Error, A, B> NonEmptyList<A>.mapOrAccumulate(
  transform: context(RaiseAccumulate<Error>) (A) -> B
): NonEmptyList<B> {
  contract { callsInPlace(transform, AT_LEAST_ONCE) }
  return raise.mapOrAccumulate(this, transform)
}

@Suppress("WRONG_INVOCATION_KIND") @RaiseDSL // at least once is given by the type
context(raise: RaiseAccumulate<Error>)
public inline fun <Error, A, B> NonEmptySet<A>.mapOrAccumulate(
  transform: context(RaiseAccumulate<Error>) (A) -> B
): NonEmptySet<B> {
  contract { callsInPlace(transform, AT_LEAST_ONCE) }
  return raise.mapOrAccumulate(this, transform)
}

@RaiseDSL
context(raise: RaiseAccumulate<Error>)
public inline fun <K, Error, A, B> Map<K, A>.mapValuesOrAccumulate(
  transform: context(RaiseAccumulate<Error>) (Map.Entry<K, A>) -> B
): Map<K, B> = with(raise) { this@mapValuesOrAccumulate.mapValuesOrAccumulate(transform) }

@RaiseDSL
context(raise: RaiseAccumulate<Error>)
public fun <Error, A> EitherNel<Error, A>.bindNel(): A =
  with(raise) { this@bindNel.bindNel() }

@RaiseDSL
context(raise: RaiseAccumulate<Error>)
public inline fun <Error, A> withNel(block: context(Raise<NonEmptyList<Error>>) () -> A): A {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  return raise.withNel { block() }
}
