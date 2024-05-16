@file:OptIn(ExperimentalContracts::class)

package arrow.core

import arrow.core.Ior.Both
import arrow.core.Ior.Left
import arrow.core.Ior.Right
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmStatic

public typealias IorNel<A, B> = Ior<Nel<A>, B>

/**
 * Port of https://github.com/typelevel/cats/blob/v0.9.0/core/src/main/scala/cats/data/Ior.scala
 *
 * Represents a right-biased disjunction that is either an `A`, or a `B`, or both an `A` and a `B`.
 *
 * An instance of [Ior]<`A`,`B`> is one of:
 *  - [Ior.Left] <`A`>
 *  - [Ior.Right] <`B`>
 *  - [Ior.Both]<`A`,`B`>
 *
 * [Ior]<`A`,`B`> is similar to [Either]<`A`,`B`>, except that it can represent the simultaneous presence of
 * an `A` and a `B`. It is right-biased so methods such as `map` and `flatMap` operate on the
 * `B` value. Some methods, like `flatMap`, handle the presence of two [Ior.Both] values using a
 * `combine` function `(A, A) -> A`, while other methods, like [toEither], ignore the `A` value in a [Ior.Both Both].
 *
 * [Ior]<`A`,`B`> is isomorphic to [Either]<[Either]<`A`,`B`>, [Pair]<`A`,`B`>>, but provides methods biased toward `B`
 * values, regardless of whether the `B` values appear in a [Ior.Right] or a [Ior.Both].
 * The isomorphic Either form can be accessed via the [unwrap] method.
 */
public sealed class Ior<out A, out B> {

  /**
   * Returns `true` if this is a [Left], `false` otherwise.
   *
   * Example:
   *
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Left("tulip").isLeft()           // Result: true
   *   Ior.Right("venus fly-trap").isLeft() // Result: false
   *   Ior.Both("venus", "fly-trap").isLeft() // Result: false
   * }
   * ```
   * <!--- KNIT example-ior-01.kt -->
   */
  public fun isLeft(): Boolean {
    contract {
      returns(true) implies (this@Ior is Ior.Left<A>)
      returns(false) implies (this@Ior is Ior.Right<B> || this@Ior is Ior.Both<A, B>)
    }
    return this@Ior is Ior.Left<A>
  }

  /**
   * Returns `true` if this is a [Right], `false` otherwise.
   *
   * Example:
   *
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Left("tulip").isRight()           // Result: false
   *   Ior.Right("venus fly-trap").isRight() // Result: true
   *   Ior.Both("venus", "fly-trap").isRight() // Result: false
   * }
   * ```
   * <!--- KNIT example-ior-02.kt -->
   */
  public fun isRight(): Boolean {
    contract {
      returns(true) implies (this@Ior is Ior.Right<B>)
      returns(false) implies (this@Ior is Ior.Left<A> || this@Ior is Ior.Both<A, B>)
    }
    return this@Ior is Ior.Right<B>
  }

  /**
   * Returns `true` if this is a [Both], `false` otherwise.
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Left("tulip").isBoth()           // Result: false
   *   Ior.Right("venus fly-trap").isBoth() // Result: false
   *   Ior.Both("venus", "fly-trap").isBoth() // Result: true
   * }
   * ```
   * <!--- KNIT example-ior-03.kt -->
   */
  public fun isBoth(): Boolean {
    contract {
      returns(false) implies (this@Ior is Ior.Right<B> || this@Ior is Ior.Left<A>)
      returns(true) implies (this@Ior is Ior.Both<A, B>)
    }
    return this@Ior is Ior.Both<A, B>
  }

