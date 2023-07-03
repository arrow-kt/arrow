@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:Suppress("DEPRECATION")
@file:JvmMultifileClass
@file:JvmName("RaiseKt")

package arrow.core.raise

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.Validated
import arrow.core.ValidatedDeprMsg
import arrow.core.continuations.EffectScope
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.nonFatalOrThrow
import arrow.core.recover
import kotlin.coroutines.cancellation.CancellationException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@DslMarker
public annotation class RaiseDSL

/**
 * <!--- TEST_NAME RaiseKnitTest -->
 *
 * The [Raise] DSL allows you to work with _logical failures_ of type [Error].
 * A _logical failure_ does not necessarily mean that the computation has failed,
 * but that it has stopped or _short-circuited_.
 *
 * The [Raise] DSL allows you to [raise] _logical failure_ of type [Error], and you can [recover] from them.
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
 * And in the function `recovered` we recover from the failure by providing a fallback value, and resolving the error type [String].
 *
 * You can also use the [recover] function inside the [Raise] DSL to transform the error type to a different type such as `List<Char>`.
 * And you can do the same for other data types such as `Effect`, `Either`, etc. using [getOrElse] as an alternative to [recover].
 *
 * <!--- INCLUDE
 * import arrow.core.getOrElse
 * import arrow.core.left
 * import arrow.core.raise.Raise
 * import arrow.core.raise.effect
 * import arrow.core.raise.recover
 * import arrow.core.raise.getOrElse
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * fun Raise<String>.failure(): Int = raise("failed")
 *
 * fun Raise<List<Char>>.recovered(): Int =
 *   recover({ failure() }) { msg: String -> raise(msg.toList()) }
 *
 * suspend fun Raise<List<Char>>.recovered2(): Int =
 *   effect { failure() } getOrElse { msg: String -> raise(msg.toList()) }
 *
 * fun Raise<List<Char>>.recovered3(): Int =
 *   "failed".left() getOrElse { msg: String -> raise(msg.toList()) }
 *
 * fun test(): Unit {
 *   recover({ "failed".left().bind() }) { 1 } shouldBe "failed".left().getOrElse { 1 }
 * }
 * ```
 * <!--- KNIT example-raise-dsl-02.kt -->
 * <!--- TEST lines.isEmpty() -->
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
 *     ior(String::plus) { failure() }
 *
 *   either shouldBe Either.Left("failed")
 *   effect.toEither() shouldBe Either.Left("failed")
 *   ior shouldBe Ior.Left("failed")
 * }
 * ```
 * <!--- KNIT example-raise-dsl-03.kt -->
 * <!--- TEST lines.isEmpty() -->
 *
 * Arrow also exposes [Raise] based error handlers for the most common data types,
 * which allows to recover from _logical failures_ whilst transforming the error type.
 *
 * <!--- INCLUDE
 * import arrow.core.Either
 * import arrow.core.getOrElse
 * import arrow.core.raise.Raise
 * import arrow.core.raise.either
 * import arrow.core.raise.recover
 * import arrow.core.recover
 * import arrow.core.right
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * fun Raise<String>.failure(): Int = raise("failed")
 *
 * fun test() {
 *   val failure: Either<String, Int> = either { failure() }
 *
 *   failure.recover { _: String -> 1.right().bind() } shouldBe Either.Right(1)
 *
 *   failure.recover { msg: String -> raise(msg.toList()) } shouldBe Either.Left(listOf('f', 'a', 'i', 'l', 'e', 'd'))
 *
 *   recover({ failure.bind() }) { 1 } shouldBe failure.getOrElse { 1 }
 * }
 * ```
 * <!--- KNIT example-raise-dsl-04.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
public interface Raise<in Error> {

  /** Raise a _logical failure_ of type [Error] */
  @RaiseDSL
  public fun raise(r: Error): Nothing

  @Deprecated("Use raise instead", ReplaceWith("raise(r)"))
  public fun <A> shift(r: Error): A = raise(r)

  @RaiseDSL
  public suspend fun <A> arrow.core.continuations.Effect<Error, A>.bind(): A =
    fold({ raise(it) }, ::identity)

  // Added for source compatibility with EffectScope / EagerScope
  @RaiseDSL
  public suspend fun <A> arrow.core.continuations.EagerEffect<Error, A>.bind(): A =
    fold({ raise(it) }, ::identity)

  @Deprecated(
    "Use getOrElse on Raise, Effect or EagerEffect instead.",
    ReplaceWith("effect { f() }")
  )
  public suspend fun <OtherError, A> attempt(
    @BuilderInference
    f: suspend EffectScope<OtherError>.() -> A,
  ): arrow.core.continuations.Effect<OtherError, A> = arrow.core.continuations.effect(f)

  @Deprecated(
    "Use getOrElse on Raise, Effect or EagerEffect instead.",
    ReplaceWith("fold({ recover(it) }, ::identity)")
  )
  public suspend infix fun <OtherError, A> arrow.core.continuations.Effect<OtherError, A>.catch(
    recover: suspend Raise<Error>.(otherError: OtherError) -> A,
  ): A = fold({ recover(it) }, ::identity)

  /**
   * Invoke an [EagerEffect] inside `this` [Raise] context.
   * Any _logical failure_ is raised in `this` [Raise] context,
   * and thus short-circuits the computation.
   *
   * @see [recover] if you want to attempt to recover from any _logical failure_.
   */
  public operator fun <A> EagerEffect<Error, A>.invoke(): A = invoke(this@Raise)

  @RaiseDSL
  public fun <A> EagerEffect<Error, A>.bind(): A = invoke(this@Raise)

  /**
   * Invoke an [Effect] inside `this` [Raise] context.
   * Any _logical failure_ raised are raised in `this` [Raise] context,
   * and thus short-circuits the computation.
   *
   * @see [recover] if you want to attempt to recover from any _logical failure_.
   */
  public suspend operator fun <A> Effect<Error, A>.invoke(): A = invoke(this@Raise)

  @RaiseDSL
  public suspend fun <A> Effect<Error, A>.bind(): A = invoke(this@Raise)

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
   * <!--- KNIT example-raise-dsl-05.kt -->
   * <!--- TEST lines.isEmpty() -->
   */
  @RaiseDSL
  public fun <A> Either<Error, A>.bind(): A = when (this) {
    is Either.Left -> raise(value)
    is Either.Right -> value
  }

  public fun <K, A> Map<K, Either<Error, A>>.bindAll(): Map<K, A> =
    mapValues { (_, a) -> a.bind() }

  @Deprecated(ValidatedDeprMsg, ReplaceWith("toEither().bind()"))
  @RaiseDSL
  public fun <A> Validated<Error, A>.bind(): A = when (this) {
    is Validated.Invalid -> raise(value)
    is Validated.Valid -> value
  }

  @RaiseDSL
  public fun <A> Iterable<Either<Error, A>>.bindAll(): List<A> =
    map { it.bind() }

  @RaiseDSL
  public fun <A> NonEmptyList<Either<Error, A>>.bindAll(): NonEmptyList<A> =
    map { it.bind() }

  @RaiseDSL
  public fun <A> NonEmptySet<Either<Error, A>>.bindAll(): NonEmptySet<A> =
    map { it.bind() }
}

