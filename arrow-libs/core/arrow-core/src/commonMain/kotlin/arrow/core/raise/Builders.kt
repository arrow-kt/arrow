@file:JvmMultifileClass
@file:JvmName("Effect")
@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
package arrow.core.raise

import arrow.core.Either
import arrow.core.Ior
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.continuations.AtomicRef
import arrow.core.continuations.updateAndGet
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.orElse
import arrow.typeclasses.Semigroup
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public inline fun <E, A> either(@BuilderInference block: Raise<E>.() -> A): Either<E, A> =
  fold({ block.invoke(this) }, { Either.Left(it) }, { Either.Right(it) })

public inline fun <A> nullable(block: NullableRaise.() -> A): A? =
  fold({ block(NullableRaise(this)) }, { null }, ::identity)

public inline fun <A> result(action: ResultRaise.() -> A): Result<A> =
  fold({ action(ResultRaise(this)) }, Result.Companion::failure, Result.Companion::success)

public inline fun <A> option(action: OptionRaise.() -> A): Option<A> =
  fold({ action(OptionRaise(this)) }, ::identity, ::Some)

public inline fun <E, A> ior(semigroup: Semigroup<E>, @BuilderInference action: IorRaise<E>.() -> A): Ior<E, A> {
  val state: AtomicRef<Option<E>> = AtomicRef(None)
  return fold<E, A, Ior<E, A>>(
    { action(IorRaise(semigroup, state, this)) },
    { e -> throw e },
    { e -> Ior.Left(state.get().getOrElse { e }) },
    { a -> state.get().fold({ Ior.Right(a) }, { Ior.Both(it, a) }) }
  )
}

public typealias Null = Nothing?

@JvmInline
public value class NullableRaise(private val cont: Raise<Null>) : Raise<Null> {
  @EffectDSL
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
  semigroup: Semigroup<E>,
  private val state: AtomicRef<Option<E>>,
  private val raise: Raise<E>,
) : Raise<E>, Semigroup<E> by semigroup {
  
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
      prev.map { e -> e.combine(other) }.orElse { Some(other) }
    }.getOrElse { other }
}
