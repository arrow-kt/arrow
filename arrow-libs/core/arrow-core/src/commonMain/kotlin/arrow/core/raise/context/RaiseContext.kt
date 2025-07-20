@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")

package arrow.core.raise.context

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.raise.RaiseDSL
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.raise.withError
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public typealias Raise<A> = arrow.core.raise.Raise<A>
public typealias SingletonRaise = Raise<Unit>
public typealias ResultRaise = Raise<Throwable>

context(raise: Raise<Error>) @RaiseDSL public fun <Error> raise(e: Error): Nothing =
  raise.raise(e)

context(raise: Raise<Error>) @RaiseDSL public inline fun <Error> ensure(condition: Boolean, otherwise: () -> Error) {
  contract { returns() implies condition }
  raise.ensure(condition, otherwise)
}

context(raise: Raise<Error>) @RaiseDSL public inline fun <Error, B : Any> ensureNotNull(value: B?, otherwise: () -> Error): B {
  contract { returns() implies (value != null) }
  return raise.ensureNotNull(value, otherwise)
}

context(raise: Raise<Error>) @RaiseDSL public inline fun <Error, OtherError, A> withError(
  transform: (OtherError) -> Error,
  @BuilderInference block: context(Raise<OtherError>) () -> A
): A {
  contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
  return raise.withError(transform, block)
}

context(_: Raise<Error>) @RaiseDSL public suspend fun <Error, A> Effect<Error, A>.bind(): A = this()

context(_: Raise<Error>) @RaiseDSL public fun <Error, A> EagerEffect<Error, A>.bind(): A = this()

@Suppress("WRONG_IMPLIES_CONDITION")
context(raise: Raise<Error>) @RaiseDSL public fun <Error, A> Either<Error, A>.bind(): A {
  contract { returns() implies (this@bind is Either.Right) }
  return with(raise) { this@bind.bind() }
}

context(_: ResultRaise) @RaiseDSL public fun <A> Result<A>.bind(): A {
  return getOrElse { raise(it) }
}

context(raise: Raise<Error>) @RaiseDSL @JvmName("bindAllEither")
public fun <Error, K, A> Map<K, Either<Error, A>>.bindAll(): Map<K, A> =
  with(raise) { bindAll() }

context(raise: Raise<Error>) @RaiseDSL @JvmName("bindAllEither")
public fun <Error, A> Iterable<Either<Error, A>>.bindAll(): List<A> =
  with(raise) { bindAll() }

context(raise: Raise<Error>) @RaiseDSL @JvmName("bindAllEither")
public fun <Error, A> NonEmptyList<Either<Error, A>>.bindAll(): NonEmptyList<A> =
  with(raise) { bindAll() }

context(raise: Raise<Error>) @RaiseDSL @JvmName("bindAllEither")
public fun <Error, A> NonEmptySet<Either<Error, A>>.bindAll(): NonEmptySet<A> =
  with(raise) { bindAll() }

context(_: ResultRaise) @RaiseDSL @JvmName("bindAllResult")
public fun <K, A> Map<K, Result<A>>.bindAll(): Map<K, A> =
  mapValues { it.value.bind() }

context(_: ResultRaise) @RaiseDSL @JvmName("bindAllResult")
public fun <A> Iterable<Result<A>>.bindAll(): List<A> =
  map { it.bind() }

context(_: ResultRaise) @RaiseDSL @JvmName("bindAllResult")
public fun <A> NonEmptyList<Result<A>>.bindAll(): NonEmptyList<A> =
  map { it.bind() }

context(raise: ResultRaise) @RaiseDSL @JvmName("bindAllResult")
public fun <A> NonEmptySet<Result<A>>.bindAll(): NonEmptySet<A> =
  map { it.bind() }.toNonEmptySet()

context(_: Raise<Unit>)
@RaiseDSL
public fun raise(): Nothing = raise(Unit)

context(_: Raise<Unit>)
@RaiseDSL
public fun raise(r: None): Nothing = raise()

context(_: Raise<Unit>)
@RaiseDSL
public fun raise(r: Nothing?): Nothing = raise()

context(_: Raise<Unit>)
@RaiseDSL
public fun raise(r: Unit): Nothing = raise()

context(_: Raise<Unit>)
@RaiseDSL
public fun ensure(condition: Boolean) {
  contract { returns() implies condition }
  return if (condition) Unit else raise()
}

context(_: Raise<Unit>)
@RaiseDSL
public fun <A> Option<A>.bind(): A {
  contract { returns() implies (this@bind is Some) }
  ensure(this is Some)
  return value
}

context(_: Raise<Unit>)
@RaiseDSL
public fun <A> A?.bind(): A {
  contract { returns() implies (this@bind != null) }
  return this ?: raise()
}

context(_: Raise<Unit>)
@RaiseDSL
public fun <A> ensureNotNull(value: A?): A {
  contract { returns() implies (value != null) }
  return value ?: raise()
}

context(_: Raise<Unit>)
@RaiseDSL
@JvmName("bindAllNullable")
public fun <K, V> Map<K, V?>.bindAll(): Map<K, V> =
  mapValues { (_, v) -> v.bind() }

context(_: Raise<Unit>)
@JvmName("bindAllOption")
public fun <K, V> Map<K, Option<V>>.bindAll(): Map<K, V> =
  mapValues { (_, v) -> v.bind() }

context(_: Raise<Unit>)
@RaiseDSL
@JvmName("bindAllNullable")
public fun <A> Iterable<A?>.bindAll(): List<A> =
  map { it.bind() }

context(_: Raise<Unit>)
@RaiseDSL
@JvmName("bindAllOption")
public fun <A> Iterable<Option<A>>.bindAll(): List<A> =
  map { it.bind() }

context(_: Raise<Unit>)
@RaiseDSL
@JvmName("bindAllNullable")
public fun <A> NonEmptyList<A?>.bindAll(): NonEmptyList<A> =
  map { it.bind() }

context(_: Raise<Unit>)
@RaiseDSL
@JvmName("bindAllOption")
public fun <A> NonEmptyList<Option<A>>.bindAll(): NonEmptyList<A> =
  map { it.bind() }

context(_: Raise<Unit>)
@RaiseDSL
@JvmName("bindAllNullable")
public fun <A> NonEmptySet<A?>.bindAll(): NonEmptySet<A> =
  map { it.bind() }.toNonEmptySet()

context(_: Raise<Unit>)
@RaiseDSL
@JvmName("bindAllOption")
public fun <A> NonEmptySet<Option<A>>.bindAll(): NonEmptySet<A> =
  map { it.bind() }.toNonEmptySet()
