@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")
@file:Suppress("LEAKED_IN_PLACE_LAMBDA")

package arrow.core.raise.context

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.RaiseAccumulate.Value
import arrow.core.raise.RaiseDSL
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_LEAST_ONCE
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public typealias RaiseAccumulate<A> = arrow.core.raise.RaiseAccumulate<A>

context(raise: RaiseAccumulate<Error>)
@RaiseDSL public inline fun <Error, A, B> Iterable<A>.mapOrAccumulate(
  @BuilderInference transform: context(RaiseAccumulate<Error>) (A) -> B
): List<B> = raise.mapOrAccumulate(this, transform)

context(raise: RaiseAccumulate<Error>)
@RaiseDSL public inline fun <Error, A, B> NonEmptyList<A>.mapOrAccumulate(
  @BuilderInference transform: context(RaiseAccumulate<Error>) (A) -> B
): NonEmptyList<B> {
  contract { callsInPlace(transform, AT_LEAST_ONCE) }
  return raise.mapOrAccumulate(this, transform)
}

context(raise: RaiseAccumulate<Error>)
@RaiseDSL public inline fun <Error, A, B> NonEmptySet<A>.mapOrAccumulate(
  @BuilderInference transform: context(RaiseAccumulate<Error>) (A) -> B
): NonEmptySet<B> {
  contract { callsInPlace(transform, AT_LEAST_ONCE) }
  return raise.mapOrAccumulate(this, transform)
}

context(raise: RaiseAccumulate<Error>)
@RaiseDSL public inline fun <K, Error, A, B> Map<K, A>.mapValuesOrAccumulate(
  @BuilderInference transform: context(RaiseAccumulate<Error>) (Map.Entry<K, A>) -> B
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

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(raise: RaiseAccumulate<Error>)
public fun <Error, A> Either<Error, A>.bindOrAccumulate(): Value<A> =
  with(raise) { this@bindOrAccumulate.bindOrAccumulate() }

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(raise: RaiseAccumulate<Error>)
public fun <Error, A> Iterable<Either<Error, A>>.bindAllOrAccumulate(): Value<List<A>> =
  with(raise) { this@bindAllOrAccumulate.bindAllOrAccumulate() }

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(raise: RaiseAccumulate<Error>)
public fun <Error, A> EitherNel<Error, A>.bindNelOrAccumulate(): Value<A> =
  with(raise) { this@bindNelOrAccumulate.bindNelOrAccumulate() }

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(raise: RaiseAccumulate<Error>)
public inline fun <Error> ensureOrAccumulate(condition: Boolean, error: () -> Error) {
  contract { callsInPlace(error, AT_MOST_ONCE) }
  with(raise) { ensureOrAccumulate(condition, error) }
}

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(raise: RaiseAccumulate<Error>)
public inline fun <Error, B: Any> ensureNotNullOrAccumulate(value: B?, error: () -> Error): Value<B> {
  contract { callsInPlace(error, AT_MOST_ONCE) }
  return with(raise) { ensureNotNullOrAccumulate(value, error) }
}

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(raise: RaiseAccumulate<Error>)
public inline fun <Error, A> accumulating(block: context(RaiseAccumulate<Error>) () -> A): Value<A> {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return with(raise) { accumulating(block) }
}
