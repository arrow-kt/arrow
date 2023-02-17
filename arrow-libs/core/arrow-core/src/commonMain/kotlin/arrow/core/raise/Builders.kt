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
import arrow.core.orElse
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupDeprecation
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public inline fun <E, A> either(@BuilderInference block: Raise<E>.() -> A): Either<E, A> {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  return fold({ block.invoke(this) }, { Either.Left(it) }, { Either.Right(it) })
}

public inline fun <A> nullable(block: NullableRaise.() -> A): A? {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  return fold({ block(NullableRaise(this)) }, { null }, ::identity)
}

public inline fun <A> result(block: ResultRaise.() -> A): Result<A> {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  return fold({ block(ResultRaise(this)) }, Result.Companion::failure, Result.Companion::failure, Result.Companion::success)
}

public inline fun <A> option(block: OptionRaise.() -> A): Option<A> {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  return fold({ block(OptionRaise(this)) }, ::identity, ::Some)
}

public inline fun <E, A> ior(noinline combineError: (E, E) -> E, @BuilderInference block: IorRaise<E>.() -> A): Ior<E, A> {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  val state: Atomic<Option<E>> = Atomic(None)
  return fold<E, A, Ior<E, A>>(
    { block(IorRaise(combineError, state, this)) },
    { e -> throw e },
    { e -> Ior.Left(state.get().getOrElse { e }) },
    { a -> state.get().fold({ Ior.Right(a) }, { Ior.Both(it, a) }) }
  )
}

@Deprecated(SemigroupDeprecation, ReplaceWith("semigroup.run { ior({ x, y -> x + y}, block) }"))
public inline fun <E, A> ior(semigroup: Semigroup<E>, @BuilderInference block: IorRaise<E>.() -> A): Ior<E, A> =
  semigroup.run { ior({ x, y -> x + y}, block) }

public typealias Null = Nothing?

@JvmInline
public value class NullableRaise(private val cont: Raise<Null>) : Raise<Null> {
  @RaiseDSL
  public fun ensure(value: Boolean): Unit = ensure(value) { null }
  override fun raise(r: Nothing?): Nothing = cont.raise(r)
  public fun <B> Option<B>.bind(): B = bind { raise(null) }

  public fun <B> B?.bind(): B {
    contract { returns() implies (this@bind != null) }
    return this ?: raise(null)
  }

  public fun <B> ensureNotNull(value: B?): B {
    contract { returns() implies (value != null) }
    return ensureNotNull(value) { null }
  }
}

@JvmInline
public value class ResultRaise(private val cont: Raise<Throwable>) : Raise<Throwable> {
  override fun raise(r: Throwable): Nothing = cont.raise(r)
  public fun <B> Result<B>.bind(): B = fold(::identity) { raise(it) }
}

@JvmInline
public value class OptionRaise(private val cont: Raise<None>) : Raise<None> {
  override fun raise(r: None): Nothing = cont.raise(r)
  public fun <B> Option<B>.bind(): B = bind { raise(None) }
  public fun ensure(value: Boolean): Unit = ensure(value) { None }

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

  override fun raise(r: E): Nothing = raise.raise(combine(r))

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
      prev.map { combineError(it, other) }.orElse { Some(other) }
    }.getOrElse { other }
}
