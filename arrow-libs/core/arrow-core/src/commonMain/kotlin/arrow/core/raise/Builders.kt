@file:JvmMultifileClass
@file:JvmName("RaiseKt")
@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package arrow.core.raise

import arrow.atomic.Atomic
import arrow.atomic.updateAndGet
import arrow.core.Either
import arrow.core.Ior
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

public inline fun <E, A> either(@BuilderInference block: Raise<E>.() -> A): Either<E, A> =
  fold({ block.invoke(this) }, { Either.Left(it) }, { Either.Right(it) })

public inline fun <A> nullable(block: NullableRaise.() -> A): A? =
  fold({ block(NullableRaise(this)) }, { null }, ::identity)

public inline fun <A> result(block: ResultRaise.() -> A): Result<A> =
  fold({ block(ResultRaise(this)) }, Result.Companion::failure, Result.Companion::failure, Result.Companion::success)

public inline fun <A> option(block: OptionRaise.() -> A): Option<A> =
  fold({ block(OptionRaise(this)) }, ::identity, ::Some)

public inline fun <E, A> ior(noinline combineError: (E, E) -> E, @BuilderInference block: IorRaise<E>.() -> A): Ior<E, A> {
  val state: Atomic<Option<E>> = Atomic(None)
  return fold<E, A, Ior<E, A>>(
    { block(IorRaise(combineError, state, this)) },
    { e -> throw e },
    { e -> Ior.Left(state.get().getOrElse { e }) },
    { a -> state.get().fold({ Ior.Right(a) }, { Ior.Both(it, a) }) }
  )
}

public typealias Null = Nothing?

public class NullableRaise(private val raise: Raise<Null>) : Raise<Null> by raise {
  @RaiseDSL
  public fun ensure(value: Boolean): Unit = ensure(value) { null }


  @RaiseDSL
  public fun <B> Option<B>.bind(): B = getOrElse { raise(null) }

  @RaiseDSL
  public fun <B> B?.bind(): B {
    contract { returns() implies (this@bind != null) }
    return this ?: raise(null)
  }

  @RaiseDSL
  @JvmName("bindAllNullable")
  public fun <A> Iterable<A?>.bindAll(): List<A> =
    map { it.bind() }

  @RaiseDSL
  public fun <B> ensureNotNull(value: B?): B {
    contract { returns() implies (value != null) }
    return ensureNotNull(value) { null }
  }
}

public class ResultRaise(private val raise: Raise<Throwable>) : Raise<Throwable> by raise {
  @RaiseDSL
  public fun <B> Result<B>.bind(): B = fold(::identity) { raise(it) }

  @RaiseDSL
  @JvmName("bindAllResult")
  public fun <A> Iterable<Result<A>>.bindAll(): List<A> =
    map { it.bind() }
}

public class OptionRaise(private val raise: Raise<None>) : Raise<None> by raise {
  @RaiseDSL
  public fun <B> Option<B>.bind(): B = getOrElse { raise(None) }

  @RaiseDSL
  @JvmName("bindAllOption")
  public fun <A> Iterable<Option<A>>.bindAll(): List<A> =
    map { it.bind() }

  @RaiseDSL
  public fun ensure(value: Boolean): Unit = ensure(value) { None }

  @RaiseDSL
  public fun <B> ensureNotNull(value: B?): B {
    contract { returns() implies (value != null) }
    return ensureNotNull(value) { None }
  }
}

public class IorRaise<E> @PublishedApi internal constructor(
  private val combineError: (E, E) -> E,
  private val state: Atomic<Option<E>>,
  private val raise: Raise<E>,
) : Raise<E> {

  @RaiseDSL
  override fun raise(r: E): Nothing = raise.raise(combine(r))

  @RaiseDSL
  @JvmName("bindAllIor")
  public fun <A> Iterable<Ior<E, A>>.bindAll(): List<A> =
    map { it.bind() }

  @RaiseDSL
  public fun <B> Ior<E, B>.bind(): B =
    when (this) {
      is Ior.Left -> raise(value)
      is Ior.Right -> value
      is Ior.Both -> {
        combine(leftValue)
        rightValue
      }
    }

  private fun combine(other: E): E =
    state.updateAndGet { prev ->
      Some(prev.map { combineError(it, other) }.getOrElse { other })
    }.getOrElse { other }
}
