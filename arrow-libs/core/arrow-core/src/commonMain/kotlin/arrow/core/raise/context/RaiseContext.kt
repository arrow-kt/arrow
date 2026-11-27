@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")
@file:Suppress("API_NOT_AVAILABLE")

package arrow.core.raise.context

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.Option
import arrow.core.Some
import arrow.core.raise.Effect
import arrow.core.raise.RaiseDSL
import kotlinx.library.watchdog.ExemptionReason
import kotlinx.library.watchdog.IntentionallyBooleanParameter
import arrow.core.raise.ensure as ensureExt
import arrow.core.raise.ensureNotNull as ensureNotNullExt
import arrow.core.raise.withError as withErrorExt
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.ExperimentalExtendedContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public typealias Raise<A> = arrow.core.raise.Raise<A>
public typealias SingletonRaise<A> = arrow.core.raise.SingletonRaise<A>
public typealias ResultRaise = arrow.core.raise.ResultRaise

@RaiseDSL
context(raise: Raise<Error>)
public fun <Error> raise(e: Error): Nothing =
  raise.raise(e)

@OptIn(ExperimentalExtendedContracts::class)
@IntentionallyBooleanParameter(reason = ExemptionReason.API_DESIGN)
@RaiseDSL
context(raise: Raise<Error>)
public inline fun <Error> ensure(condition: Boolean, otherwise: () -> Error) {
  contract {
    callsInPlace(otherwise, AT_MOST_ONCE)
    returns() implies condition
    !condition holdsIn otherwise
  }
  raise.ensureExt(condition, otherwise)
}

@OptIn(ExperimentalExtendedContracts::class)
@RaiseDSL
@IgnorableReturnValue
context(raise: Raise<Error>)
public inline fun <Error, B : Any> ensureNotNull(value: B?, otherwise: () -> Error): B {
  contract {
    returns() implies (value != null)
    callsInPlace(otherwise, AT_MOST_ONCE)
    (value == null) holdsIn otherwise
  }
  return raise.ensureNotNullExt(value, otherwise)
}

@RaiseDSL
context(raise: Raise<Error>)
public inline fun <Error, OtherError, A> withError(
  transform: (OtherError) -> Error,
  block: context(Raise<OtherError>) () -> A
): A {
  contract {
    callsInPlace(block, EXACTLY_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return raise.withErrorExt(transform, block)
}

@RaiseDSL
context(raise: Raise<Error>)
public suspend fun <Error, A> Effect<Error, A>.bind(): A =
  with(raise) { bind() }

@Suppress("WRONG_IMPLIES_CONDITION")
@RaiseDSL
context(raise: Raise<Error>)
public fun <Error, A> Either<Error, A>.bind(): A {
  contract { returns() implies (this@bind is Either.Right) }
  return with(raise) { this@bind.bind() }
}

@Suppress("WRONG_IMPLIES_CONDITION")
@RaiseDSL
context(raise: SingletonRaise<Error>)
public fun <Error, A> Option<A>.bind(): A {
  contract { returns() implies (this@bind is Some) }
  return with(raise) { this@bind.bind() }
}

@Suppress("WRONG_IMPLIES_CONDITION")
@RaiseDSL
context(raise: SingletonRaise<Error>)
public fun <Error, A> A?.bind(): A {
  contract { returns() implies (this@bind != null) }
  return with(raise) { this@bind.bind() }
}

@RaiseDSL
context(raise: ResultRaise)
public fun <A> Result<A>.bind(): A {
  return with(raise) { this@bind.bind() }
}

@RaiseDSL
@JvmName("bindAllEither")
context(raise: Raise<Error>)
public fun <Error, K, A> Map<K, Either<Error, A>>.bindAll(): Map<K, A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllEither")
context(raise: Raise<Error>)
public fun <Error, A> Iterable<Either<Error, A>>.bindAll(): List<A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllEither")
context(raise: Raise<Error>)
public fun <Error, A> NonEmptyList<Either<Error, A>>.bindAll(): NonEmptyList<A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllEither")
context(raise: Raise<Error>)
public fun <Error, A> NonEmptySet<Either<Error, A>>.bindAll(): NonEmptySet<A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllOption")
context(raise: SingletonRaise<Error>)
public fun <Error, K, A> Map<K, Option<A>>.bindAll(): Map<K, A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllOption")
context(raise: SingletonRaise<Error>)
public fun <Error, A> Iterable<Option<A>>.bindAll(): List<A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllOption")
context(raise: SingletonRaise<Error>)
public fun <Error, A> NonEmptyList<Option<A>>.bindAll(): NonEmptyList<A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllOption")
context(raise: SingletonRaise<Error>)
public fun <Error, A> NonEmptySet<Option<A>>.bindAll(): NonEmptySet<A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllNullable")
context(raise: SingletonRaise<Error>)
public fun <Error, K, A> Map<K, A?>.bindAll(): Map<K, A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllNullable")
context(raise: SingletonRaise<Error>)
public fun <Error, A> Iterable<A?>.bindAll(): List<A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllNullable")
context(raise: SingletonRaise<Error>)
public fun <Error, A> NonEmptyList<A?>.bindAll(): NonEmptyList<A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllNullable")
context(raise: SingletonRaise<Error>)
public fun <Error, A> NonEmptySet<A?>.bindAll(): NonEmptySet<A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllResult")
context(raise: ResultRaise)
public fun <K, A> Map<K, Result<A>>.bindAll(): Map<K, A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllResult")
context(raise: ResultRaise)
public fun <A> Iterable<Result<A>>.bindAll(): List<A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllResult")
context(raise: ResultRaise)
public fun <A> NonEmptyList<Result<A>>.bindAll(): NonEmptyList<A> =
  with(raise) { bindAll() }

@RaiseDSL
@JvmName("bindAllResult")
context(raise: ResultRaise)
public fun <A> NonEmptySet<Result<A>>.bindAll(): NonEmptySet<A> =
  with(raise) { bindAll() }
