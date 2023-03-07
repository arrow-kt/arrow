@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:Suppress("DEPRECATION")
@file:JvmMultifileClass
@file:JvmName("RaiseKt")

package arrow.core.raise

import arrow.core.Either
import arrow.core.Validated
import arrow.core.continuations.EffectScope
import arrow.core.identity
import arrow.core.nonFatalOrThrow
import arrow.core.recover
import kotlin.coroutines.cancellation.CancellationException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
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
 * fun recovered(): Int =
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
 * fun recovered(): Int = recover({ failure() }) { _: String -> 1 }
 * -->
 * ```kotlin
 * fun test() {
 *   val either: Either<Nothing, Int> = either { failure() }
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

  @Deprecated("Use raise instead", ReplaceWith("raise(r)"))
  public fun <B> shift(r: R): B = raise(r)

  @RaiseDSL
  public suspend fun <B> arrow.core.continuations.Effect<R, B>.bind(): B =
    fold({ raise(it) }, ::identity)

  // Added for source compatibility with EffectScope / EagerScope
  @RaiseDSL
  public suspend fun <B> arrow.core.continuations.EagerEffect<R, B>.bind(): B =
    fold({ raise(it) }, ::identity)

  @Deprecated(
    "Use recover or effect & recover instead",
    ReplaceWith("effect { f() }")
  )
  public suspend fun <E, A> attempt(
    @BuilderInference
    f: suspend EffectScope<E>.() -> A,
  ): arrow.core.continuations.Effect<E, A> = arrow.core.continuations.effect(f)

  @Deprecated(
    "Use recover or effect & recover instead",
    ReplaceWith("this.recover { recover() }")
  )
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

  @RaiseDSL
  public fun <A> EagerEffect<R, A>.bind(): A = invoke(this@Raise)

  /**
   * Invoke an [Effect] inside `this` [Raise] context.
   * Any _logical failure_ raised are raised in `this` [Raise] context,
   * and thus short-circuits the computation.
   *
   * @see [recover] if you want to attempt to recover from any _logical failure_.
   */
  public suspend operator fun <A> Effect<R, A>.invoke(): A = invoke(this@Raise)

  @RaiseDSL
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
  @RaiseDSL
  public fun <A> Either<R, A>.bind(): A = when (this) {
    is Either.Left -> raise(value)
    is Either.Right -> value
  }


  @Deprecated(
    "Validated is deprecated in favor of Either.",
    ReplaceWith("toEither().bind()")
  )
  @RaiseDSL
  public fun <A> Validated<R, A>.bind(): A = when (this) {
    is Validated.Invalid -> raise(value)
    is Validated.Valid -> value
  }

  @RaiseDSL
  public suspend infix fun <E, A> Effect<E, A>.recover(@BuilderInference resolve: suspend Raise<R>.(E) -> A): A =
    fold<E, A, A>({ this@recover.invoke(this) }, { throw it }, { resolve(it) }) { it }

  /** @see [recover] */
  @RaiseDSL
  public infix fun <E, A> EagerEffect<E, A>.recover(@BuilderInference resolve: Raise<R>.(E) -> A): A =
    recover({ invoke() }) { resolve(it) }

  /**
   * Execute the [Effect] resulting in [A],
   * and recover from any _logical error_ of type [E], and [Throwable], by providing a fallback value of type [A],
   * or raising a new error of type [R].
   *
   * @see [catch] if you don't need to recover from [Throwable].
   */
  @RaiseDSL
  public suspend fun <E, A> Effect<E, A>.recover(
    @BuilderInference recover: suspend Raise<R>.(E) -> A,
    @BuilderInference catch: suspend Raise<R>.(Throwable) -> A,
  ): A = recover({ invoke() }, { recover(it) }) { catch(it) }

  @RaiseDSL
  public suspend infix fun <A> Effect<R, A>.catch(
    @BuilderInference catch: suspend Raise<R>.(Throwable) -> A,
  ): A = catch({ invoke() }) { catch(it) }

  @RaiseDSL
  public infix fun <A> EagerEffect<R, A>.catch(
    @BuilderInference catch: Raise<R>.(Throwable) -> A,
  ): A = catch({ invoke() }) { catch(it) }
}

/**
 * Execute the [Raise] context function resulting in [A] or any _logical error_ of type [E],
 * and recover by providing a transform [E] into a fallback value of type [A].
 * Base implementation of `effect { f() } getOrElse { fallback() }`.
 *
 * <!--- INCLUDE
 * import arrow.core.Either
 * import arrow.core.raise.either
 * import arrow.core.raise.recover
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * fun test() {
 *   recover({ raise("failed") }) { str -> str.length } shouldBe 6
 *
 *   either<Int, String> {
 *     recover({ raise("failed") }) { str -> raise(-1) }
 *   } shouldBe Either.Left(-1)
 * }
 * ```
 * <!--- KNIT example-raise-dsl-05.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@RaiseDSL
public inline fun <E, A> recover(
  @BuilderInference action: Raise<E>.() -> A,
  @BuilderInference recover: (E) -> A,
): A = fold(action, { throw it }, recover, ::identity)

