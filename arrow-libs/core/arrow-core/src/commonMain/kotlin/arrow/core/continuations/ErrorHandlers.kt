@file:JvmMultifileClass
@file:JvmName("Effect")
@file:OptIn(ExperimentalTypeInference::class)

package arrow.core.continuations

import arrow.core.nonFatalOrThrow
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Catch the shifted value [E] of the `Effect`.
 * You can either return a value a new value of [A],
 * or short-circuit the effect by shifting with a value of [E],
 * or raise an exception into [suspend].
 *
 * ```kotlin
 * import arrow.core.continuations.effect
 * import arrow.core.continuations.recover
 *
 * object User
 * object Error
 *
 * val error = effect<Error, User> { shift(Error) } // // Shift(error)
 *
 * val a = error.recover<Error, Error, User> { error -> User } // Success(User)
 * val b = error.recover<Error, String, User> { error -> shift("other-failure") } // Shift(other-failure)
 * val c = error.recover<Error, Nothing, User> { error -> throw RuntimeException("BOOM") } // Exception(BOOM)
 * ```
 * <!--- KNIT example-effect-error-01.kt -->
 */
public infix fun <E, E2, A> Effect<E, A>.recover(@BuilderInference resolve: suspend Shift<E2>.(shifted: E) -> A): Effect<E2, A> =
  effect { recover(resolve) }

/**
 * Catch any unexpected exceptions, and [resolve] them.
 * You can either return a value a new value of [A],
 * or short-circuit the effect by shifting with a value of [E],
 * or raise an exception into [suspend].
 *
 * ```kotlin
 * import arrow.core.continuations.effect
 * import arrow.core.continuations.catch
 *
 * object User
 * object Error
 *
 * val exception = effect<Error, User> { throw RuntimeException("BOOM") }  // Exception(BOOM)
 *
 * val a = exception.catch { error -> error.message?.length ?: -1 } // Success(5)
 * val b = exception.catch { shift(Error) } // Shift(error)
 * val c = exception.catch { throw  RuntimeException("other-failure") } // Exception(other-failure)
 * ```
 * <!--- KNIT example-effect-error-02.kt -->
 */
public infix fun <E, A> Effect<E, A>.catch(@BuilderInference resolve: suspend Shift<E>.(throwable: Throwable) -> A): Effect<E, A> =
  effect { catch(resolve) }

/**
 * A version of [catch] that refines the [Throwable] to [T].
 * This is useful for wrapping foreign code, such as database, network calls, etc.
 *
 * ```kotlin
 * import arrow.core.continuations.effect
 * import arrow.core.continuations.catch
 *
 * object User
 * object Error
 *
 * val x = effect<Error, User> {
 *   throw IllegalArgumentException("builder missed args")
 * }.catch { shift(Error) }
 * ```
 *
 * If you don't need an `error` value when wrapping your foreign code you can use `Nothing` to fill the type parameter.
 *
 * ```kotlin
 * val y = effect<Nothing, User> {
 *   throw IllegalArgumentException("builder missed args")
 * }.catch<IllegalArgumentException, Error, User> { shift(Error) }
 * ```
 * <!--- KNIT example-effect-error-03.kt -->
 */
@JvmName("catchReified")
public inline infix fun <reified T : Throwable, E, A> Effect<E, A>.catch(
  @BuilderInference crossinline recover: suspend Shift<E>.(T) -> A,
): Effect<E, A> =
  effect { catch { t: Throwable -> if (t is T) recover(t) else throw t } }

/** Runs the [Effect] and captures any [nonFatalOrThrow] exception into [Result]. */
public fun <E, A> Effect<E, A>.catch(): Effect<E, Result<A>> =
  effect {
    try {
      Result.success(invoke())
    } catch (e: Throwable) {
      Result.failure(e.nonFatalOrThrow())
    }
  }

public infix fun <E, E2, A> EagerEffect<E, A>.recover(@BuilderInference resolve: Shift<E2>.(shifted: E) -> A): EagerEffect<E2, A> =
  eagerEffect { recover(resolve) }

public infix fun <E, A> EagerEffect<E, A>.catch(@BuilderInference recover: Shift<E>.(throwable: Throwable) -> A): EagerEffect<E, A> =
  eagerEffect { catch(recover) }

@JvmName("catchReified")
public inline infix fun <reified T : Throwable, E, A> EagerEffect<E, A>.catch(
  @BuilderInference crossinline recover: Shift<E>.(T) -> A,
): EagerEffect<E, A> =
  eagerEffect { catch { t: Throwable -> if (t is T) recover(t) else throw t } }
