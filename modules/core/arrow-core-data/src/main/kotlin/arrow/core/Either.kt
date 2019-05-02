package arrow.core

import arrow.Kind
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.higherkind

const val RightTag: Int = 0
const val LeftTag: Int = 1

/**
 * Port of https://github.com/scala/scala/blob/v2.12.1/src/library/scala/util/Either.scala
 *
 * Represents a value of one of two possible types (a disjoint union.)
 * An instance of Either is either an instance of [Left] or [Right].
 */
@higherkind
sealed class Either<out A, out B>(@JvmField @PublishedApi internal val tag: Int) : EitherOf<A, B> {

  fun isLeft(): Boolean = tag == LeftTag

  fun isRight(): Boolean = tag == RightTag

  /**
   * Applies `ifLeft` if this is a [Left] or `ifRight` if this is a [Right].
   *
   * Example:
   * ```
   * val result: Either<Exception, Value> = possiblyFailingOperation()
   * result.fold(
   *      { log("operation failed with $it") },
   *      { log("operation succeeded with $it") }
   * )
   * ```
   *
   * @param ifLeft the function to apply if this is a [Left]
   * @param ifRight the function to apply if this is a [Right]
   * @return the results of applying the function
   */
  inline fun <C> fold(ifLeft: (A) -> C, ifRight: (B) -> C): C = when (tag) {
    RightTag -> {
      this as Right<B>
      ifRight(b)
    }
    else -> {
      this as Left<A>
      ifLeft(a)
    }
  }

  fun <C> foldLeft(initial: C, rightOperation: (C, B) -> C): C =
    fold({ initial }, { rightOperation(initial, (this as Right<B>).b) })

