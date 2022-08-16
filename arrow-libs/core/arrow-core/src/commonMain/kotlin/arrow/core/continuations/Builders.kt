@file:JvmMultifileClass
@file:JvmName("Effect")

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

public typealias EagerEffect<R, A> = Shift<R>.() -> A

public typealias Effect<R, A> = suspend Shift<R>.() -> A

@OptIn(ExperimentalTypeInference::class)
public inline fun <R, A> eagerEffect(@BuilderInference noinline f: Shift<R>.() -> A): EagerEffect<R, A> = f

@OptIn(ExperimentalTypeInference::class)
public inline fun <R, A> effect(@BuilderInference noinline f: suspend Shift<R>.() -> A): Effect<R, A> = f

public inline fun <E, A> either(action: Shift<E>.() -> A): Either<E, A> =
  fold<E, A, Either<E, A>>(
    { action() },
    { throw it },
    { Either.Left(it) },
    { Either.Right(it) }
  )

public inline fun <A> nullable(f: NullableShift.() -> A): A? =
  fold<Nothing?, A, A?>(
    { f(NullableShift(this)) },
    { throw it },
    { null },
    { it }
  )

@JvmInline
public value class NullableShift(private val cont: Shift<Nothing?>) : Shift<Nothing?> {
  @EffectDSL
  public fun ensure(value: Boolean): Unit = ensure(value) { null }
  override fun <B> shift(r: Nothing?): B = cont.shift(r)
  
  public fun <B> Option<B>.bind(): B = bind { shift(null) }
  
  @OptIn(ExperimentalContracts::class)
  public fun <B> B?.bind(): B {
    contract { returns() implies (this@bind != null) }
    return this ?: shift(null)
  }
  
  @OptIn(ExperimentalContracts::class)
  public fun <B> ensureNotNull(value: B?): B {
    contract { returns() implies (value != null) }
    return ensureNotNull(value) { null }
  }
}

public inline fun <A> result(action: ResultShift.() -> A): Result<A> =
  fold<Throwable, A, Result<A>>(
    { action(ResultShift(this)) },
    { throw it },
    { Result.failure(it) },
    { Result.success(it) }
  )

@JvmInline
public value class ResultShift(private val cont: Shift<Throwable>) : Shift<Throwable> {
  override fun <B> shift(r: Throwable): B = cont.shift(r)
  public fun <B> Result<B>.bind(): B = fold(::identity) { shift(it) }
}

public inline fun <A> option(action: OptionShift.() -> A): Option<A> =
  fold<None, A, Option<A>>(
    { action(OptionShift(this)) },
    { throw it },
    { None },
    { Some(it) }
  )

@JvmInline
public value class OptionShift(private val cont: Shift<None>) : Shift<None> {
  override fun <B> shift(r: None): B =
    cont.shift(r)
  
  // public fun <B> Option<B>.bind(): B = bind { None }
  
  public fun ensure(value: Boolean): Unit =
    ensure(value) { None }
  
  @OptIn(ExperimentalContracts::class)
  public fun <B> ensureNotNull(value: B?): B {
    contract { returns() implies (value != null) }
    return ensureNotNull(value) { None }
  }
}

public inline fun <E, A> ior(
  semigroup: Semigroup<E>,
  action: IorShift<E>.() -> A,
): Ior<E, A> = fold<E, Ior<E, A>, Ior<E, A>>(
  {
    val effect = IorShift(semigroup, this)
    val res = action(effect)
    val leftState = effect.leftState.get()
    if (leftState === EmptyValue) Ior.Right(res) else Ior.Both(EmptyValue.unbox(leftState), res)
  },
  { throw it },
  { Ior.Left(it) },
  { it }
)

public class IorShift<E>(semigroup: Semigroup<E>, private val effect: Shift<E>) : Shift<E>, Semigroup<E> by semigroup {
  
  @PublishedApi
  internal var leftState: AtomicRef<Any?> = AtomicRef(EmptyValue)
  
  private fun combine(other: E): E =
    leftState.updateAndGet { state ->
      if (state === EmptyValue) other else EmptyValue.unbox<E>(state).combine(other)
    } as E
  
  public fun <B> Ior<E, B>.bind(): B =
    when (this) {
      is Ior.Left -> shift(value)
      is Ior.Right -> value
      is Ior.Both -> {
        combine(leftValue)
        rightValue
      }
    }
  
  override fun <B> shift(r: E): B = effect.shift(combine(r))
}
