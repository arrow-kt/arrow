@file:OptIn(ExperimentalTypeInference::class)

package arrow.core.continuations

import arrow.core.Either
import arrow.core.EmptyValue
import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.emptyCombine
import arrow.core.identity
import arrow.core.nel
import arrow.typeclasses.Semigroup
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

@DslMarker
public annotation class EffectDSL

/**
 * The [Raise] DSL allows you to work with _logical failures_ of type [R].
 * A _logical failure_ does not necessarily mean that the computation has failed,
 * but that it has stopped or _short-circuited_.
 *
 * The [Raise] DSL allows you to [raise] _logical failure_ of type [R], and you can [recover] from them.
 *
 * ```kotlin
 * fun Raise<String>.failure(): Int = raise("failed")
 *
 * fun Raise<Nothing>.recovered(): String =
 *   recover({ program() }) { failure: String ->
 *     "Recovered from $failure"
 *   }
 * ```
 *
 * Above we defined a function `failure` that raises a logical failure of type [String] with value `"failed"`.
 * And in the function `recovered` we recover from the failure by providing a fallback value,
 * and resolving the error type [String] to [Nothing]. Meaning we can track that we recovered from the error in the type.
 *
 * Since we defined programs in terms of [Raise] they _seamlessly work with any of the builders_ available in Arrow,
 * or any you might build for your custom types.
 *
 * ```kotlin
 * fun main() {
 *   val either: Either<String, Int> =
 *     either { failure() } // returns Left("failed")
 *
 *   val effect: Effect<String, Int> =
 *     effect { failure() }
 *
 *   val ior: Ior<String, Int> =
 *     ior { failure() }
 *
 *   println(either)
 * }
 * ```
 *
 * ```text
 * Either.Right(Recovered from failed)
 * ```
 */
public interface Raise<in R> {
  
  /** Raise a _logical failure_ of type [R] */
  public fun raise(r: R): Nothing

  /**
   * Accumulate the errors obtained by executing the [block]
   * over every element of [this] using the given [semigroup].
   */
  public fun <A, B> Iterable<A>.mapOrAccumulate(
    semigroup: Semigroup<@UnsafeVariance R>,
    block: Raise<R>.(A) -> B
  ): List<B> {
    var error: Any? = EmptyValue
    val results = mutableListOf<B>()
    forEach {
      fold<R, B, Unit>({
        block(it)
      }, { newError ->
        error = semigroup.emptyCombine(error, newError)
      }, {
        results.add(it)
      })
    }
    when (val e = error) {
      is EmptyValue -> return results
      else -> raise(EmptyValue.unbox(e))
    }
  }
  
  /**
   * Invoke an [EagerEffect] inside `this` [Raise] context.
   * Any _logical failure_ raised are raised in `this` [Raise] context,
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
   * ```kotlin
   * fun main() {
   *   val left: Either<String, Int> = Either.Left("failed")
   *   either {
   *     recover({ left.bind() }) { failure : String ->
   *       "Recovered from $failure"
   *     }
   *   }.also(::println)
   * ```
   * ```text
   * Either.Right(Recovered from failed)
   * ```
   */
  public fun <A> Either<R, A>.bind(): A = when (this) {
    is Either.Left -> raise(value)
    is Either.Right -> value
  }
  
  /**
   * Extract the [Result.success] value out of [Result],
   * because [Result] works with [Throwable] as its error type you need to [transform] [Throwable] to [R].
   *
   * Note that this functions can currently not be _inline_ without Context Receivers,
   * but you can rely on [recover] if you want to run any suspend code in your error handler.
   *
   * ```kotlin
   * fun main() {
   *   val failure: Result<Int> = Result.failure(RuntimeException("Boom!"))
   *   either {
   *     recover({ failure.bind() }) { failure: Throwable ->
   *       delay(10)
   *       raise("Something bad happened: ${failure.message}")
   *     }
   *   }.also(::println)
   * ```
   * ```text
   * Either.Left(Something bad happened: Boom!)
   * ```
   */
  public fun <A> Result<A>.bind(transform: (Throwable) -> R): A =
    fold(::identity) { throwable -> raise(transform(throwable)) }
  
  /**
   * Extract the [Some] value out of [Option],
   * because [Option] works with [None] as its error type you need to [transform] [None] to [R].
   *
   * Note that this functions can currently not be _inline_ without Context Receivers,
   * but you can rely on [recover] if you want to run any suspend code in your error handler.
   *
   * ```kotlin
   * fun main() {
   *   val empty: Option<Int> = None
   *   either {
   *     recover({ empty.bind() }) { failure: None ->
   *       delay(10)
   *       raise("Something bad happened: ${failure.message}")
   *     }
   *   }.also(::println)
   * ```
   * ```text
   * Either.Left(Something bad happened: Boom!)
   * ```
   */
  public fun <A> Option<A>.bind(transform: Raise<R>.(None) -> A): A =
    when (this) {
      None -> transform(None)
      is Some -> value
    }
  