  fun <C> foldRight(initial: Eval<C>, rightOperation: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fold({ initial }, { rightOperation((this as Right<B>).b, initial) })

  /**
   * If this is a `Left`, then return the left value in `Right` or vice versa.
   *
   * Example:
   * ```
   * Left("left").swap()   // Result: Right("left")
   * Right("right").swap() // Result: Left("right")
   * ```
   */
  fun swap(): Either<B, A> = fold({ Right(it) }, { Left(it) })

  /**
   * The given function is applied if this is a `Right`.
   *
   * Example:
   * ```
   * Right(12).map { "flower" } // Result: Right("flower")
   * Left(12).map { "flower" }  // Result: Left(12)
   * ```
   */
  @Suppress("UNCHECKED_CAST")
  inline fun <C> map(f: (B) -> C): Either<A, C> =
    flatMap { Right(f(it)) }

  /**
   * The given function is applied if this is a `Left`.
   *
   * Example:
   * ```
   * Right(12).mapLeft { "flower" } // Result: Right(12)
   * Left(12).mapLeft { "flower" }  // Result: Left("flower)
   * ```
   */
  inline fun <C> mapLeft(f: (A) -> C): Either<C, B> =
    fold({ Left(f(it)) }, { Right(it) })

  /**
   * Map over Left and Right of this Either
   */
  fun <C, D> bimap(leftOperation: (A) -> C, rightOperation: (B) -> D): Either<C, D> =
    fold({ Left(leftOperation(it)) }, { Right(rightOperation(it)) })

  /**
   * Returns `false` if [Left] or returns the result of the application of
   * the given predicate to the [Right] value.
   *
   * Example:
   * ```
   * Right(12).exists { it > 10 } // Result: true
   * Right(7).exists { it > 10 }  // Result: false
   *
   * val left: Either<Int, Int> = Left(12)
   * left.exists { it > 10 }      // Result: false
   * ```
   */
  fun exists(predicate: (B) -> Boolean): Boolean =
    fold({ false }, { predicate(it) })

  /**
   * Returns a [Some] containing the [Right] value
   * if it exists or a [None] if this is a [Left].
   *
   * Example:
   * ```
   * Right(12).toOption() // Result: Some(12)
   * Left(12).toOption()  // Result: None
   * ```
   */
  fun toOption(): Option<B> =
    fold({ None }, { Some(it) })

  /**
   * The left side of the disjoint union, as opposed to the [Right] side.
   */
  @Suppress("DataClassPrivateConstructor")
  data class Left<out A> @PublishedApi internal constructor(val a: A) : Either<A, Nothing>(LeftTag) {
    companion object {
      operator fun <A> invoke(a: A): Either<A, Nothing> = Left(a)
    }
  }

  /**
   * The right side of the disjoint union, as opposed to the [Left] side.
   */
  @Suppress("DataClassPrivateConstructor")
  data class Right<out B> @PublishedApi internal constructor(val b: B) : Either<Nothing, B>(RightTag) {
    companion object {
      operator fun <B> invoke(b: B): Either<Nothing, B> = Right(b)
    }
  }

  companion object {

    fun <L> left(left: L): Either<L, Nothing> = Left(left)

    fun <R> right(right: R): Either<Nothing, R> = Right(right)

    tailrec fun <L, A, B> tailRecM(a: A, f: (A) -> Kind<EitherPartialOf<L>, Either<A, B>>): Either<L, B> {
      val ev: Either<L, Either<A, B>> = f(a).fix()
      return when (ev.tag) {
        LeftTag -> {
          ev as Left<L>
          Left(ev.a)
        }
        else -> {
          ev as Right<Either<A, B>>
          val b: Either<A, B> = ev.b
          when (b.tag) {
            LeftTag -> {
              b as Left<A>
              tailRecM(b.a, f)
            }
            else -> b as Right<B>
          }
        }
      }
    }

    fun <L, R> cond(test: Boolean, ifTrue: () -> R, ifFalse: () -> L): Either<L, R> = if (test) right(ifTrue()) else left(ifFalse())
  }
}

fun <L> Left(left: L): Either<L, Nothing> = Either.left(left)

fun <R> Right(right: R): Either<Nothing, R> = Either.right(right)

/**
 * Binds the given function across [Either.Right].
 *
 * @param f The function to bind across [Either.Right].
 */
@Suppress("UNCHECKED_CAST")
inline fun <A, B, C> EitherOf<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> =
  fix().let { eith -> eith.fold({ eith as Left<A> }, f) }

/**
 * Returns the value from this [Either.Right] or the given argument if this is a [Either.Left].
 *
 * Example:
 * ```
 * Right(12).getOrElse(17) // Result: 12
 * Left(12).getOrElse(17)  // Result: 17
 * ```
 */
fun <B> EitherOf<*, B>.getOrElse(default: () -> B): B =
  fix().fold({ default() }, ::identity)

/**
 * Returns the value from this [Either.Right] or null if this is a [Either.Left].
 *
 * Example:
 * ```
 * Right(12).orNull() // Result: 12
 * Left(12).orNull()  // Result: null
 * ```
 */
fun <B> EitherOf<*, B>.orNull(): B? =
  getOrElse { null }

/**
 * Returns the value from this [Either.Right] or allows clients to transform [Either.Left] to [Either.Right] while providing access to
 * the value of [Either.Left].
 *
 * Example:
 * ```
 * Right(12).getOrHandle { 17 } // Result: 12
 * Left(12).getOrHandle { it + 5 } // Result: 17
 * ```
 */
fun <A, B> EitherOf<A, B>.getOrHandle(default: (A) -> B): B =
  fix().fold({ default(it) }, ::identity)

/**
 * Returns [Either.Right] with the existing value of [Either.Right] if this is a [Either.Right] and the given predicate
 * holds for the right value.<br>
 *
 * Returns `Left(default)` if this is a [Either.Right] and the given predicate does not
 * hold for the right value.<br>
 *
 * Returns [Either.Left] with the existing value of [Either.Left] if this is a [Either.Left].<br>
 *
 * Example:
 * ```
 * Right(12).filterOrElse({ it > 10 }, { -1 }) // Result: Right(12)
 * Right(7).filterOrElse({ it > 10 }, { -1 })  // Result: Left(-1)
 *
 * val left: Either<Int, Int> = Left(12)
 * left.filterOrElse({ it > 10 }, { -1 })      // Result: Left(12)
 * ```
 */
inline fun <A, B> EitherOf<A, B>.filterOrElse(predicate: (B) -> Boolean, default: () -> A): Either<A, B> =
  flatMap { if (predicate(it)) Right(it) else Left(default()) }

/**
 * Returns [Either.Right] with the existing value of [Either.Right] if this is a [Either.Right] and the given
 * predicate holds for the right value.<br>
 *
 * Returns `Left(default({right}))` if this is a [Either.Right] and the given predicate does not
 * hold for the right value. Useful for error handling where 'default' returns a message with context on why the value
 * did not pass the filter<br>
 *
 * Returns [Either.Left] with the existing value of [Either.Left] if this is a [Either.Left].<br>
 *
 * Example:
 *
 * {: data-executable='true'}
 * ```kotlin:ank
 * import arrow.core.*
 *
 * Right(12).filterOrOther({ it > 10 }, { -1 })
 * ```
 *
 * {: data-executable='true'}
 * ```kotlin:ank
 * Right(7).filterOrOther({ it > 10 }, { "Value '$it' not greater than 10" })
 * ```
 *
 * {: data-executable='true'}
 * ```kotlin:ank
 * val left: Either<Int, Int> = Left(12)
 * left.filterOrOther({ it > 10 }, { -1 })
 * ```
 */
inline fun <A, B> EitherOf<A, B>.filterOrOther(predicate: (B) -> Boolean, default: (B) -> A): Either<A, B> =
  flatMap {
    if (predicate(it)) arrow.core.Either.Right(it)
    else arrow.core.Either.Left(default(it))
  }

/**
 * Returns [Either.Right] with the existing value of [Either.Right] if this is an [Either.Right] with a non-null value.
 * The returned Either.Right type is not nullable.
 *
 * Returns `Left(default())` if this is an [Either.Right] and the existing value is null
 *
 * Returns [Either.Left] with the existing value of [Either.Left] if this is an [Either.Left].
 *
 * Example:
 * ```
 * Right(12).leftIfNull({ -1 })   // Result: Right(12)
 * Right(null).leftIfNull({ -1 }) // Result: Left(-1)
 *
 * Left(12).leftIfNull({ -1 })    // Result: Left(12)
 * ```
 */
inline fun <A, B> EitherOf<A, B?>.leftIfNull(crossinline default: () -> A): Either<A, B> =
  fix().flatMap { it.rightIfNotNull { default() } }

/**
 * Returns `true` if this is a [Either.Right] and its value is equal to `elem` (as determined by `==`),
 * returns `false` otherwise.
 *
 * Example:
 * ```
 * Right("something").contains { "something" } // Result: true
 * Right("something").contains { "anything" }  // Result: false
 * Left("something").contains { "something" }  // Result: false
 *  ```
 *
 * @param elem the element to test.
 * @return `true` if the option has an element that is equal (as determined by `==`) to `elem`, `false` otherwise.
 */
fun <A, B> EitherOf<A, B>.contains(elem: B): Boolean =
  fix().fold({ false }, { it == elem })

fun <A, B, C> EitherOf<A, B>.ap(ff: EitherOf<A, (B) -> C>): Either<A, C> =
  ff.fix().flatMap { f -> fix().map(f) }.fix()

fun <A, B> EitherOf<A, B>.combineK(y: EitherOf<A, B>): Either<A, B> =
  fix().fold({ y.fix() }, { fix() })

fun <A> A.left(): Either<A, Nothing> = Either.Left(this)

fun <A> A.right(): Either<Nothing, A> = Either.Right(this)

/**
 * Returns [Either.Right] if the value of type B is not null, otherwise the specified A value wrapped into an
 * [Either.Left].
 *
 * Example:
 * ```
 * "value".rightIfNotNull { "left" } // Right(b="value")
 * null.rightIfNotNull { "left" }    // Left(a="left")
 * ```
 */
fun <A, B> B?.rightIfNotNull(default: () -> A): Either<A, B> = when (this) {
  null -> Either.Left(default())
  else -> Either.Right(this)
}

/**
 * Applies the given function `f` if this is a [Left], otherwise returns this if this is a [Right].
 * This is like `flatMap` for the exception.
 */
fun <A, B> EitherOf<A, B>.handleErrorWith(f: (A) -> EitherOf<A, B>): Either<A, B> =
  fix().fold({ f(it).fix() }, { this.fix() })
