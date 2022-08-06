package arrow.core.continuations

import arrow.core.Either
import arrow.core.EmptyValue
import arrow.core.Ior
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.identity
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/**
 * [RestrictsSuspension] version of [Effect]. This version runs eagerly, and can be used in
 * non-suspending code.
 * An [effect] computation interoperates with an [EagerEffect] via `bind`.
 * @see Effect
 */
public typealias EagerEffect<R, A> = suspend EagerShift<R>.() -> A

/**
 * Runs the non-suspending computation by creating a [Continuation] with an [EmptyCoroutineContext],
 * and running the `fold` function over the computation.
 *
 * When the [EagerEffect] has shifted with [R] it will [recover] the shifted value to [B], and when it
 * ran the computation to completion it will [transform] the value [A] to [B].
 *
 * ```kotlin
 * import arrow.core.continuations.eagerEffect
 * import arrow.core.continuations.fold
 * import io.kotest.matchers.shouldBe
 *
 * fun main() {
 *   val shift = eagerEffect<String, Int> {
 *     shift("Hello, World!")
 *   }.fold({ str: String -> str }, { int -> int.toString() })
 *   shift shouldBe "Hello, World!"
 *
 *   val res = eagerEffect<String, Int> {
 *     1000
 *   }.fold({ str: String -> str.length }, { int -> int })
 *   res shouldBe 1000
 * }
 * ```
 * <!--- KNIT example-eager-effect-01.kt -->
 */
public fun <R, A, B> EagerEffect<R, A>.fold(recover: (R) -> B, transform: (A) -> B): B =
  fold({ throw it }, recover, transform)

/**
 * [fold] the [EagerEffect] into an [Ior]. Where the shifted value [R] is mapped to [Ior.Left], and
 * result value [A] is mapped to [Ior.Right].
 */
public fun <R, A> EagerEffect<R, A>.toIor(): Ior<R, A> = fold({ Ior.Left(it) }) { Ior.Right(it) }

/**
 * [fold] the [EagerEffect] into an [Either]. Where the shifted value [R] is mapped to [Either.Left], and
 * result value [A] is mapped to [Either.Right].
 */
public fun <R, A> EagerEffect<R, A>.toEither(): Either<R, A> = fold({ Either.Left(it) }) { Either.Right(it) }

/**
 * [fold] the [EagerEffect] into an [Validated]. Where the shifted value [R] is mapped to
 * [Validated.Invalid], and result value [A] is mapped to [Validated.Valid].
 */
public fun <R, A> EagerEffect<R, A>.toValidated(): Validated<R, A> =
  fold({ Validated.Invalid(it) }) { Validated.Valid(it) }

/**
 * [fold] the [EagerEffect] into an [A?]. Where the shifted value [R] is mapped to
 * [null], and result value [A].
 */
public fun <R, A> EagerEffect<R, A>.orNull(): A? = fold({ null }, ::identity)

/**
 * [fold] the [EagerEffect] into an [Option]. Where the shifted value [R] is mapped to [Option] by the
 * provided function [orElse], and result value [A] is mapped to [Some].
 */
public fun <R, A> EagerEffect<R, A>.toOption(orElse: (R) -> Option<A>): Option<A> =
  fold(orElse, ::Some)

public fun <R, A> EagerEffect<R, A>.attempt(): EagerEffect<R, Result<A>> = eagerEffect {
  kotlin.runCatching { bind() }
}

/**
 * DSL for constructing `EagerEffect<R, A>` values
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Validated
 * import arrow.core.continuations.eagerEffect
 * import arrow.core.continuations.fold
 * import io.kotest.assertions.fail
 * import io.kotest.matchers.shouldBe
 *
 * fun main() {
 *   eagerEffect<String, Int> {
 *     val x = Either.Right(1).bind()
 *     val y = Validated.Valid(2).bind()
 *     val z = Option(3).bind { "Option was empty" }
 *     x + y + z
 *   }.fold({ fail("Shift can never be the result") }, { it shouldBe 6 })
 *
 *   eagerEffect<String, Int> {
 *     val x = Either.Right(1).bind()
 *     val y = Validated.Valid(2).bind()
 *     val z: Int = None.bind { "Option was empty" }
 *     x + y + z
 *   }.fold({ it shouldBe "Option was empty" }, { fail("Int can never be the result") })
 * }
 * ```
 * <!--- KNIT example-eager-effect-02.kt -->
 */
