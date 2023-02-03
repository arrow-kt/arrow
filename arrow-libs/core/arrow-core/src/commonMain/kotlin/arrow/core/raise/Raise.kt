@file:OptIn(ExperimentalTypeInference::class)

package arrow.core.raise

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.continuations.EffectScope
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

@DslMarker
public annotation class RaiseDSL

/**
 * <!--- TEST_NAME RaiseKnitTest -->
 *
 * The [Raise] DSL allows you to work with _logical failures_ of type [R].
 * A _logical failure_ does not necessarily mean that the computation has failed,
 * but that it has stopped or _short-circuited_.
 *
 * The [Raise] DSL allows you to [raise] _logical failure_ of type [R], and you can [recover] from them.
 *
 * <!--- INCLUDE
 * import arrow.core.raise.Raise
 * import arrow.core.raise.recover
 * -->
 * ```kotlin
 * fun Raise<String>.failure(): Int = raise("failed")
 *
 * fun Raise<Nothing>.recovered(): Int =
 *   recover({ failure() }) { _: String -> 1 }
 * ```
 * <!--- KNIT example-raise-dsl-01.kt -->
 *
 * Above we defined a function `failure` that raises a logical failure of type [String] with value `"failed"`.
 * And in the function `recovered` we recover from the failure by providing a fallback value,
 * and resolving the error type [String] to [Nothing]. Meaning we can track that we recovered from the error in the type.
 *
 * Since we defined programs in terms of [Raise] they _seamlessly work with any of the builders_ available in Arrow,
 * or any you might build for your custom types.
 *
 * <!--- INCLUDE
 * import arrow.core.Either
 * import arrow.core.Ior
 * import arrow.core.raise.Effect
 * import arrow.core.raise.Raise
 * import arrow.core.raise.either
 * import arrow.core.raise.effect
 * import arrow.core.raise.ior
 * import arrow.core.raise.toEither
 * import arrow.typeclasses.Semigroup
 * import io.kotest.matchers.shouldBe
 *
 * fun Raise<String>.failure(): Int = raise("failed")
 * -->
 * ```kotlin
 * suspend fun test() {
 *   val either: Either<String, Int> =
 *     either { failure() }
 *
 *   val effect: Effect<String, Int> =
 *     effect { failure() }
 *
 *   val ior: Ior<String, Int> =
 *     ior(Semigroup.string()) { failure() }
 *
 *   either shouldBe Either.Left("failed")
 *   effect.toEither() shouldBe Either.Left("failed")
 *   ior shouldBe Ior.Left("failed")
 * }
 * ```
 * <!--- KNIT example-raise-dsl-02.kt -->
 * <!--- TEST lines.isEmpty() -->
 *
 * And we can apply the same technique to recover from the failures using the [Raise] DSL based error handlers available in Arrow.
 *
 * <!--- INCLUDE
 * import arrow.core.Either
 * import arrow.core.raise.Raise
 * import arrow.core.raise.either
 * import arrow.core.raise.recover
 * import arrow.core.recover
 * import io.kotest.matchers.shouldBe
 *
 * fun Raise<String>.failure(): Int = raise("failed")
 *
 * fun Raise<Nothing>.recovered(): Int = recover({ failure() }) { _: String -> 1 }
 * -->
 * ```kotlin
 * fun test() {
 *   val either = either { failure() }
 *     .recover { _: String -> recovered() }
 *
 *   either shouldBe Either.Right(1)
 * }
 * ```
 * <!--- KNIT example-raise-dsl-03.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public interface Raise<in R> {
  
  /** Raise a _logical failure_ of type [R] */
  @RaiseDSL
  public fun raise(r: R): Nothing
  
  // Added for source compatibility with EffectScope / EagerScope
  @Deprecated("Use raise instead", ReplaceWith("raise(r)"))
  public fun <B> shift(r: R): B = raise(r)
  
  // Added for source compatibility with EffectScope / EagerScope
  public suspend fun <B> arrow.core.continuations.Effect<R, B>.bind(): B =
    fold({ raise(it) }, ::identity)
  
  // Added for source compatibility with EffectScope / EagerScope
  public suspend fun <B> arrow.core.continuations.EagerEffect<R, B>.bind(): B =
    fold({ raise(it) }, ::identity)
  
  // Added for source compatibility with EffectScope / EagerScope
  @OptIn(ExperimentalTypeInference::class)
  public suspend fun <E, A> attempt(
    @BuilderInference
    f: suspend EffectScope<E>.() -> A,
  ): arrow.core.continuations.Effect<E, A> = arrow.core.continuations.effect(f)
  
  // Added for source compatibility with EffectScope / EagerScope
  public suspend infix fun <E, A> arrow.core.continuations.Effect<E, A>.catch(
    recover: suspend Raise<R>.(E) -> A,
  ): A = fold({ recover(it) }, ::identity)
  
  /**
   * Invoke an [EagerEffect] inside `this` [Raise] context.
   * Any _logical failure_ is raised in `this` [Raise] context,
   * and thus short-circuits the computation.
   *
   * @see [recover] if you want to attempt to recover from any _logical failure_.
   */
  public operator fun <A> EagerEffect<R, A>.invoke(): A = invoke(this@Raise)
  public fun <A> EagerEffect<R, A>.bind(): A = invoke(this@Raise)
  
  /**
   * Invoke an [Effect] inside `this` [Raise] context.
   * Any _logical failure_ raised are raised in `this` [Raise] context,
   * and thus short-circuits the computation.
   *
   * @see [recover] if you want to attempt to recover from any _logical failure_.
   */
  public suspend operator fun <A> Effect<R, A>.invoke(): A = invoke(this@Raise)
  public suspend fun <A> Effect<R, A>.bind(): A = invoke(this@Raise)
  
  /**
   * Extract the [Either.Right] value of an [Either].
   * Any encountered [Either.Left] will be raised as a _logical failure_ in `this` [Raise] context.
   * You can wrap the [bind] call in [recover] if you want to attempt to recover from any _logical failure_.
   *
   * <!--- INCLUDE
   * import arrow.core.Either
   * import arrow.core.right
   * import arrow.core.raise.either
   * import arrow.core.raise.recover
   * import io.kotest.matchers.shouldBe
   * -->
   * ```kotlin
   * fun test() {
   *   val one: Either<Nothing, Int> = 1.right()
   *   val left: Either<String, Int> = Either.Left("failed")
   *
   *   either {
   *     val x = one.bind()
   *     val y = recover({ left.bind() }) { failure : String -> 1 }
   *     x + y
   *   } shouldBe Either.Right(2)
   * }
   * ```
   * <!--- KNIT example-raise-dsl-04.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  public fun <A> Either<R, A>.bind(): A = when (this) {
    is Either.Left -> raise(value)
    is Either.Right -> value
  }
  
  /* Will be removed in subsequent PRs for Arrow 2.x.x */
  public fun <A> Validated<R, A>.bind(): A = when (this) {
    is Validated.Invalid -> raise(value)
    is Validated.Valid -> value
  }
  
  /**
   * Extract the [Result.success] value out of [Result],
   * because [Result] works with [Throwable] as its error type you need to [transform] [Throwable] to [R].
   *
   * Note that this functions can currently not be _inline_ without Context Receivers,
   * and thus doesn't allow suspension in its error handler.
   * To do so, use [Result.recover] and [bind].
   *
   * <!--- INCLUDE
   * import arrow.core.Either
   * import arrow.core.raise.either
   * import arrow.core.raise.recover
   * import kotlinx.coroutines.delay
   * import io.kotest.matchers.shouldBe
   * -->
   * ```kotlin
   * suspend fun test() {
   *   val one: Result<Int> = Result.success(1)
   *   val failure: Result<Int> = Result.failure(RuntimeException("Boom!"))
   *
   *   either {
   *     val x = one.bind { -1 }
   *     val y = failure.bind { failure: Throwable ->
   *       raise("Something bad happened: ${failure.message}")
   *     }
   *     val z = failure.recover { failure: Throwable ->
   *       delay(10)
   *       1
   *     }.bind { raise("Something bad happened: ${it.message}") }
   *     x + y + z
   *   } shouldBe Either.Left("Something bad happened: Boom!")
   * }
   * ```
   * <!--- KNIT example-raise-dsl-05.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  public fun <A> Result<A>.bind(transform: (Throwable) -> R): A =
    fold(::identity) { throwable -> raise(transform(throwable)) }
  
  /**
   * Extract the [Some] value out of [Option],
   * because [Option] works with [None] as its error type you need to [transform] [None] to [R].
   *
   * Note that this functions can currently not be _inline_ without Context Receivers,
   * and thus doesn't allow suspension in its error handler.
   * To do so, use [Option.recover] and [bind].
   *
   * <!--- INCLUDE
   * import arrow.core.Either
   * import arrow.core.None
   * import arrow.core.Option
   * import arrow.core.recover
   * import arrow.core.raise.either
   * import kotlinx.coroutines.delay
   * import io.kotest.matchers.shouldBe
   * -->
   * ```kotlin
   * suspend fun test() {
   *   val empty: Option<Int> = None
   *   either {
   *     val x: Int = empty.bind { _: None -> 1 }
   *     val y: Int = empty.bind { _: None -> raise("Something bad happened: Boom!") }
   *     val z: Int = empty.recover { _: None ->
   *       delay(10)
   *       1
   *     }.bind { raise("Something bad happened: Boom!") }
   *     x + y + z
   *   } shouldBe Either.Left("Something bad happened: Boom!")
   * }
   * ```
   * <!--- KNIT example-raise-dsl-06.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  public fun <A> Option<A>.bind(transform: Raise<R>.(None) -> A): A =
    when (this) {
      None -> transform(None)
      is Some -> value
    }

  @RaiseDSL
  public suspend infix fun <E, A> Effect<E, A>.recover(@BuilderInference resolve: suspend Raise<R>.(E) -> A): A =
    recover({ invoke() }) { resolve(it) }
  
  /** @see [recover] */
  @RaiseDSL
  public infix fun <E, A> EagerEffect<E, A>.recover(@BuilderInference resolve: Raise<R>.(E) -> A): A =
    recover({ invoke() }, resolve)
  
  /**
   * Execute the [Effect] resulting in [A],
   * and recover from any _logical error_ of type [E], and [Throwable], by providing a fallback value of type [A],
   * or raising a new error of type [R].
   *
   * @see [catch] if you don't need to recover from [Throwable].
   */
  @RaiseDSL
  public suspend fun <E, A> Effect<E, A>.recover(
    @BuilderInference action: suspend Raise<E>.() -> A,
    @BuilderInference recover: suspend Raise<R>.(E) -> A,
    @BuilderInference catch: suspend Raise<R>.(Throwable) -> A,
  ): A = fold({ action(this) }, { catch(it) }, { recover(it) }, { it })
  
  @RaiseDSL
  public suspend fun <A> Effect<R, A>.catch(
    @BuilderInference catch: suspend Raise<R>.(Throwable) -> A,
  ): A = fold({ catch(it) }, { raise(it) }, { it })
  
  @RaiseDSL
  public fun <A> EagerEffect<R, A>.catch(
    @BuilderInference catch: Raise<R>.(Throwable) -> A,
  ): A = fold({ catch(it) }, { raise(it) }, { it })
}

