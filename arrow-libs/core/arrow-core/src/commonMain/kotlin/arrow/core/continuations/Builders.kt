@file:JvmMultifileClass
@file:JvmName("Effect")
@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package arrow.core.continuations

import arrow.core.Either
import arrow.core.EmptyValue
import arrow.core.Ior
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
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
  fold({ IorShift(semigroup, this).invoke(action) }, { Ior.Left(it) }, ::identity)

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

public class IorShift<E> @PublishedApi internal constructor(semigroup: Semigroup<E>, private val effect: Shift<E>) :
  Shift<E>, Semigroup<E> by semigroup {
  
  // TODO this is a mess...
  @PublishedApi
  internal var leftState: AtomicRef<Any?> = AtomicRef(EmptyValue)
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
  
  @PublishedApi
  internal inline operator fun <A> invoke(action: IorShift<E>.() -> A): Ior<E, A> {
    val res = action(this)
    val leftState = leftState.get()
    return if (leftState === EmptyValue) Ior.Right(res)
    else Ior.Both(EmptyValue.unbox(leftState), res)
  }
  
  @Suppress("UNCHECKED_CAST")
  private fun combine(other: E): E =
    leftState.updateAndGet { state ->
      if (state === EmptyValue) other else EmptyValue.unbox<E>(state).combine(other)
    } as E
}
