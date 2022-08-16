@file:JvmMultifileClass
@file:JvmName("Effect")
@file:OptIn(ExperimentalTypeInference::class)

package arrow.core.continuations

import arrow.core.nonFatalOrThrow
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@BuilderInference
public infix fun <E, E2, A> Effect<E, A>.catch(resolve: suspend Shift<E2>.(shifted: E) -> A): Effect<E2, A> =
  effect { catch { e -> resolve(e) } }

@BuilderInference
public infix fun <E, A> Effect<E, A>.attempt(recover: suspend Shift<E>.(throwable: Throwable) -> A): Effect<E, A> =
  effect { attempt({ invoke() }) { t -> recover(t) } }

@BuilderInference
@JvmName("attemptOrThrow")
public inline infix fun <reified T : Throwable, E, A> Effect<E, A>.attempt(
  crossinline recover: suspend Shift<E>.(T) -> A,
): Effect<E, A> =
  effect { attempt({ invoke() }) { t: T -> recover(t) } }

public  fun <E, A> Effect<E, A>.attempt(): Effect<E, Result<A>> =
  effect{ kotlin.runCatching { invoke() }.onFailure { it.nonFatalOrThrow() } }

@BuilderInference
public infix fun <E, E2, A> EagerEffect<E, A>.catch(resolve: Shift<E2>.(shifted: E) -> A): EagerEffect<E2, A> =
  eagerEffect { catch { e -> resolve(e) } }

@BuilderInference
public infix fun <E, A> EagerEffect<E, A>.attempt(recover: Shift<E>.(throwable: Throwable) -> A): EagerEffect<E, A> =
  eagerEffect { attempt({ invoke() }) { t -> recover(t) } }

@JvmName("attemptOrThrow")
public inline infix fun <reified T : Throwable, E, A> EagerEffect<E, A>.attempt(
  @BuilderInference crossinline recover: Shift<E>.(T) -> A,
): EagerEffect<E, A> =
  eagerEffect { attempt({ invoke() }) { t: T -> recover(t) } }
