@file:JvmMultifileClass
@file:JvmName("RaiseKt")
@file:OptIn(ExperimentalTypeInference::class)
package arrow.core.raise

import arrow.core.nonFatalOrThrow
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Catch the raised value [Error] of the `Effect`.
 * You can either return a value a new value of [A],
 * or short-circuit the effect by raising with a value of [OtherError],
 * or raise an exception into [suspend].
 *
 * ```kotlin
 * import arrow.core.raise.effect
 * import arrow.core.raise.recover
 *
 * object User
 * object Error
 *
 * val error = effect<Error, User> { raise(Error) } // Raise(error)
 *
 * val a = error.recover<Error, Error, User> { error -> User } // Success(User)
 * val b = error.recover<Error, String, User> { error -> raise("other-failure") } // Raise(other-failure)
 * val c = error.recover<Error, Nothing, User> { error -> throw RuntimeException("BOOM") } // Exception(BOOM)
 * ```
 * <!--- KNIT example-effect-error-01.kt -->
 */
public infix fun <Error, OtherError, A> Effect<Error, A>.recover(@BuilderInference recover: suspend Raise<OtherError>.(error: Error) -> A): Effect<OtherError, A> =
  effect { recover({ invoke() }) { recover(it) } }

/**
 * Catch any unexpected exceptions, and [catch] them.
 * You can either return a value a new value of [A],
 * or short-circuit the effect by raising with a value of [Error],
 * or raise an exception into [suspend].
 *
 * ```kotlin
 * import arrow.core.raise.effect
 * import arrow.core.raise.catch
 *
 * object User
 * object Error
 *
 * val exception = effect<Error, User> { throw RuntimeException("BOOM") }  // Exception(BOOM)
 *
 * val a = exception.catch { error -> error.message?.length ?: -1 } // Success(5)
 * val b = exception.catch { raise(Error) } // Raise(error)
 * val c = exception.catch { throw  RuntimeException("other-failure") } // Exception(other-failure)
 * ```
 * <!--- KNIT example-effect-error-02.kt -->
 */
public infix fun <Error, A> Effect<Error, A>.catch(@BuilderInference catch: suspend Raise<Error>.(throwable: Throwable) -> A): Effect<Error, A> =
  effect { catch({ invoke() }) { catch(it) } }

/**
 * A version of [catch] that refines the [Throwable] to [T].
 * This is useful for wrapping foreign code, such as database, network calls, etc.
 *
 * ```kotlin
 * import arrow.core.raise.effect
 * import arrow.core.raise.catch
 *
 * object User
 * object Error
 *
 * val x = effect<Error, User> {
 *   throw IllegalArgumentException("builder missed args")
 * }.catch { raise(Error) }
 * ```
 *
 * If you don't need an `error` value when wrapping your foreign code you can use `Nothing` to fill the type parameter.
 *
 * ```kotlin
 * val y = effect<Nothing, User> {
 *   throw IllegalArgumentException("builder missed args")
 * }.catch<IllegalArgumentException, Error, User> { raise(Error) }
 * ```
 * <!--- KNIT example-effect-error-03.kt -->
 */
@JvmName("catchReified")
public inline infix fun <reified T : Throwable, Error, A> Effect<Error, A>.catch(
  @BuilderInference crossinline catch: suspend Raise<Error>.(t: T) -> A,
): Effect<Error, A> =
  effect { catch({ invoke() }) { t: T -> catch(t) } }

/** Runs the [Effect] and captures any [nonFatalOrThrow] exception into [Result]. */
public fun <Error, A> Effect<Error, A>.catch(): Effect<Error, Result<A>> =
  effect {
    catch({ Result.success(invoke()) }, Result.Companion::failure)
  }

public suspend inline infix fun <Error, A> Effect<Error, A>.getOrElse(recover: suspend (error: Error) -> A): A =
  recover({ invoke() }) { recover(it) }

/**
 * Transform the raised value [Error] of the `Effect` into [OtherError],
 * or raise an exception into [suspend].
 * This results in an `Effect` that returns a value of [A] or raises [OtherError].
 *
 * ```kotlin
 * import arrow.core.raise.effect
 * import arrow.core.raise.mapError
 *
 * object User
 * object Error
 *
 * val error = effect<Error, User> { raise(Error) } // Raise(error)
 *
 * val a = error.mapError<Error, String, User> { error -> "some-failure" } // Raise(some-failure)
 * val b = error.mapError<Error, String, User>(Any::toString) // Raise(Error)
 * val c = error.mapError<Error, Nothing, User> { error -> throw RuntimeException("BOOM") } // Exception(BOOM)
 * ```
 * <!--- KNIT example-effect-error-04.kt -->
 */
public infix fun <Error, OtherError, A> Effect<Error, A>.mapError(@BuilderInference transform: suspend (error: Error) -> OtherError): Effect<OtherError, A> =
  effect { withError({ transform(it) }) { invoke() } }

public infix fun <Error, OtherError, A> EagerEffect<Error, A>.recover(@BuilderInference recover: Raise<OtherError>.(error: Error) -> A): EagerEffect<OtherError, A> =
  eagerEffect { recover({ invoke() }) { recover(it) } }

public infix fun <Error, A> EagerEffect<Error, A>.catch(@BuilderInference catch: Raise<Error>.(throwable: Throwable) -> A): EagerEffect<Error, A> =
  eagerEffect { catch({ invoke() }) { catch(it) } }

@JvmName("catchReified")
public inline infix fun <reified T : Throwable, Error, A> EagerEffect<Error, A>.catch(
  @BuilderInference crossinline catch: Raise<Error>.(t: T) -> A,
): EagerEffect<Error, A> =
  eagerEffect { catch({ invoke() }) { t: T -> catch(t) } }

public inline infix fun <Error, A> EagerEffect<Error, A>.getOrElse(recover: (error: Error) -> A): A =
  recover({ invoke() }, recover)

/**
 * Transform the raised value [Error] of the `EagerEffect` into [OtherError].
 * This results in an `EagerEffect` that returns a value of [A] or raises [OtherError].
 *
 * ```kotlin
 * import arrow.core.raise.eagerEffect
 * import arrow.core.raise.mapError
 *
 * object User
 * object Error
 *
 * val error = eagerEffect<Error, User> { raise(Error) } // Raise(error)
 *
 * val a = error.mapError<Error, String, User> { error -> "some-failure" } // Raise(some-failure)
 * val b = error.mapError<Error, String, User>(Any::toString) // Raise(Error)
 * ```
 * <!--- KNIT example-effect-error-05.kt -->
 */
public infix fun <Error, OtherError, A> EagerEffect<Error, A>.mapError(@BuilderInference transform: (error: Error) -> OtherError): EagerEffect<OtherError, A> =
  eagerEffect { withError({ transform(it) }) { invoke() } }