@OptIn(ExperimentalTypeInference::class)
public fun <R, A> eagerEffect(@BuilderInference f: suspend EagerShift<R>.() -> A): EagerEffect<R, A> = f

public fun <A> EagerEffect<A, A>.merge(): A = fold(::identity, ::identity)

/**
 * Catch the shifted value [E] of the `Effect`.
 * You can either return a value a new value of [A],
 * or short-circuit the effect by shifting with a value of [E],
 * or raise an exception into [suspend].
 *
 * ```kotlin
 * import arrow.core.continuations.eagerEffect
 * import arrow.core.continuations.catch
 *
 * object User
 * object Error
 *
 * val error = eagerEffect<Error, User> { shift(Error) } // // Shift(error)
 *
 * val a = error.catch<Error, Error, User> { error -> User } // Success(User)
 * val b = error.catch<Error, String, User> { error -> shift("other-failure") } // Shift(other-failure)
 * val c = error.catch<Error, Nothing, User> { error -> throw RuntimeException("BOOM") } // Exception(BOOM)
 * ```
 * <!--- KNIT example-eager-01.kt -->
 */
@OptIn(ExperimentalTypeInference::class)
public infix fun <E, E2, A> EagerEffect<E, A>.catch(@BuilderInference resolve: suspend EagerShift<E2>.(E) -> A): EagerEffect<E2, A> =
  eagerEffect {
    var left: Any? = EmptyValue
    var right: Any? = EmptyValue
    fold({ r -> left = r }, { a -> right = a })
    if (left === EmptyValue) EmptyValue.unbox(right)
    else resolve(EmptyValue.unbox(left))
  }

/**
 * Attempt to run the effect, and [recover] from any unexpected exceptions.
 * You can either return a value a new value of [A],
 * or short-circuit the effect by shifting with a value of [E],
 * or raise an exception into [suspend].
 *
 * ```kotlin
 * import arrow.core.continuations.eagerEffect
 * import arrow.core.continuations.attempt
 *
 * object User
 * object Error
 *
 * val exception = eagerEffect<Error, User> { throw RuntimeException("BOOM") }  // Exception(BOOM)
 *
 * val a = exception.attempt { error -> error.message?.length ?: -1 } // Success(5)
 * val b = exception.attempt { shift(Error) } // Shift(error)
 * val c = exception.attempt { throw  RuntimeException("other-failure") } // Exception(other-failure)
 * ```
 * <!--- KNIT example-eager-02.kt -->
 */
@OptIn(ExperimentalTypeInference::class)
public infix fun <E, A> EagerEffect<E, A>.attempt(@BuilderInference recover: suspend EagerShift<E>.(Throwable) -> A): EagerEffect<E, A> =
  eagerEffect {
    var exception: Any? = EmptyValue
    var left: Any? = EmptyValue
    var right: Any? = EmptyValue
    fold({ e -> exception = e }, { r -> left = r }, { a -> right = a })
    when {
      exception !== EmptyValue -> recover(EmptyValue.unbox(exception))
      left !== EmptyValue -> shift(EmptyValue.unbox(left))
      else -> EmptyValue.unbox(right)
    }
  }

/**
 * A version of [attempt] that refines the [Throwable] to [T].
 * This is useful for wrapping foreign code, such as database, network calls, etc.
 *
 * ```kotlin
 * import arrow.core.continuations.eagerEffect
 * import arrow.core.continuations.attempt
 *
 * object User
 * object Error
 *
 * val x = eagerEffect<Error, User> {
 *   throw IllegalArgumentException("builder missed args")
 * }.attempt<IllegalArgumentException, Error, User> { shift(Error) }
 * ```
 *
 * If you don't need an `error` value when wrapping your foreign code you can use `Nothing` to fill the type parameter.
 *
 * ```kotlin
 * val y = eagerEffect<Nothing, User> {
 *   throw IllegalArgumentException("builder missed args")
 * }.attempt<IllegalArgumentException, Error, User> { shift(Error) }
 * ```
 * <!--- KNIT example-eager-03.kt -->
 */
@OptIn(ExperimentalTypeInference::class)
@JvmName("attemptOrThrow")
public inline infix fun <reified T : Throwable, E, A> EagerEffect<E, A>.attempt(@BuilderInference crossinline recover: suspend EagerShift<E>.(T) -> A): EagerEffect<E, A> =
  attempt { e -> if (e is T) recover(e) else throw e }