  /**
   * Execute the [Effect] resulting in [A],
   * and recover from any _logical error_ of type [E] by providing a fallback value of type [A],
   * or raising a new error of type [R].
   *
   * ```kotlin
   * suspend fun main() {
   *   either<Nothing, Int> {
   *     effect { raise("failed") }.recover { str -> str.length }
   *   }.also(::println)
   *
   *   either<Int, Nothing> {
   *     effect { raise("failed") }.recover { str -> raise(-1) }
   *   }.also(::println)
   * }
   * ```
   * ```text
   * Either.Right(6)
   * Either.Left(-1)
   * ```
   */
  @EffectDSL
  public suspend infix fun <E, A> Effect<E, A>.recover(@BuilderInference resolve: suspend Raise<R>.(E) -> A): A =
    recover({ invoke() }) { resolve(it) }
  
  /** @see [recover] */
  @EffectDSL
  public infix fun <E, A> EagerEffect<E, A>.recover(@BuilderInference resolve: Raise<R>.(E) -> A): A =
    recover({ invoke() }, resolve)
  
  /**
   * Execute the [Effect] resulting in [A],
   * and recover from any _logical error_ of type [E], and [Throwable], by providing a fallback value of type [A],
   * or raising a new error of type [R].
   *
   * @see [catch] if you don't need to recover from [Throwable].
   */
  @EffectDSL
  public suspend fun <E, A> Effect<E, A>.recover(
    @BuilderInference action: suspend Raise<E>.() -> A,
    @BuilderInference recover: suspend Raise<R>.(E) -> A,
    @BuilderInference catch: suspend Raise<R>.(Throwable) -> A,
  ): A = fold({ action(this) }, { catch(it) }, { recover(it) }, { it })

  @EffectDSL
  public suspend fun <A> Effect<R, A>.catch(
    @BuilderInference catch: suspend Raise<R>.(Throwable) -> A,
  ): A = fold({ catch(it) }, { raise(it) }, { it })
  
  @EffectDSL
  public fun <A> EagerEffect<R, A>.catch(
    @BuilderInference catch: Raise<R>.(Throwable) -> A,
  ): A = fold({ catch(it) }, { raise(it) }, { it })
}

/**
 * Execute an [action], and map every error of type [E]
 * into one of type [R].
 */
@EffectDSL
public inline fun <R, E, A> Raise<R>.mapLeft(
  @BuilderInference action: Raise<E>.() -> A,
  @BuilderInference error: (E) -> R
): A = recover(action) { raise(error(it)) }

@EffectDSL
public inline fun <R, E, A> Raise<R>.recover(
  @BuilderInference action: Raise<E>.() -> A,
  @BuilderInference recover: Raise<R>.(E) -> A,
): A = fold<E, A, A>({ action(this) }, { throw it }, { recover(it) }, { it })

@EffectDSL
public inline fun <R, E, A> Raise<R>.recover(
  @BuilderInference action: Raise<E>.() -> A,
  @BuilderInference recover: Raise<R>.(E) -> A,
  @BuilderInference catch: Raise<R>.(Throwable) -> A,
): A = fold({ action(this) }, { catch(it) }, { recover(it) }, { it })

@EffectDSL
public inline fun <R, A> Raise<R>.catch(
  @BuilderInference action: Raise<R>.() -> A,
  @BuilderInference catch: Raise<R>.(Throwable) -> A,
): A = fold({ action(this) }, { catch(it) }, { raise(it) }, { it })

@EffectDSL
@JvmName("catchReified")
public inline fun <reified T : Throwable, R, A> Raise<R>.catch(
  @BuilderInference action: Raise<R>.() -> A,
  @BuilderInference catch: Raise<R>.(T) -> A,
): A = catch(action) { t: Throwable -> if (t is T) catch(t) else throw t }

@EffectDSL
public inline fun <R> Raise<R>.ensure(condition: Boolean, raise: () -> R): Unit =
  if (condition) Unit else raise(raise())

@OptIn(ExperimentalContracts::class)
@EffectDSL
public inline fun <R, B : Any> Raise<R>.ensureNotNull(value: B?, raise: () -> R): B {
  contract { returns() implies (value != null) }
  return value ?: raise(raise())
}

/**
 * Accumulate the errors obtained by executing the [block]
 * over every element of [list].
 */
@EffectDSL
public inline fun <R, A, B> Raise<NonEmptyList<R>>.mapOrAccumulate(
  list: Iterable<A>,
  crossinline block: Raise<R>.(A) -> B
): List<B> = list.mapOrAccumulate(Semigroup.nonEmptyList()) {
  recover({ block(it) }, { raise(it.nel()) })
}
