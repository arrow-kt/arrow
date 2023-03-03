@file:OptIn(ExperimentalTypeInference::class)

package arrow.core

import arrow.core.raise.EagerEffect
import arrow.core.raise.Effect
import arrow.core.raise.Raise
import arrow.core.raise.RaiseDSL
import arrow.core.raise.fold
import arrow.core.raise.recover
import kotlin.experimental.ExperimentalTypeInference
import kotlin.js.JsName
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName

/**
 * This is a temporary solution for accumulating errors in the context of a [Raise],
 * belonging to [Iterable.mapOrAccumulate], [NonEmptyList.mapOrAccumulate], [Sequence.mapOrAccumulate] & [Map.mapOrAccumulate].
 *
 * All methods that are defined in [AccumulatingRaise] can be implemented using context receivers instead,
 * this can be [DeprecationLevel.HIDDEN] in a source -and binary-compatible way when context receivers are released:
 *
 * ```kotlin
 * context(Raise<NonEmptyList<Error>>)
 * fun <Error, A> Either<Error, A>.bind(): A = when (this) {
 *   is Invalid -> raise(nonEmptyListOf(value))
 *   is Valid -> value
 * }
 *
 * @OptIn(ExperimentalTypeInference::class)
 * public inline fun <Error, A, B> Iterable<A>.mapOrAccumulate(
 *   @BuilderInference transform: Raise<NonEmptyList<Error>>.(A) -> B,
 * ): Either<NonEmptyList<Error>, List<B>>
 * ```
 */
public open class AccumulatingRaise<E>(
  private val raise: Raise<NonEmptyList<E>>
): Raise<NonEmptyList<E>> by raise {

  @RaiseDSL
  @JsName("raise1")
  @JvmName("raise1")
  public fun raise(r: E): Nothing =
    raise.raise(nonEmptyListOf(r))

  /**
   * Invoke an [EagerEffect] inside `this` [Raise] context.
   * Any _logical failure_ is raised in `this` [Raise] context,
   * and thus short-circuits the computation.
   *
   * @see [recover] if you want to attempt to recover from any _logical failure_.
   */
  @JsName("invokeEager")
  @JvmName("invokeEager")
  public operator fun <A> EagerEffect<E, A>.invoke(): A =
    fold({ raise(it) }, ::identity)

  @RaiseDSL
  @JsName("bindEager")
  @JvmName("bindEager")
  public fun <A> EagerEffect<E, A>.bind(): A =
    fold({ raise(it) }, ::identity)

  /**
   * Invoke an [Effect] inside `this` [Raise] context.
   * Any _logical failure_ raised are raised in `this` [Raise] context,
   * and thus short-circuits the computation.
   *
   * @see [recover] if you want to attempt to recover from any _logical failure_.
   */
  @JsName("invokeEffect")
  @JvmName("invokeEffect")
  public suspend operator fun <A> Effect<E, A>.invoke(): A =
    fold({ raise(it) }, ::identity)

  @RaiseDSL
  @JsName("bindEffect")
  @JvmName("bindEffect")
  public suspend fun <A> Effect<E, A>.bind(): A =
    fold({ raise(it) }, ::identity)

  @RaiseDSL
  @JsName("bindEither")
  @JvmName("bindEither")
  public fun <A> Either<E, A>.bind(): A = when (this) {
    is Either.Left -> raise(value)
    is Either.Right -> value
  }

  /* Will be removed in subsequent PRs for Arrow 2.x.x */
  @RaiseDSL
  @JsName("bindValidated")
  @JvmName("bindValidated")
  public fun <A> Validated<E, A>.bind(): A = when (this) {
    is Validated.Invalid -> raise(value)
    is Validated.Valid -> value
  }

  @RaiseDSL
  @JvmName("recoverEffect")
  public suspend infix fun <A> Effect<E, A>.recover(
    @BuilderInference resolve: suspend Raise<NonEmptyList<E>>.(E) -> A
  ): A =
    fold<E, A, A>({ this@recover.invoke(this) }, { throw it }, { resolve(it) }) { it }

  /** @see [recover] */
  @RaiseDSL
  @JvmName("recoverEager")
  public infix fun <A> EagerEffect<E, A>.recover(@BuilderInference resolve: Raise<NonEmptyList<E>>.(E) -> A): A =
    recover({ invoke() }) { resolve(it) }

  /**
   * Execute the [Effect] resulting in [A],
   * and recover from any _logical error_ of type [E], and [Throwable], by providing a fallback value of type [A],
   * or raising a new error of type [R].
   *
   * @see [catch] if you don't need to recover from [Throwable].
   */
  @RaiseDSL
  @JvmName("recoverAndCatchEffect")
  public suspend fun <A> Effect<E, A>.recover(
    @BuilderInference recover: suspend Raise<NonEmptyList<E>>.(E) -> A,
    @BuilderInference catch: suspend Raise<NonEmptyList<E>>.(Throwable) -> A,
  ): A = fold({ invoke() }, { catch(it) }, { recover(it) }, { it })

  @RaiseDSL
  @JvmName("catchEffect")
  public suspend infix fun <A> Effect<E, A>.catch(
    @BuilderInference catch: suspend Raise<NonEmptyList<E>>.(Throwable) -> A,
  ): A = fold({ catch(it) }, { raise(it) }, { it })

  @RaiseDSL
  @JvmName("catchEagerEffect")
  public infix fun <A> EagerEffect<E, A>.catch(
    @BuilderInference catch: Raise<NonEmptyList<E>>.(Throwable) -> A,
  ): A = fold({ catch(it) }, { raise(it) }, { it })
}
