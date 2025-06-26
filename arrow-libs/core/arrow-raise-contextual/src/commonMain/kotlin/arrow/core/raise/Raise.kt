@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")

package arrow.core.raise

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.Option
import arrow.core.Some
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

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
  @BuilderInference block: Raise<OtherError>.() -> A
): A = raise.withError(transform, block)

context(raise: Raise<Error>) @RaiseDSL public suspend fun <Error, A> Effect<Error, A>.bind(): A =
  with(raise) { bind() }

context(raise: Raise<Error>) @RaiseDSL public fun <Error, A> Either<Error, A>.bind(): A {
  contract { returns() implies (this@bind is Either.Right) }
  return with(raise) { this@bind.bind() }
}

context(raise: SingletonRaise<Error>) @RaiseDSL public fun <Error, A> Option<A>.bind(): A {
  contract { returns() implies (this@bind is Some) }
  return with(raise) { this@bind.bind() }
}

context(raise: SingletonRaise<Error>) @RaiseDSL public fun <Error, A> A?.bind(): A {
  contract { returns() implies (this@bind != null) }
  return with(raise) { this@bind.bind() }
}

context(raise: ResultRaise) @RaiseDSL public fun <Error, A> Result<A>.bind(): A {
  return with(raise) { this@bind.bind() }
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

context(raise: SingletonRaise<Error>) @RaiseDSL @JvmName("bindAllOption")
public fun <Error, K, A> Map<K, Option<A>>.bindAll(): Map<K, A> =
  with(raise) { bindAll() }

context(raise: SingletonRaise<Error>) @RaiseDSL @JvmName("bindAllOption")
public fun <Error, A> Iterable<Option<A>>.bindAll(): List<A> =
  with(raise) { bindAll() }

context(raise: SingletonRaise<Error>) @RaiseDSL @JvmName("bindAllOption")
public fun <Error, A> NonEmptyList<Option<A>>.bindAll(): NonEmptyList<A> =
  with(raise) { bindAll() }

context(raise: SingletonRaise<Error>) @RaiseDSL @JvmName("bindAllOption")
public fun <Error, A> NonEmptySet<Option<A>>.bindAll(): NonEmptySet<A> =
  with(raise) { bindAll() }

context(raise: SingletonRaise<Error>) @RaiseDSL @JvmName("bindAllNullable")
public fun <Error, K, A> Map<K, A?>.bindAll(): Map<K, A> =
  with(raise) { bindAll() }

context(raise: SingletonRaise<Error>) @RaiseDSL @JvmName("bindAllNullable")
public fun <Error, A> Iterable<A?>.bindAll(): List<A> =
  with(raise) { bindAll() }

context(raise: SingletonRaise<Error>) @RaiseDSL @JvmName("bindAllNullable")
public fun <Error, A> NonEmptyList<A?>.bindAll(): NonEmptyList<A> =
  with(raise) { bindAll() }

context(raise: SingletonRaise<Error>) @RaiseDSL @JvmName("bindAllNullable")
public fun <Error, A> NonEmptySet<A?>.bindAll(): NonEmptySet<A> =
  with(raise) { bindAll() }

context(raise: ResultRaise) @RaiseDSL @JvmName("bindAllResult")
public fun <Error, K, A> Map<K, Result<A>>.bindAll(): Map<K, A> =
  with(raise) { bindAll() }

context(raise: ResultRaise) @RaiseDSL @JvmName("bindAllResult")
public fun <Error, A> Iterable<Result<A>>.bindAll(): List<A> =
  with(raise) { bindAll() }

context(raise: ResultRaise) @RaiseDSL @JvmName("bindAllResult")
public fun <Error, A> NonEmptyList<Result<A>>.bindAll(): NonEmptyList<A> =
  with(raise) { bindAll() }

context(raise: ResultRaise) @RaiseDSL @JvmName("bindAllResult")
public fun <Error, A> NonEmptySet<Result<A>>.bindAll(): NonEmptySet<A> =
  with(raise) { bindAll() }