/**
 * Execute the [Raise] context function resulting in [A] or any _logical error_ of type [Error],
 * and recover by providing a transform [Error] into a fallback value of type [A].
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
 * <!--- KNIT example-raise-dsl-06.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@RaiseDSL
public inline fun <Error, A> recover(
  @BuilderInference block: Raise<Error>.() -> A,
  @BuilderInference recover: (error: Error) -> A,
): A = fold(block, { throw it }, recover, ::identity)

/**
 * Execute the [Raise] context function resulting in [A] or any _logical error_ of type [Error],
 * and [recover] by providing a transform [Error] into a fallback value of type [A],
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
 * <!--- KNIT example-raise-dsl-07.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@RaiseDSL
public inline fun <Error, A> recover(
  @BuilderInference block: Raise<Error>.() -> A,
  @BuilderInference recover: (error: Error) -> A,
  @BuilderInference catch: (throwable: Throwable) -> A,
): A = fold(block, catch, recover, ::identity)

/**
 * Execute the [Raise] context function resulting in [A] or any _logical error_ of type [Error],
 * and [recover] by providing a transform [Error] into a fallback value of type [A],
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
 * <!--- KNIT example-raise-dsl-08.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@RaiseDSL
@JvmName("recoverReified")
public inline fun <reified T : Throwable, Error, A> recover(
  @BuilderInference block: Raise<Error>.() -> A,
  @BuilderInference recover: (error: Error) -> A,
  @BuilderInference catch: (t: T) -> A,
): A = fold(block, { t -> if (t is T) catch(t) else throw t }, recover, ::identity)

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
 * <!--- KNIT example-raise-dsl-09.kt -->
 * <!--- TEST lines.isEmpty() -->
 *
 * Alternatively, you can use `try { } catch { }` blocks with [nonFatalOrThrow].
 * This API offers a similar syntax as the top-level [catch] functions like [Either.catch].
 */