/**
 * Execute the [Raise] context function resulting in [A] or any _logical error_ of type [E],
 * and [recover] by providing a transform [E] into a fallback value of type [A],
 * or [catch] any unexpected exceptions by providing a transform [Throwable] into a fallback value of type [A],
 *
 * <!--- INCLUDE
 * import arrow.core.raise.recover
 * import arrow.core.raise.Raise
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * fun test() {
 *   recover(
 *     { raise("failed") },
 *     { str -> str.length }
 *   ) { t -> t.message ?: -1 } shouldBe 6
 *
 *   fun Raise<String>.boom(): Int = throw RuntimeException("BOOM")
 *
 *   recover(
 *     { boom() },
 *     { str -> str.length }
 *   ) { t -> t.message?.length ?: -1 } shouldBe 4
 * }
 * ```
 * <!--- KNIT example-raise-dsl-06.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@RaiseDSL
public inline fun <E, A> recover(
  @BuilderInference action: Raise<E>.() -> A,
  @BuilderInference recover: (E) -> A,
  @BuilderInference catch: (Throwable) -> A,
): A = fold(action, catch, recover, ::identity)

/**
 * Execute the [Raise] context function resulting in [A] or any _logical error_ of type [E],
 * and [recover] by providing a transform [E] into a fallback value of type [A],
 * or [catch] any unexpected exceptions by providing a transform [Throwable] into a fallback value of type [A],
 *
 * <!--- INCLUDE
 * import arrow.core.raise.recover
 * import arrow.core.raise.Raise
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * fun test() {
 *   recover(
 *     { raise("failed") },
 *     { str -> str.length }
 *   ) { t -> t.message ?: -1 } shouldBe 6
 *
 *   fun Raise<String>.boom(): Int = throw RuntimeException("BOOM")
 *
 *   recover(
 *     { boom() },
 *     { str -> str.length }
 *   ) { t: RuntimeException -> t.message?.length ?: -1 } shouldBe 4
 * }
 * ```
 * <!--- KNIT example-raise-dsl-07.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@RaiseDSL
@JvmName("recoverReified")
public inline fun <reified T : Throwable, E, A> recover(
  @BuilderInference action: Raise<E>.() -> A,
  @BuilderInference recover: (E) -> A,
  @BuilderInference catch: (T) -> A,
): A = fold(action, { t -> if (t is T) catch(t) else throw t }, recover, ::identity)

/**
 * Allows safely catching exceptions without capturing [CancellationException],
 * or fatal exceptions like `OutOfMemoryError` or `VirtualMachineError` on the JVM.
 *
 * <!--- INCLUDE
 * import arrow.core.Either
 * import arrow.core.raise.either
 * import arrow.core.raise.catch
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * fun test() {
 *   catch({ throw RuntimeException("BOOM") }) { t ->
 *     "fallback"
 *   } shouldBe "fallback"
 *
 *   fun fetchId(): Int = throw RuntimeException("BOOM")
 *
 *   either {
 *     catch({ fetchId() }) { t ->
 *       raise("something went wrong: ${t.message}")
 *     }
 *   } shouldBe Either.Left("something went wrong: BOOM")
 * }
 * ```
 * <!--- KNIT example-raise-dsl-08.kt -->
 * <!--- TEST lines.isEmpty() -->
 *
 * Alternatively, you can use `try { } catch { }` blocks with [nonFatalOrThrow].
 * This API offers a similar syntax as the top-level [catch] functions like [Either.catch].
 */
@RaiseDSL
public inline fun <A> catch(action: () -> A, catch: (Throwable) -> A): A =
  try {
    action()
  } catch (t: Throwable) {
    catch(t.nonFatalOrThrow())
  }

/**
 * Allows safely catching exceptions of type `T` without capturing [CancellationException],
 * or fatal exceptions like `OutOfMemoryError` or `VirtualMachineError` on the JVM.
 *
 * <!--- INCLUDE
 * import arrow.core.Either
 * import arrow.core.raise.either
 * import arrow.core.raise.catch
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * fun test() {
 *   catch({ throw RuntimeException("BOOM") }) { t ->
 *     "fallback"
 *   } shouldBe "fallback"
 *
 *   fun fetchId(): Int = throw RuntimeException("BOOM")
 *
 *   either {
 *     catch({ fetchId() }) { t: RuntimeException ->
 *       raise("something went wrong: ${t.message}")
 *     }
 *   } shouldBe Either.Left("something went wrong: BOOM")
 * }
 * ```
 * <!--- KNIT example-raise-dsl-09.kt -->
 * <!--- TEST lines.isEmpty() -->
 *
 * Alternatively, you can use `try { } catch(e: T) { }` blocks.
 * This API offers a similar syntax as the top-level [catch] functions like [Either.catch].
 */
@RaiseDSL
@JvmName("catchReified")
public inline fun <reified T : Throwable, A> catch(action: () -> A, catch: (T) -> A): A =
  catch(action) { t: Throwable -> if (t is T) catch(t) else throw t }

@RaiseDSL
public inline fun <R> Raise<R>.ensure(condition: Boolean, raise: () -> R) {
  contract {
    callsInPlace(raise, AT_MOST_ONCE)
    returns() implies condition
  }
  return if (condition) Unit else raise(raise())
}

@RaiseDSL
public inline fun <R, B : Any> Raise<R>.ensureNotNull(value: B?, raise: () -> R): B {
  contract {
    callsInPlace(raise, AT_MOST_ONCE)
    returns() implies (value != null)
  }
  return value ?: raise(raise())
}
