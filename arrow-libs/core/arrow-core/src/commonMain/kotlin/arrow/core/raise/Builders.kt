@file:JvmMultifileClass
@file:JvmName("RaiseKt")
@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package arrow.core.raise

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.core.Either
import arrow.core.EmptyValue
import arrow.core.Ior
import arrow.core.IorNel
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.none
import arrow.core.some
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@RaiseDSL
public inline fun <A> singleton(
  raise: () -> A,
  @BuilderInference block: SingletonRaise<A>.() -> A,
): A {
  contract {
    callsInPlace(raise, InvocationKind.AT_MOST_ONCE)
    callsInPlace(block, InvocationKind.AT_MOST_ONCE)
  }
  return recover({ block(SingletonRaise(this)) }) { raise() }
}

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
public inline fun <Error, A> either(@BuilderInference block: Raise<Error>.() -> A): Either<Error, A> {
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  return fold(block, { Either.Left(it) }, { Either.Right(it) })
}

/**
 * Runs a computation [block] using [Raise], and return its outcome as nullable type,
 * where `null` represents logical failure.
 *
 * This function re-throws any exceptions thrown within the [Raise] block.
 *
 * Read more about running a [Raise] computation in the
 * [Arrow docs](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#running-and-inspecting-results).
 *
 * @see SingletonRaise.ignoreErrors By default, `nullable` only allows raising `null`.
 * Calling [ignoreErrors][SingletonRaise.ignoreErrors] inside `nullable` allows to raise any error, which will be returned to the caller as if `null` was raised.
 */
public inline fun <A> nullable(block: SingletonRaise<Nothing?>.() -> A): A? {
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  return singleton({ null }, block)
}

/**
 * Runs a computation [block] using [Raise], and return its outcome as [Result].
 *
 *
 * Read more about running a [Raise] computation in the
 * [Arrow docs](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#running-and-inspecting-results).
 */
public inline fun <A> result(block: ResultRaise.() -> A): Result<A> {
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  return fold({ block(ResultRaise(this)) }, Result.Companion::failure, Result.Companion::failure, Result.Companion::success)
}

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
public inline fun <A> option(block: SingletonRaise<None>.() -> A): Option<A> {
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  return singleton(::none) { block().some() }
}

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
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  val state: Atomic<Any?> = Atomic(EmptyValue)
  return fold(
    { block(IorRaise(combineError, state, this)) },
    { e -> Ior.Left(EmptyValue.combine(state.get(), e, combineError)) },
    { a -> EmptyValue.fold(state.get(), { Ior.Right(a) }, { e: Error -> Ior.Both(e, a) }) }
  )
}

/**
 * Run a computation [block] using [Raise]. and return its outcome as [IorNel].
 * - [Ior.Right] represents success,
 * - [Ior.Left] represents logical failure which made it impossible to continue,
 * - [Ior.Both] represents that some logical failures were raised,
 *   but it was possible to continue until producing a final value.
 *
 * This function re-throws any exceptions thrown within the [Raise] block.
 *
 * In both [Ior.Left] and [Ior.Both] cases, if more than one logical failure
 * has been raised, they are combined using [combineError]. This defaults to
 * combining [NonEmptyList]s by concatenating them.
 *
 * Read more about running a [Raise] computation in the
 * [Arrow docs](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#running-and-inspecting-results).
 */
public inline fun <Error, A> iorNel(noinline combineError: (NonEmptyList<Error>, NonEmptyList<Error>) -> NonEmptyList<Error> = { a, b -> a + b }, @BuilderInference block: IorRaise<NonEmptyList<Error>>.() -> A): IorNel<Error, A> {
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  return ior(combineError, block)
}

/**
 * Runs a computation [block] using [Raise], and ignore its outcome.
 *
 * This function re-throws any exceptions thrown within the [Raise] block.
 *
 * Read more about running a [Raise] computation in the
 * [Arrow docs](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#running-and-inspecting-results).
 */
public inline fun impure(block: SingletonRaise<Unit>.() -> Unit) {
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  return singleton({ }, block)
}

public class SingletonRaise<in E>(private val raise: Raise<Unit>): Raise<E> {
  @RaiseDSL
  public fun raise(): Nothing = raise.raise(Unit)

  @RaiseDSL
  override fun raise(r: E): Nothing = raise()

  @RaiseDSL
  public fun ensure(condition: Boolean) {
    contract { returns() implies condition }
    return if (condition) Unit else raise()
  }

  @RaiseDSL
  public fun <A> Option<A>.bind(): A {
    contract { returns() implies (this@bind is Some<A>) }
    return getOrElse { raise() }
  }