  public companion object {
    /**
     * Create an [Ior] from two nullables if at least one of them is defined.
     *
     * @param a an element (nullable) for the left side of the [Ior]
     * @param b an element (nullable) for the right side of the [Ior]
     *
     * @return `null` if both [a] and [b] are `null`. Otherwise
     * an [Ior.Left], [Ior.Right], or [Ior.Both] if [a], [b], or both are defined (respectively).
     */
    @JvmStatic
    public fun <A, B> fromNullables(a: A?, b: B?): Ior<A, B>? =
      when (a != null) {
        true -> when (b != null) {
          true -> Both(a, b)
          false -> Left(a)
        }

        false -> when (b != null) {
          true -> Right(b)
          false -> null
        }
      }

    @JvmStatic
    public fun <A, B> leftNel(a: A): IorNel<A, B> = Left(nonEmptyListOf(a))

    @JvmStatic
    public fun <A, B> bothNel(a: A, b: B): IorNel<A, B> = Both(nonEmptyListOf(a), b)
  }

  /**
   * Applies `fa` if this is a [Left], `fb` if this is a [Right] or `fab` if this is a [Both]
   *
   *
   * @param fa the function to apply if this is a [Left]
   * @param fb the function to apply if this is a [Right]
   * @param fab the function to apply if this is a [Both]
   * @return the results of applying the function
   */
  public inline fun <C> fold(fa: (A) -> C, fb: (B) -> C, fab: (A, B) -> C): C {
    contract {
      callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
      callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
      callsInPlace(fab, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
      is Left -> fa(value)
      is Right -> fb(value)
      is Both -> fab(leftValue, rightValue)
    }
  }

  /**
   * The given function is applied if this is a [Right] or [Both] to `B`.
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Right(12).map { "flower" } // Result: Right("flower")
   *   Ior.Left(12).map { "flower" }  // Result: Left(12)
   *   Ior.Both(12, "power").map { "flower $it" }  // Result: Both(12, "flower power")
   * }
   * ```
   * <!--- KNIT example-ior-04.kt -->
   */
  public inline fun <D> map(f: (B) -> D): Ior<A, D> {
    contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
      is Left -> this
      is Right -> Right(f(value))
      is Both -> Both(leftValue, f(rightValue))
    }
  }

