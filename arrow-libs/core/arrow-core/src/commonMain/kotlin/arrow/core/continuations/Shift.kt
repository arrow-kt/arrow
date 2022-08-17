@file:OptIn(ExperimentalTypeInference::class)

package arrow.core.continuations

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

@DslMarker
public annotation class EffectDSL

public interface Shift<in R> {
  public fun <A> shift(r: R): A
  
  public fun <A> EagerEffect<R, A>.bind(): A = invoke(this@Shift)
  public operator fun <A> EagerEffect<R, A>.invoke(): A = invoke(this@Shift)
  
  public suspend fun <A> Effect<R, A>.bind(): A = invoke(this@Shift)
  public suspend operator fun <A> Effect<R, A>.invoke(): A = invoke(this@Shift)
  
  public fun <A> Either<R, A>.bind(): A = when (this) {
    is Either.Left -> shift(value)
    is Either.Right -> value
  }
  
  public fun <A> Validated<R, A>.bind(): A = when (this) {
    is Validated.Invalid -> shift(value)
    is Validated.Valid -> value
  }
  
  // TODO can be inlined with context receivers, and top-level
  public fun <A> Option<A>.bind(transform: Shift<R>.(None) -> A): A =
    when (this) {
      None -> transform(None)
      is Some -> value
    }
  
  // TODO can be inlined with context receivers, and top-level
  public fun <A> Result<A>.bind(transform: (Throwable) -> R): A =
    fold(::identity) { throwable -> shift(transform(throwable)) }
  
  @EffectDSL
  public suspend infix fun <E, A> Effect<E, A>.catch(@BuilderInference resolve: suspend Shift<R>.(E) -> A): A =
    catch({ invoke() }) { resolve(it) }
  
  @EffectDSL
  public infix fun <E, A> EagerEffect<E, A>.catch(@BuilderInference resolve: Shift<R>.(E) -> A): A =
    catch({ invoke() }, resolve)
  
  @EffectDSL
  public suspend fun <E, A> Effect<E, A>.catch(
    @BuilderInference action: suspend Shift<E>.() -> A,
    @BuilderInference resolve: suspend Shift<R>.(E) -> A,
    @BuilderInference recover: suspend Shift<R>.(Throwable) -> A,
  ): A = fold({ action(this) }, { recover(it) }, { resolve(it) }, { it })
  
  @EffectDSL
  public suspend fun <A> Effect<R, A>.attempt(
    @BuilderInference recover: Shift<R>.(Throwable) -> A,
  ): A = fold({ recover(it) }, { shift(it) }, { it })
}

@EffectDSL
public inline fun <R, E, A> Shift<R>.catch(
  @BuilderInference action: Shift<E>.() -> A,
  @BuilderInference resolve: Shift<R>.(E) -> A
): A = fold<E, A, A>({ action(this) }, { throw it }, { resolve(it) }, { it })

@EffectDSL
public inline fun <R, E, A> Shift<R>.catch(
  @BuilderInference action: Shift<E>.() -> A,
  @BuilderInference resolve: Shift<R>.(E) -> A,
  @BuilderInference recover: Shift<R>.(Throwable) -> A,
): A = fold({ action(this) }, { recover(it) }, { resolve(it) }, { it })

@EffectDSL
public inline fun <R, A> Shift<R>.attempt(
  @BuilderInference action: Shift<R>.() -> A,
  @BuilderInference recover: Shift<R>.(Throwable) -> A,
): A = fold({ action(this) }, { recover(it) }, { shift(it) }, { it })

@EffectDSL
@JvmName("attemptOrThrow")
public inline fun <reified T : Throwable, R, A> Shift<R>.attempt(
  @BuilderInference action: Shift<R>.() -> A,
  @BuilderInference recover: Shift<R>.(T) -> A,
): A = attempt(action) { if (it is T) recover(it) else throw it }

@EffectDSL
public inline fun <R> Shift<R>.ensure(condition: Boolean, shift: () -> R): Unit =
  if (condition) Unit else shift(shift())

@OptIn(ExperimentalContracts::class)
@EffectDSL
public inline fun <R, B : Any> Shift<R>.ensureNotNull(value: B?, shift: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: shift(shift())
}