@RaiseDSL
public inline fun <A> catch(block: () -> A, catch: (throwable: Throwable) -> A): A =
  try {
    block()
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
 * <!--- KNIT example-raise-dsl-10.kt -->
 * <!--- TEST lines.isEmpty() -->
 *
 * Alternatively, you can use `try { } catch(e: T) { }` blocks.
 * This API offers a similar syntax as the top-level [catch] functions like [Either.catch].
 */
@RaiseDSL
@JvmName("catchReified")
public inline fun <reified T : Throwable, A> catch(block: () -> A, catch: (t: T) -> A): A =
  catch(block) { t: Throwable -> if (t is T) catch(t) else throw t }

@RaiseDSL
public inline fun <Error> Raise<Error>.ensure(condition: Boolean, raise: () -> Error) {
  contract {
    callsInPlace(raise, AT_MOST_ONCE)
    returns() implies condition
  }
  return if (condition) Unit else raise(raise())
}

@RaiseDSL
public inline fun <Error, B : Any> Raise<Error>.ensureNotNull(value: B?, raise: () -> Error): B {
  contract {
    callsInPlace(raise, AT_MOST_ONCE)
    returns() implies (value != null)
  }
  return value ?: raise(raise())
}

/**
 * Execute the [Raise] context function resulting in [A] or any _logical error_ of type [OtherError],
 * and transform any raised [OtherError] into [Error], which is raised to the outer [Raise].
 *
 * <!--- INCLUDE
 * import arrow.core.Either
 * import arrow.core.raise.either
 * import arrow.core.raise.withError
 * import io.kotest.matchers.shouldBe
 * -->
 * ```kotlin
 * fun test() {
 *   either<Int, String> {
 *     withError(String::length) {
 *       raise("failed")
 *     }
 *   } shouldBe Either.Left(6)
 * }
 * ```
 * <!--- KNIT example-raise-dsl-11.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@RaiseDSL
public inline fun <Error, OtherError, A> Raise<Error>.withError(
  transform: (OtherError) -> Error,
  @BuilderInference block: Raise<OtherError>.() -> A
): A {
  contract {
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return recover(block) { raise(transform(it)) }
}

/**
 * Execute the [Raise] context function resulting in [A] or any _logical error_ of type [A].
 * Does not distinguish between normal results and errors, thus you can consider
 * `return` and `raise` to be semantically equivalent inside.
 *
 * <!--- INCLUDE
 * import arrow.core.raise.merge
 * import io.kotest.matchers.shouldBe
 * import kotlin.random.Random
 * -->
 * ```kotlin
 * fun test() {
 *   merge { if(Random.nextBoolean()) raise("failed") else "failed" } shouldBe "failed"
 * }
 * ```
 * <!--- KNIT example-raise-dsl-12.kt -->
 * <!--- TEST lines.isEmpty() -->
 */
@RaiseDSL
@JvmName("_merge")
public inline fun <A> merge(
  @BuilderInference block: Raise<A>.() -> A,
): A = recover(block, ::identity)
