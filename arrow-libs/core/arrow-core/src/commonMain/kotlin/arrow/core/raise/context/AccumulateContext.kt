@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")
@file:Suppress("LEAKED_IN_PLACE_LAMBDA")

package arrow.core.raise.context

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.RaiseAccumulate.Value
import arrow.core.raise.RaiseDSL
import arrow.core.raise.accumulating
import arrow.core.raise.ensureNotNullOrAccumulate
import arrow.core.raise.ensureOrAccumulate
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public typealias Accumulate<A> = arrow.core.raise.Accumulate<A>

@ExperimentalRaiseAccumulateApi
@JvmName("accumulateContextBindOr")
context(raise: Accumulate<Error>)
public fun <Error, A> Either<Error, A>.bindOrAccumulate(): Value<A> =
  with(raise) { this@bindOrAccumulate.bindOrAccumulate() }

@ExperimentalRaiseAccumulateApi
@JvmName("accumulateContextBindAll")
context(raise: Accumulate<Error>)
public fun <Error, A> Iterable<Either<Error, A>>.bindAllOrAccumulate(): Value<List<A>> =
  with(raise) { this@bindAllOrAccumulate.bindAllOrAccumulate() }

@ExperimentalRaiseAccumulateApi
@JvmName("accumulateContextBindNel")
context(raise: Accumulate<Error>)
public fun <Error, A> EitherNel<Error, A>.bindNelOrAccumulate(): Value<A> =
  with(raise) { this@bindNelOrAccumulate.bindNelOrAccumulate() }

@ExperimentalRaiseAccumulateApi
@RaiseDSL
@IgnorableReturnValue
@JvmName("accumulateContextEnsure")
context(raise: Accumulate<Error>)
public inline fun <Error> ensureOrAccumulate(condition: Boolean, error: () -> Error): Value<Unit> {
  contract { callsInPlace(error, AT_MOST_ONCE) }
  return raise.ensureOrAccumulate(condition, error)
}

@ExperimentalRaiseAccumulateApi
@RaiseDSL
@JvmName("accumulateContextEnsureNotNull")
context(raise: Accumulate<Error>)
public inline fun <Error, B : Any> ensureNotNullOrAccumulate(value: B?, error: () -> Error): Value<B> {
  contract { callsInPlace(error, AT_MOST_ONCE) }
  return raise.ensureNotNullOrAccumulate(value, error)
}

@ExperimentalRaiseAccumulateApi
@JvmName("accumulateContextGet")
context(raise: Accumulate<Error>)
public fun <Error, A> Either<Error, A>.getOrAccumulate(recover: (Error) -> A): A =
  with(raise) { this@getOrAccumulate.getOrAccumulate(recover) }

@ExperimentalRaiseAccumulateApi
@JvmName("accumulateContextAccumulating")
context(raise: Accumulate<Error>)
public inline fun <Error, A> accumulating(block: context(RaiseAccumulate<Error>) () -> A): Value<A> {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return raise.accumulating { block() }
}

@JvmName("accumulateContextMap")
context(raise: Accumulate<Error>)
@RaiseDSL
public inline fun <Error, A, B> Iterable<A>.mapOrAccumulate(
  transform: context(RaiseAccumulate<Error>) (A) -> B
): Value<List<B>> = raise.accumulating {
  this@mapOrAccumulate.mapOrAccumulate(transform)
}
