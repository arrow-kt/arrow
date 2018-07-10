package arrow.core

import arrow.Kind
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.higherkind
import arrow.legacy.Disjunction
import arrow.legacy.LeftProjection
import arrow.legacy.RightProjection

/**
 * Port of https://github.com/scala/scala/blob/v2.12.1/src/library/scala/util/Either.scala
 *
 * Represents a value of one of two possible types (a disjoint union.)
 * An instance of Either is either an instance of [Left] or [Right].
 */
@higherkind
sealed class Either<out A, out B> : EitherOf<A, B> {

  /**
   * Returns `true` if this is a [Right], `false` otherwise.
   * Used only for performance instead of fold.
   */
  internal abstract val isRight: Boolean

  /**
   * Returns `true` if this is a [Left], `false` otherwise.
   * Used only for performance instead of fold.
   */
  internal abstract val isLeft: Boolean

  fun isLeft(): Boolean = isLeft

  fun isRight(): Boolean = isRight

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
  inline fun <C> fold(crossinline ifLeft: (A) -> C, crossinline ifRight: (B) -> C): C = when (this) {
    is Right -> ifRight(b)
    is Left -> ifLeft(a)
  }

  @Deprecated(DeprecatedUnsafeAccess, ReplaceWith("getOrElse { ifLeft }"))
  fun get(): B = when (this) {
    is Right -> b
    is Left -> throw NoSuchElementException("Disjunction.Left")
  }

  fun <C> foldLeft(initial: C, rightOperation: (C, B) -> C): C =
    this.fix().let { either ->
      when (either) {
        is Right -> rightOperation(initial, either.b)
        is Left -> initial
      }
    }

  fun <C> foldRight(initial: Eval<C>, rightOperation: (B, Eval<C>) -> Eval<C>): Eval<C> =
    this.fix().let { either ->
      when (either) {
        is Right -> rightOperation(either.b, initial)
        is Left -> initial
      }
    }

  @Deprecated("arrow.data.Either is right biased. This method will be removed in future releases")
  fun toDisjunction(): Disjunction<A, B> = when (this) {
    is Right -> Disjunction.Right(b)
    is Left -> Disjunction.Left(a)
  }

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
  inline fun <C> map(crossinline f: (B) -> C): Either<A, C> =
      when (this) {
        is Right -> Right(f(b))
        is Left -> this
      }

  /**
   * The given function is applied if this is a `Left`.
   *
   * Example:
   * ```
   * Right(12).mapLeft { "flower" } // Result: Right(12)
   * Left(12).mapLeft { "flower" }  // Result: Left("flower)
   * ```
   */
  inline fun <C> mapLeft(crossinline f: (A) -> C): Either<C, B> =
    fold({ Left(f(it)) }, { Right(it) })

  /**
   * Map over Left and Right of this Either
   */
  inline fun <C, D> bimap(crossinline leftOperation: (A) -> C, crossinline rightOperation: (B) -> D): Either<C, D> =
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
  inline fun exists(crossinline predicate: (B) -> Boolean): Boolean =
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

  @Deprecated("arrow.data.Either is right biased. This method will be removed in future releases")
  fun left(): LeftProjection<A, B> = LeftProjection(this)

  @Deprecated("arrow.data.Either is right biased. This method will be removed in future releases")
  fun right(): RightProjection<A, B> = RightProjection(this)

  /**
   * The left side of the disjoint union, as opposed to the [Right] side.
   */
  @Suppress("DataClassPrivateConstructor")
  data class Left<out A> @PublishedApi internal constructor(val a: A) : Either<A, Nothing>() {
    override val isLeft
      get() = true
    override val isRight
      get() = false

    companion object {
      inline operator fun <A> invoke(a: A): Either<A, Nothing> = Left(a)
    }
  }

  /**
   * The right side of the disjoint union, as opposed to the [Left] side.
   */
  @Suppress("DataClassPrivateConstructor")
  data class Right<out B> @PublishedApi internal constructor(val b: B) : Either<Nothing, B>() {
    override val isLeft
      get() = false
    override val isRight
      get() = true

    companion object {
      inline operator fun <B> invoke(b: B): Either<Nothing, B> = Right(b)
    }
  }

  companion object {

    fun <L> left(left: L): Either<L, Nothing> = Left(left)

    fun <R> right(right: R): Either<Nothing, R> = Right(right)

    tailrec fun <L, A, B> tailRecM(a: A, f: (A) -> Kind<EitherPartialOf<L>, Either<A, B>>): Either<L, B> {
      val ev: Either<L, Either<A, B>> = f(a).fix()
      return when (ev) {
        is Left -> Left(ev.a)
        is Right -> {
          val b: Either<A, B> = ev.b
          when (b) {
            is Left -> tailRecM(b.a, f)
            is Right -> Right(b.b)
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
fun <A, B, C> EitherOf<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> =
  fix().let {
    when (it) {
      is Right -> f(it.b)
      is Left -> it
    }
  }

/**
 * Returns the value from this [Either.Right] or the given argument if this is a [Either.Left].
 *
 * Example:
 * ```
 * Right(12).getOrElse(17) // Result: 12
 * Left(12).getOrElse(17)  // Result: 17
 * ```
 */
inline fun <B> EitherOf<*, B>.getOrElse(crossinline default: () -> B): B =
  fix().fold({ default() }, ::identity)

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
inline fun <A, B> EitherOf<A, B>.getOrHandle(crossinline default: (A) -> B): B =
  fix().fold({ default(it) }, ::identity)

/**
 * * Returns [Either.Right] with the existing value of [Either.Right] if this is a [Either.Right] and the given predicate
 * holds for the right value.
 * * Returns `Left(default)` if this is a [Either.Right] and the given predicate does not
 * hold for the right value.
 * * Returns [Either.Left] with the existing value of [Either.Left] if this is a [Either.Left].
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
inline fun <A, B> EitherOf<A, B>.filterOrElse(crossinline predicate: (B) -> Boolean, crossinline default: () -> A): Either<A, B> =
  fix().fold({ Left(it) }, { if (predicate(it)) Right(it) else Left(default()) })

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
  when (this) {
    is Either.Left -> y.fix()
    else -> this.fix()
  }

@Deprecated(DeprecatedAmbiguity, ReplaceWith("Try { body }.toEither()"))
inline fun <T> eitherTry(body: () -> T): Either<Throwable, T> = try {
  Right(body())
} catch (t: Throwable) {
  Left(t)
}

fun <A> A.left(): Either<A, Nothing> = Either.Left(this)

fun <A> A.right(): Either<Nothing, A> = Either.Right(this)