/**
 * Execute the [Raise] context function resulting in [A] or any _logical error_ of type [E],
 * and recover by providing a fallback value of type [A] or raising a new error of type [R].
 *
 * <!--- INCLUDE
 * import arrow.core.Either
 * import arrow.core.raise.either
 * import arrow.core.raise.recover
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * suspend fun test() {
 *   either<Nothing, Int> {
 *     recover({ raise("failed") }) { str -> str.length }
 *   } shouldBe Either.Right(6)
 *
 *   either {
 *     recover({ raise("failed") }) { str -> raise(-1) }
 *   } shouldBe Either.Left(-1)
 * }
 * ```
 * <!--- KNIT example-raise-dsl-07.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@RaiseDSL
public inline fun <R, E, A> Raise<R>.recover(
  @BuilderInference action: Raise<E>.() -> A,
  @BuilderInference recover: Raise<R>.(E) -> A,
): A = fold<E, A, A>({ action(this) }, { throw it }, { recover(it) }, { it })

@RaiseDSL
public inline fun <R, E, A> Raise<R>.recover(
  @BuilderInference action: Raise<E>.() -> A,
  @BuilderInference recover: Raise<R>.(E) -> A,
  @BuilderInference catch: Raise<R>.(Throwable) -> A,
): A = fold({ action(this) }, { catch(it) }, { recover(it) }, { it })

@RaiseDSL
public inline fun <R, A> Raise<R>.catch(
  @BuilderInference action: Raise<R>.() -> A,
  @BuilderInference catch: Raise<R>.(Throwable) -> A,
): A = fold({ action(this) }, { catch(it) }, { raise(it) }, { it })

@RaiseDSL
@JvmName("catchReified")
public inline fun <reified T : Throwable, R, A> Raise<R>.catch(
  @BuilderInference action: Raise<R>.() -> A,
  @BuilderInference catch: Raise<R>.(T) -> A,
): A = catch(action) { t: Throwable -> if (t is T) catch(t) else throw t }

@RaiseDSL
public inline fun <R> Raise<R>.ensure(condition: Boolean, raise: () -> R): Unit =
  if (condition) Unit else raise(raise())

@OptIn(ExperimentalContracts::class)
@RaiseDSL
public inline fun <R, B : Any> Raise<R>.ensureNotNull(value: B?, raise: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: raise(raise())
}