  /**
   * The given function is applied if this is a [Left] or [Both] to `A`.
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Right(12).mapLeft { "flower" } // Result: Right(12)
   *   Ior.Left(12).mapLeft { "flower" }  // Result: Left("power")
   *   Ior.Both(12, "power").mapLeft { "flower $it" }  // Result: Both("flower 12", "power")
   * }
   * ```
   * <!--- KNIT example-ior-05.kt -->
   */
  public inline fun <C> mapLeft(fa: (A) -> C): Ior<C, B> {
    contract { callsInPlace(fa, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
      is Left -> Left(fa(value))
      is Right -> this
      is Both -> Both(fa(leftValue), rightValue)
    }
  }

  /**
   * If this is a [Left], then return the left value in [Right] or vice versa,
   * when this is [Both] , left and right values are swap
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Left("left").swap()   // Result: Right("left")
   *   Ior.Right("right").swap() // Result: Left("right")
   *   Ior.Both("left", "right").swap() // Result: Both("right", "left")
   * }
   * ```
   * <!--- KNIT example-ior-06.kt -->
   */
  public fun swap(): Ior<B, A> = fold(
    { Right(it) },
    { Left(it) },
    { a, b -> Both(b, a) }
  )

  /**
   * Return the isomorphic [Either] of this [Ior]
   */
  public fun unwrap(): Either<Either<A, B>, Pair<A, B>> = fold(
    { Either.Left(Either.Left(it)) },
    { Either.Left(Either.Right(it)) },
    { a, b -> Either.Right(Pair(a, b)) }
  )

  public fun toPair(): Pair<A?, B?> = fold(
    { Pair(it, null) },
    { Pair(null, it) },
    { a, b -> Pair(a, b) }
  )

  /**
   * Returns a [Either.Right] containing the [Right] value or `B` if this is [Right] or [Both]
   * and [Either.Left] if this is a [Left].
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Right(12).toEither() // Result: Either.Right(12)
   *   Ior.Left(12).toEither()  // Result: Either.Left(12)
   *   Ior.Both("power", 12).toEither()  // Result: Either.Right(12)
   * }
   * ```
   * <!--- KNIT example-ior-07.kt -->
   */
  public fun toEither(): Either<A, B> =
    fold({ Either.Left(it) }, { Either.Right(it) }, { _, b -> Either.Right(b) })

  public fun getOrNull(): B? {
    contract {
      returns(null) implies (this@Ior is Left<A>)
      returnsNotNull() implies ((this@Ior is Right<B>) || (this@Ior is Both<A, B>))
    }
    return fold({ null }, { it }, { _, b -> b })
  }

  /**
   * Returns the [Left] value or `A` if this is [Left] or [Both]
   * and `null` if this is a [Right].
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   val right = Ior.Right(12).leftOrNull()         // Result: null
   *   val left = Ior.Left(12).leftOrNull()           // Result: 12
   *   val both = Ior.Both(12, "power").leftOrNull()  // Result: 12
   *   println("right = $right")
   *   println("left = $left")
   *   println("both = $both")
   * }
   * ```
   * <!--- KNIT example-ior-08.kt -->
   */
  public fun leftOrNull(): A? {
    contract {
      returns(null) implies (this@Ior is Right<B>)
      returnsNotNull() implies ((this@Ior is Left<A>) || (this@Ior is Both<A, B>))
    }
    return fold({ it }, { null }, { a, _ -> a })
  }

  public data class Left<out A>(val value: A) : Ior<A, Nothing>() {
    override fun toString(): String = "Ior.Left($value)"

    public companion object
  }

  public data class Right<out B>(val value: B) : Ior<Nothing, B>() {
    override fun toString(): String = "Ior.Right($value)"

    public companion object
  }

  public data class Both<out A, out B>(val leftValue: A, val rightValue: B) : Ior<A, B>() {
    override fun toString(): String = "Ior.Both($leftValue, $rightValue)"
  }

  override fun toString(): String = fold(
    { "Ior.Left($it" },
    { "Ior.Right($it)" },
    { a, b -> "Ior.Both($a, $b)" }
  )

  /**
   * Returns `false` if [Right] or [Both], or returns the result of the application of
   * the given predicate to the [Left] value.
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   val right: Ior<Int, Int> = Ior.Right(12)
   *   right.isLeft { it > 10 }   // Result: false
   *   Ior.Both(12, 7).isLeft { it > 10 }    // Result: false
   *   Ior.Left(12).isLeft { it > 10 }      // Result: true
   * }
   * ```
   * <!--- KNIT example-ior-09.kt -->
   */
  public inline fun isLeft(predicate: (A) -> Boolean): Boolean {
    contract {
      returns(true) implies (this@Ior is Left<A>)
      returns(false) implies (this@Ior is Right<B> || this@Ior is Both<A, B>)
    }
    return this@Ior is Left<A> && predicate(value)
  }

  /**
   * Returns `false` if [Left] or [Both], or returns the result of the application of
   * the given predicate to the [Right] value.
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Right(12).isRight { it > 10 }   // Result: false
   *   Ior.Both(12, 7).isRight { it > 10 }    // Result: false
   *   val left: Ior<Int, Int> = Ior.Left(12)
   *   left.isRight { it > 10 }      // Result: true
   * }
   * ```
   * <!--- KNIT example-ior-10.kt -->
   */
  public inline fun isRight(predicate: (B) -> Boolean): Boolean {
    contract {
      returns(true) implies (this@Ior is Right<B>)
      returns(false) implies (this@Ior is Left<A> || this@Ior is Both<A, B>)
    }
    return this@Ior is Right<B> && predicate(value)
  }

  /**
   * Returns `false` if [Right] or [Left], or returns the result of the application of
   * the given predicate to the [Both] value.
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *     val right: Ior<Int, Int> = Ior.Right(12)
   *     right.isBoth( {it > 10}, {it > 6 })   // Result: false
   *     Ior.Both(12, 7).isBoth( {it > 10}, {it > 6 })// Result: true
   *     val left: Ior<Int, Int> = Ior.Left(12)
   *     left.isBoth ( {it > 10}, {it > 6 })      // Result: false
   * }
   * ```
   * <!--- KNIT example-ior-11.kt -->
   */
  public inline fun isBoth(leftPredicate: (A) -> Boolean, rightPredicate: (B) -> Boolean): Boolean {
    contract {
      returns(true) implies (this@Ior is Both<A, B>)
      returns(false) implies (this@Ior is Left<A> || this@Ior is Right<B>)
    }
    return this@Ior is Both<A, B> && leftPredicate(leftValue) && rightPredicate(rightValue)
  }
}

/**
 * Binds the given function across [Ior.Right].
 *
 * @param f The function to bind across [Ior.Right].
 */
public inline fun <A, B, D> Ior<A, B>.flatMap(combine: (A, A) -> A, f: (B) -> Ior<A, D>): Ior<A, D> =
  when (this) {
    is Left -> this
    is Right -> f(value)
    is Both -> when (val r = f(rightValue)) {
      is Left -> Left(combine(this.leftValue, r.value))
      is Right -> Both(this.leftValue, r.value)
      is Both -> Both(combine(this.leftValue, r.leftValue), r.rightValue)
    }
  }

public inline fun <A, B> Ior<A, B>.getOrElse(default: (A) -> B): B {
  contract { callsInPlace(default, InvocationKind.AT_MOST_ONCE) }
  return when (this) {
    is Left -> default(this.value)
    is Right -> this.value
    is Both -> this.rightValue
  }
}


public fun <A, B> Pair<A, B>.bothIor(): Ior<A, B> = Ior.Both(this.first, this.second)

public fun <A> A.leftIor(): Ior<A, Nothing> = Ior.Left(this)

public fun <A> A.rightIor(): Ior<Nothing, A> = Ior.Right(this)

public fun <A, B> Ior<A, B>.combine(other: Ior<A, B>, combineA: (A, A) -> A, combineB: (B, B) -> B): Ior<A, B> =
  when (this) {
    is Ior.Left -> when (other) {
      is Ior.Left -> Ior.Left(combineA(value, other.value))
      is Ior.Right -> Ior.Both(value, other.value)
      is Ior.Both -> Ior.Both(combineA(value, other.leftValue), other.rightValue)
    }

    is Ior.Right -> when (other) {
      is Ior.Left -> Ior.Both(other.value, value)
      is Ior.Right -> Ior.Right(combineB(value, other.value))
      is Ior.Both -> Ior.Both(other.leftValue, combineB(value, other.rightValue))
    }

    is Ior.Both -> when (other) {
      is Ior.Left -> Ior.Both(combineA(leftValue, other.value), rightValue)
      is Ior.Right -> Ior.Both(leftValue, combineB(rightValue, other.value))
      is Ior.Both -> Ior.Both(combineA(leftValue, other.leftValue), combineB(rightValue, other.rightValue))
    }
  }

public inline fun <A, B> Ior<A, Ior<A, B>>.flatten(combine: (A, A) -> A): Ior<A, B> =
  flatMap(combine, ::identity)

/**
 * Given an [Ior] with an error type [A], returns an [IorNel] with the same
 * error type. Wraps the original error in a [NonEmptyList] so that it can be
 * combined with an [IorNel] in a Raise DSL which operates on one.
 */
public fun <A, B> Ior<A, B>.toIorNel(): IorNel<A, B> =
  mapLeft { it.nel() }

public operator fun <A : Comparable<A>, B : Comparable<B>> Ior<A, B>.compareTo(other: Ior<A, B>): Int = fold(
  { a1 -> other.fold({ a2 -> a1.compareTo(a2) }, { -1 }, { _, _ -> -1 }) },
  { b1 -> other.fold({ 1 }, { b2 -> b1.compareTo(b2) }, { _, _ -> -1 }) },
  { a1, b1 ->
    other.fold(
      { 1 },
      { 1 },
      { a2, b2 -> if (a1.compareTo(a2) == 0) b1.compareTo(b2) else a1.compareTo(a2) }
    )
  }
)
