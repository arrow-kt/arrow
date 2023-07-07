@file:JvmMultifileClass
@file:JvmName("RaiseKt")
@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package arrow.core.raise

import arrow.atomic.Atomic
import arrow.atomic.updateAndGet
import arrow.core.Either
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Runs a computation [block] using [Raise], and return its outcome as [Either].
 * - [Either.Right] represents success,
 * - [Either.Left] represents logical failure.
 *
 * This function re-throws any exceptions thrown within the [Raise] block.
 *
 * Read more about running a [Raise] computation in the
 * [Arrow docs](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#running-and-inspecting-results).
 */
public inline fun <Error, A> either(@BuilderInference block: Raise<Error>.() -> A): Either<Error, A> =
  fold({ block.invoke(this) }, { Either.Left(it) }, { Either.Right(it) })

/**
 * Runs a computation [block] using [Raise], and return its outcome as nullable type,
 * where `null` represents logical failure.
 *
 * This function re-throws any exceptions thrown within the [Raise] block.
 *
 * Read more about running a [Raise] computation in the
 * [Arrow docs](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#running-and-inspecting-results).
 */
public inline fun <A> nullable(block: NullableRaise.() -> A): A? =
  merge { block(NullableRaise(this)) }

/**
 * Runs a computation [block] using [Raise], and return its outcome as [Result].
 *
 *
 * Read more about running a [Raise] computation in the
 * [Arrow docs](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#running-and-inspecting-results).
 */
public inline fun <A> result(block: ResultRaise.() -> A): Result<A> =
  fold({ block(ResultRaise(this)) }, Result.Companion::failure, Result.Companion::failure, Result.Companion::success)

/**
 * Runs a computation [block] using [Raise], and return its outcome as [Option].
 * - [Some] represents success,
 * - [None] represents logical failure.
 *
 * This function re-throws any exceptions thrown within the [Raise] block.
 *
 * Read more about running a [Raise] computation in the
 * [Arrow docs](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#running-and-inspecting-results).
 */
public inline fun <A> option(block: OptionRaise.() -> A): Option<A> =
  fold({ block(OptionRaise(this)) }, ::identity, ::Some)

/**
 * Runs a computation [block] using [Raise], and return its outcome as [Ior].
 * - [Ior.Right] represents success,
 * - [Ior.Left] represents logical failure which made it impossible to continue,
 * - [Ior.Both] represents that some logical failures were raised,
 *   but it was possible to continue until producing a final value.
 *
 * This function re-throws any exceptions thrown within the [Raise] block.
 *
 * In both [Ior.Left] and [Ior.Both] cases, if more than one logical failure
 * has been raised, they are combined using [combineError].
 *
 * Read more about running a [Raise] computation in the
 * [Arrow docs](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#running-and-inspecting-results).
 */
public inline fun <Error, A> ior(noinline combineError: (Error, Error) -> Error, @BuilderInference block: IorRaise<Error>.() -> A): Ior<Error, A> {
  val state: Atomic<Option<Error>> = Atomic(None)
  return fold<Error, A, Ior<Error, A>>(
    { block(IorRaise(combineError, state, this)) },
    { e -> throw e },
    { e -> Ior.Left(state.get().getOrElse { e }) },
    { a -> state.get().fold({ Ior.Right(a) }, { Ior.Both(it, a) }) }
  )
}

public typealias Null = Nothing?

/**
 * Implementation of [Raise] used by [nullable].
 * You should never use this directly.
 */
public class NullableRaise(private val raise: Raise<Null>) : Raise<Null> by raise {
  @RaiseDSL
  public fun ensure(value: Boolean): Unit = ensure(value) { null }

  @RaiseDSL
  public fun <A> Option<A>.bind(): A = getOrElse { raise(null) }

  @RaiseDSL
  public fun <A> A?.bind(): A {
    contract { returns() implies (this@bind != null) }
    return this ?: raise(null)
  }

  @JvmName("bindAllNullable")
  public fun <K, V> Map<K, V?>.bindAll(): Map<K, V> =
    mapValues { (_, v) -> v.bind() }

  @RaiseDSL
  @JvmName("bindAllNullable")
  public fun <A> Iterable<A?>.bindAll(): List<A> =
    map { it.bind() }

  @RaiseDSL
  public fun <A> ensureNotNull(value: A?): A {
    contract { returns() implies (value != null) }
    return ensureNotNull(value) { null }
  }

  @RaiseDSL
  public inline fun <A> recover(
    @BuilderInference block: NullableRaise.() -> A,
    recover: () -> A,
  ): A = when (val nullable = nullable(block)) {
    null -> recover()
    else -> nullable
  }
}

/**
 * Implementation of [Raise] used by [result].
 * You should never use this directly.
 */
public class ResultRaise(private val raise: Raise<Throwable>) : Raise<Throwable> by raise {
  @RaiseDSL
  public fun <A> Result<A>.bind(): A = fold(::identity) { raise(it) }

  @JvmName("bindAllResult")
  public fun <K, V> Map<K, Result<V>>.bindAll(): Map<K, V> =
    mapValues { (_, v) -> v.bind() }

  @RaiseDSL
  @JvmName("bindAllResult")
  public fun <A> Iterable<Result<A>>.bindAll(): List<A> =
    map { it.bind() }

  @RaiseDSL
  @JvmName("bindAllResult")
  public fun <A> NonEmptyList<Result<A>>.bindAll(): NonEmptyList<A> =
    map { it.bind() }

  @RaiseDSL
  @JvmName("bindAllResult")
  public fun <A> NonEmptySet<Result<A>>.bindAll(): NonEmptySet<A> =
    map { it.bind() }.toNonEmptySet()

  @RaiseDSL
  public inline fun <A> recover(
    @BuilderInference block: ResultRaise.() -> A,
    recover: (Throwable) -> A,
  ): A = result(block).fold(
    onSuccess = { it },
    onFailure =  { recover(it) }
  )
}

/**
 * Implementation of [Raise] used by [option].
 * You should never use this directly.
 */
public class OptionRaise(private val raise: Raise<None>) : Raise<None> by raise {
  @RaiseDSL
  public fun <A> Option<A>.bind(): A = getOrElse { raise(None) }

  @JvmName("bindAllOption")
  public fun <K, V> Map<K, Option<V>>.bindAll(): Map<K, V> =
    mapValues { (_, v) -> v.bind() }

  @RaiseDSL
  @JvmName("bindAllOption")
  public fun <A> Iterable<Option<A>>.bindAll(): List<A> =
    map { it.bind() }

  @RaiseDSL
  @JvmName("bindAllOption")
  public fun <A> NonEmptyList<Option<A>>.bindAll(): NonEmptyList<A> =
    map { it.bind() }

  @RaiseDSL
  @JvmName("bindAllOption")
  public fun <A> NonEmptySet<Option<A>>.bindAll(): NonEmptySet<A> =
    map { it.bind() }.toNonEmptySet()

  @RaiseDSL
  public fun ensure(value: Boolean): Unit = ensure(value) { None }

  @RaiseDSL
  public fun <A> ensureNotNull(value: A?): A {
    contract { returns() implies (value != null) }
    return ensureNotNull(value) { None }
  }

  @RaiseDSL
  public inline fun <A> recover(
    @BuilderInference block: OptionRaise.() -> A,
    recover: () -> A,
  ): A = when (val option = option(block)) {
    is None -> recover()
    is Some<A> -> option.value
  }
}

/**
 * Implementation of [Raise] used by [ior].
 * You should never use this directly.
 */
public class IorRaise<Error> @PublishedApi internal constructor(
  @PublishedApi internal val combineError: (Error, Error) -> Error,
  private val state: Atomic<Option<Error>>,
  private val raise: Raise<Error>,
) : Raise<Error> {

  @RaiseDSL
  override fun raise(r: Error): Nothing = raise.raise(combine(r))

  @RaiseDSL
  @JvmName("bindAllIor")
  public fun <A> Iterable<Ior<Error, A>>.bindAll(): List<A> =
    map { it.bind() }

  @RaiseDSL
  @JvmName("bindAllIor")
  public fun <A> NonEmptyList<Ior<Error, A>>.bindAll(): NonEmptyList<A> =
    map { it.bind() }

  @RaiseDSL
  @JvmName("bindAllIor")
  public fun <A> NonEmptySet<Ior<Error, A>>.bindAll(): NonEmptySet<A> =
    map { it.bind() }.toNonEmptySet()

  @RaiseDSL
  public fun <A> Ior<Error, A>.bind(): A =
    when (this) {
      is Ior.Left -> raise(value)
      is Ior.Right -> value
      is Ior.Both -> {
        combine(leftValue)
        rightValue
      }
    }

  @JvmName("bindAllIor")
  public fun <K, V> Map<K, Ior<Error, V>>.bindAll(): Map<K, V> =
    mapValues { (_, v) -> v.bind() }

  @PublishedApi
  internal fun combine(other: Error): Error =
    state.updateAndGet { prev ->
      Some(prev.map { combineError(it, other) }.getOrElse { other })
    }.getOrElse { other }

  @RaiseDSL
  public inline fun <A> recover(
    @BuilderInference block: IorRaise<Error>.() -> A,
    recover: (error: Error) -> A,
  ): A = when (val ior = ior(combineError, block)) {
    is Ior.Both -> {
      combine(ior.leftValue)
      ior.rightValue
    }

    is Ior.Left -> recover(ior.value)
    is Ior.Right -> ior.value
  }
}
