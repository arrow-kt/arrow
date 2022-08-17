@file:JvmMultifileClass
@file:JvmName("Effect")
@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package arrow.core.continuations

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
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public inline fun <E, A> either(@BuilderInference block: Shift<E>.() -> A): Either<E, A> =
  fold({ block.invoke(this) }, { Either.Left(it) }, { Either.Right(it) })

public inline fun <A> nullable(block: NullableShift.() -> A): A? =
  fold({ block(NullableShift(this)) }, { null }, ::identity)

public inline fun <A> result(action: ResultShift.() -> A): Result<A> =
  fold({ action(ResultShift(this)) }, Result.Companion::failure, Result.Companion::success)

public inline fun <A> option(action: OptionShift.() -> A): Option<A> =
  fold({ action(OptionShift(this)) }, ::identity, ::Some)

public inline fun <E, A> ior(semigroup: Semigroup<E>, @BuilderInference action: IorShift<E>.() -> A): Ior<E, A> =
  fold<Option<E>, E, A, Ior<E, A>>(
    None,
    { action(IorShift(semigroup, this)) },
    { _, e -> throw e },
    { state, e -> Ior.Left(state.getOrElse { e }) },
    { state, a -> state.fold({ Ior.Right(a) }, { Ior.Both(it, a) }) }
  )

@JvmInline
public value class NullableShift(private val cont: Shift<Nothing?>) : Shift<Nothing?> {
  @EffectDSL
  public fun ensure(value: Boolean): Unit = ensure(value) { null }
  override fun <B> shift(r: Nothing?): B = cont.shift(r)
  public fun <B> Option<B>.bind(): B = bind { shift(null) }
  
  public fun <B> B?.bind(): B {
    contract { returns() implies (this@bind != null) }
    return this ?: shift(null)
  }
  
  public fun <B> ensureNotNull(value: B?): B {
    contract { returns() implies (value != null) }
    return ensureNotNull(value) { null }
  }
}

@JvmInline
public value class ResultShift(private val cont: Shift<Throwable>) : Shift<Throwable> {
  override fun <B> shift(r: Throwable): B = cont.shift(r)
  public fun <B> Result<B>.bind(): B = fold(::identity) { shift(it) }
}

@JvmInline
public value class OptionShift(private val cont: Shift<None>) : Shift<None> {
  override fun <B> shift(r: None): B = cont.shift(r)
  public fun <B> Option<B>.bind(): B = bind { shift(None) }
  public fun ensure(value: Boolean): Unit = ensure(value) { None }
  
  public fun <B> ensureNotNull(value: B?): B {
    contract { returns() implies (value != null) }
    return ensureNotNull(value) { None }
  }
}

public class IorShift<E> @PublishedApi internal constructor(
  semigroup: Semigroup<E>,
  @PublishedApi
  internal val effect: StateShift<Option<E>, E>,
) : Shift<E>, Semigroup<E> by semigroup {
  
  override fun <B> shift(r: E): B = effect.shift(combine(r))
  
  public fun <B> Ior<E, B>.bind(): B =
    when (this) {
      is Ior.Left -> shift(value)
      is Ior.Right -> value
      is Ior.Both -> {
        combine(leftValue)
        rightValue
      }
    }

  private fun combine(other: E): E =
    effect.updateAndGet { state ->
      state.map { e -> e.combine(other) }.orElse { Some(other) }
    }.getOrElse { other }
}