  @RaiseDSL
  public fun <A> A?.bind(): A {
    contract { returns() implies (this@bind != null) }
    return this ?: raise()
  }

  @RaiseDSL
  public fun <A> ensureNotNull(value: A?): A {
    contract { returns() implies (value != null) }
    return value ?: raise()
  }

  @RaiseDSL
  @JvmName("bindAllNullable")
  public fun <K, V> Map<K, V?>.bindAll(): Map<K, V> =
    mapValues { (_, v) -> v.bind() }

  @JvmName("bindAllOption")
  public fun <K, V> Map<K, Option<V>>.bindAll(): Map<K, V> =
    mapValues { (_, v) -> v.bind() }

  @RaiseDSL
  @JvmName("bindAllNullable")
  public fun <A> Iterable<A?>.bindAll(): List<A> =
    map { it.bind() }

  @RaiseDSL
  @JvmName("bindAllOption")
  public fun <A> Iterable<Option<A>>.bindAll(): List<A> =
    map { it.bind() }

  @RaiseDSL
  @JvmName("bindAllNullable")
  public fun <A> NonEmptyList<A?>.bindAll(): NonEmptyList<A> =
    map { it.bind() }

  @RaiseDSL
  @JvmName("bindAllOption")
  public fun <A> NonEmptyList<Option<A>>.bindAll(): NonEmptyList<A> =
    map { it.bind() }

  @RaiseDSL
  @JvmName("bindAllNullable")
  public fun <A> NonEmptySet<A?>.bindAll(): NonEmptySet<A> =
    map { it.bind() }.toNonEmptySet()

  @RaiseDSL
  @JvmName("bindAllOption")
  public fun <A> NonEmptySet<Option<A>>.bindAll(): NonEmptySet<A> =
    map { it.bind() }.toNonEmptySet()

  @RaiseDSL
  public inline fun <A> recover(
    block: SingletonRaise<E>.() -> A,
    raise: () -> A,
  ): A {
    contract {
      callsInPlace(block, InvocationKind.AT_MOST_ONCE)
      callsInPlace(raise, InvocationKind.AT_MOST_ONCE)
    }
    return recover<_, A>({ block(SingletonRaise(this)) }) { raise() }
  }

  /**
   * Introduces a scope where you can [bind] errors of any type,
   * but no information is saved in the [raise] case.
   */
  @RaiseDSL
  public inline fun <A> ignoreErrors(
    block: SingletonRaise<Any?>.() -> A,
  ): A {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    // This is safe because SingletonRaise never leaks the e from `raise(e: E)`, instead always calling `raise()`.
    // and hence the type parameter of SingletonRaise merely states what errors it accepts and ignores.
    @Suppress("UNCHECKED_CAST")
    return block(this as SingletonRaise<Any?>)
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
  ): A {
    contract {
      callsInPlace(block, InvocationKind.AT_MOST_ONCE)
      callsInPlace(recover, InvocationKind.AT_MOST_ONCE)
    }
    return result(block).fold(
      onSuccess = { it },
      onFailure = { recover(it) }
    )
  }
}

/**
 * Implementation of [Raise] used by [ior].
 * You should never use this directly.
 */
public class IorRaise<Error> @PublishedApi internal constructor(
  @PublishedApi internal val combineError: (Error, Error) -> Error,
  private val state: Atomic<Any?>,
  private val raise: Raise<Error>,
) : Raise<Error> by raise {
  @PublishedApi
  internal fun combine(e: Error): Error = state.update(
    function = { EmptyValue.combine(it, e, combineError) },
    transform = { _, new -> new }
  )

  @RaiseDSL
  public fun accumulate(value: Error): Unit = Ior.Both(value, Unit).bind()

  @RaiseDSL
  public fun <A> Either<Error, A>.getOrAccumulate(recover: (Error) -> A): A =
    fold(ifLeft = { Ior.Both(it, recover(it)) }, ifRight = { Ior.Right(it) }).bind()

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

  @RaiseDSL
  public inline fun <A> recover(
    @BuilderInference block: IorRaise<Error>.() -> A,
    recover: (error: Error) -> A,
  ): A {
    contract {
      callsInPlace(block, InvocationKind.AT_MOST_ONCE)
      callsInPlace(recover, InvocationKind.AT_MOST_ONCE)
    }
    val state: Atomic<Any?> = Atomic(EmptyValue)
    return recover<Error, A>({
      try {
        block(IorRaise(combineError, state, this))
      } finally {
        val accumulated = state.get()
        if (accumulated != EmptyValue) {
          @Suppress("UNCHECKED_CAST")
          combine(accumulated as Error)
        }
      }
    }, recover)
  }
}
