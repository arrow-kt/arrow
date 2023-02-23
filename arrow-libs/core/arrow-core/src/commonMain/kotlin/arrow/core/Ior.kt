@file:OptIn(ExperimentalContracts::class)
package arrow.core

import arrow.core.Ior.Both
import arrow.core.Ior.Left
import arrow.core.Ior.Right
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidDeprecation
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupDeprecation
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
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
 * combination function, while other methods, like [toEither], ignore the `A` value in a [Ior.Both Both].
 *
 * [Ior]<`A`,`B`> is isomorphic to [Either]<[Either]<`A`,`B`>, [Pair]<`A`,`B`>>, but provides methods biased toward `B`
 * values, regardless of whether the `B` values appear in a [Ior.Right] or a [Ior.Both].
 * The isomorphic Either form can be accessed via the [unwrap] method.
 */
public sealed class Ior<out A, out B> {

  /**
   * Returns `true` if this is a [Right], `false` otherwise.
   *
   * Example:
   *
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Left("tulip").isRight           // Result: false
   *   Ior.Right("venus fly-trap").isRight // Result: true
   *   Ior.Both("venus", "fly-trap").isRight // Result: false
   * }
   * ```
   * <!--- KNIT example-ior-01.kt -->
   */
  public abstract val isRight: Boolean

  /**
   * Returns `true` if this is a [Left], `false` otherwise.
   *
   * Example:
   *
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Left("tulip").isLeft           // Result: true
   *   Ior.Right("venus fly-trap").isLeft // Result: false
   *   Ior.Both("venus", "fly-trap").isLeft // Result: false
   * }
   * ```
 * <!--- KNIT example-ior-02.kt -->
   */
  public abstract val isLeft: Boolean

  /**
   * Returns `true` if this is a [Both], `false` otherwise.
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Left("tulip").isBoth           // Result: false
   *   Ior.Right("venus fly-trap").isBoth // Result: false
   *   Ior.Both("venus", "fly-trap").isBoth // Result: true
   * }
   * ```
 * <!--- KNIT example-ior-03.kt -->
   */
  public abstract val isBoth: Boolean

  public companion object {
    /**
     * Create an [Ior] from two nullables if at least one of them is defined.
     *
     * @param a an element (nullable) for the left side of the [Ior]
     * @param b an element (nullable) for the right side of the [Ior]
     *
     * @return [null] if both [a] and [b] are [null]. Otherwise
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

    /**
     *  Lifts a function `(B) -> C` to the [Ior] structure returning a polymorphic function
     *  that can be applied over all [Ior] values in the shape of Ior<A, B>
     *
     *  ```kotlin
     *  import arrow.core.*
     *
     *  fun main(args: Array<String>) {
     *   //sampleStart
     *   val f = Ior.lift<Int, CharSequence, String> { s: CharSequence -> "$s World" }
     *   val ior: Ior<Int, CharSequence> = Ior.Right("Hello")
     *   val result = f(ior)
     *   //sampleEnd
     *   println(result)
     *  }
     *  ```
     */
    @JvmStatic
    public fun <A, B, C> lift(f: (B) -> C): (Ior<A, B>) -> Ior<A, C> =
      { it.map(f) }

    @JvmStatic
    public fun <A, B, C, D> lift(fa: (A) -> C, fb: (B) -> D): (Ior<A, B>) -> Ior<C, D> =
      { it.bimap(fa, fb) }
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

  public inline fun <C> foldLeft(c: C, f: (C, B) -> C): C {
    contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
    return fold({ c }, { f(c, it) }, { _, b -> f(c, b) })
  }

  @Deprecated(MonoidDeprecation, ReplaceWith("MN.run { foldLeft(MN.empty()) { b, a -> b + f(a) } }"))
  public inline fun <C> foldMap(MN: Monoid<C>, f: (B) -> C): C {
    contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
    return MN.run {
      foldLeft(MN.empty()) { b, a -> b + f(a) }
    }
  }

  public inline fun <C> bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C {
    contract {
      callsInPlace(f, InvocationKind.AT_MOST_ONCE)
      callsInPlace(g, InvocationKind.AT_MOST_ONCE)
    }
    return fold({ f(c, it) }, { g(c, it) }, { a, b -> g(f(c, a), b) })
  }

  @Deprecated(MonoidDeprecation, ReplaceWith("MN.run { bifoldLeft(MN.empty(), { c, a -> c + f(a) }, { c, b -> c + g(b) }) }"))
  public inline fun <C> bifoldMap(MN: Monoid<C>, f: (A) -> C, g: (B) -> C): C {
    contract {
      callsInPlace(f, InvocationKind.AT_MOST_ONCE)
      callsInPlace(g, InvocationKind.AT_MOST_ONCE)
    }
    return MN.run {
      bifoldLeft(MN.empty(), { c, a -> c + f(a) }, { c, b -> c + g(b) })
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
      is Left -> Left(value)
      is Right -> Right(f(value))
      is Both -> Both(leftValue, f(rightValue))
    }
  }

  /**
   * Apply `fa` if this is a [Left] or [Both] to `A`
   * and apply `fb` if this is [Right] or [Both] to `B`
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Right(12).bimap ({ "flower" }, { 12 }) // Result: Right(12)
   *   Ior.Left(12).bimap({ "flower" }, { 12 })  // Result: Left("flower")
   *   Ior.Both(12, "power").bimap ({ it * 2 }, { b -> "flower $b" })   // Result: Both("flower power", 24)
   * }
   * ```
 * <!--- KNIT example-ior-05.kt -->
   */
  public inline fun <C, D> bimap(fa: (A) -> C, fb: (B) -> D): Ior<C, D> {
    contract {
      callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
      callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    }
    return fold(
      { Left(fa(it)) },
      { Right(fb(it)) },
      { a, b -> Both(fa(a), fb(b)) }
    )
  }

  /**
   * The given function is applied if this is a [Left] or [Both] to `A`.
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Right(12).map { "flower" } // Result: Right(12)
   *   Ior.Left(12).map { "flower" }  // Result: Left("power")
   *   Ior.Both(12, "power").map { "flower $it" }  // Result: Both("flower 12", "power")
   * }
   * ```
   * <!--- KNIT example-ior-06.kt -->
   */
  public inline fun <C> mapLeft(fa: (A) -> C): Ior<C, B> {
    contract { callsInPlace(fa, InvocationKind.AT_MOST_ONCE) }
    return fold(
      { Left(fa(it)) },
      ::Right,
      { a, b -> Both(fa(a), b) }
    )
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
 * <!--- KNIT example-ior-07.kt -->
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

  /**
   * Return this [Ior] as [Pair] of nullables]
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   val right = Ior.Right(12).padNull()         // Result: Pair(null, 12)
   *   val left = Ior.Left(12).padNull()           // Result: Pair(12, null)
   *   val both = Ior.Both("power", 12).padNull()  // Result: Pair("power", 12)
   *
   *   println("right = $right")
   *   println("left = $left")
   *   println("both = $both")
   * }
   * ```
 * <!--- KNIT example-ior-08.kt -->
   */
  public fun padNull(): Pair<A?, B?> = fold(
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
 * <!--- KNIT example-ior-09.kt -->
   */
  public fun toEither(): Either<A, B> =
    fold({ Either.Left(it) }, { Either.Right(it) }, { _, b -> Either.Right(b) })

  /**
   * Returns the [Right] value or `B` if this is [Right] or [Both]
   * and [null] if this is a [Left].
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   val right = Ior.Right(12).orNull()         // Result: 12
   *   val left = Ior.Left(12).orNull()           // Result: null
   *   val both = Ior.Both(12, "power").orNull()  // Result: "power"
   *
   *   println("right = $right")
   *   println("left = $left")
   *   println("both = $both")
   * }
   * ```
 * <!--- KNIT example-ior-10.kt -->
   */
  public fun orNull(): B? {
    contract {
      returns(null) implies (this@Ior is Left<A>)
      returnsNotNull() implies ((this@Ior is Right<B>) || (this@Ior is Both<A, B>))
    }
    return fold({ null }, { it }, { _, b -> b })
  }

  /**
   * Returns the [Left] value or `A` if this is [Left] or [Both]
   * and [null] if this is a [Right].
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
 * <!--- KNIT example-ior-11.kt -->
   */
  public fun leftOrNull(): A? {
    contract {
      returns(null) implies (this@Ior is Right<B>)
      returnsNotNull() implies ((this@Ior is Left<A>) || (this@Ior is Both<A, B>))
    }
    return fold({ it }, { null }, { a, _ -> a })
  }

  /**
   * Returns a [Validated.Valid] containing the [Right] value or `B` if this is [Right] or [Both]
   * and [Validated.Invalid] if this is a [Left].
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Right(12).toValidated() // Result: Valid(12)
   *   Ior.Left(12).toValidated()  // Result: Invalid(12)
   *   Ior.Both(12, "power").toValidated()  // Result: Valid("power")
   * }
   * ```
 * <!--- KNIT example-ior-12.kt -->
   */
  public fun toValidated(): Validated<A, B> =
    fold({ Invalid(it) }, { Valid(it) }, { _, b -> Valid(b) })

  public data class Left<out A>(val value: A) : Ior<A, Nothing>() {
    override val isRight: Boolean get() = false
    override val isLeft: Boolean get() = true
    override val isBoth: Boolean get() = false

    override fun toString(): String = "Ior.Left($value)"

    public companion object
  }

  public data class Right<out B>(val value: B) : Ior<Nothing, B>() {
    override val isRight: Boolean get() = true
    override val isLeft: Boolean get() = false
    override val isBoth: Boolean get() = false

    override fun toString(): String = "Ior.Right($value)"

    public companion object {
      @PublishedApi
      internal val unit: Ior<Nothing, Unit> =
        Right(Unit)
    }
  }

  public data class Both<out A, out B>(val leftValue: A, val rightValue: B) : Ior<A, B>() {
    override val isRight: Boolean get() = false
    override val isLeft: Boolean get() = false
    override val isBoth: Boolean get() = true

    override fun toString(): String = "Ior.Both($leftValue, $rightValue)"
  }

  override fun toString(): String = fold(
    { "Ior.Left($it" },
    { "Ior.Right($it)" },
    { a, b -> "Ior.Both($a, $b)" }
  )

  public inline fun <C, D> bicrosswalk(
    fa: (A) -> Iterable<C>,
    fb: (B) -> Iterable<D>
  ): List<Ior<C, D>> =
    fold(
      { a -> fa(a).map { it.leftIor() } },
      { b -> fb(b).map { it.rightIor() } },
      { a, b -> fa(a).align(fb(b)) }
    )

  public inline fun <C, D, K> bicrosswalkMap(
    fa: (A) -> Map<K, C>,
    fb: (B) -> Map<K, D>
  ): Map<K, Ior<C, D>> =
    fold(
      { a -> fa(a).mapValues { it.value.leftIor() } },
      { b -> fb(b).mapValues { it.value.rightIor() } },
      { a, b -> fa(a).align(fb(b)) }
    )

  public inline fun <C, D> bicrosswalkNull(
    fa: (A) -> C?,
    fb: (B) -> D?
  ): Ior<C, D>? =
    fold(
      { a -> fa(a)?.let { Left(it) } },
      { b -> fb(b)?.let { Right(it) } },
      { a, b -> fromNullables(fa(a), fb(b)) }
    )

  public inline fun <AA, C> bitraverse(fa: (A) -> Iterable<AA>, fb: (B) -> Iterable<C>): List<Ior<AA, C>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> fa(a).zip(fb(b)) { aa, c -> Both(aa, c) } }
    )

  public inline fun <AA, C, D> bitraverseEither(
    fa: (A) -> Either<AA, C>,
    fb: (B) -> Either<AA, D>
  ): Either<AA, Ior<C, D>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> fa(a).zip(fb(b)) { aa, c -> Both(aa, c) } }
    )

  public inline fun <C, D> bitraverseOption(
    fa: (A) -> Option<C>,
    fb: (B) -> Option<D>
  ): Option<Ior<C, D>> =
    fold(
      { fa(it).map { Left(it) } },
      { fb(it).map { Right(it) } },
      { a, b -> fa(a).zip(fb(b)) { aa, c -> Both(aa, c) } }
    )

  public inline fun <C, D> bitraverseNullable(
    fa: (A) -> C?,
    fb: (B) -> D?
  ): Ior<C, D>? =
    fold(
      { a -> fa(a)?.let { Left(it) } },
      { b -> fb(b)?.let { Right(it) } },
      { a, b -> Nullable.zip(fa(a), fb(b)) { aa, c -> Both(aa, c) } }
    )

  public inline fun <AA, C, D> bitraverseValidated(
    combine: (AA, AA) -> AA,
    fa: (A) -> Validated<AA, C>,
    fb: (B) -> Validated<AA, D>
  ): Validated<AA, Ior<C, D>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> fa(a).zip(combine, fb(b)) { aa, c -> Both(aa, c) } }
    )

  @Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { bitraverseValidated({ x, y -> x + y }, fa, fb) }"))
  public inline fun <AA, C, D> bitraverseValidated(
    SA: Semigroup<AA>,
    fa: (A) -> Validated<AA, C>,
    fb: (B) -> Validated<AA, D>
  ): Validated<AA, Ior<C, D>> =
    SA.run { bitraverseValidated({ x, y -> x + y }, fa, fb) }

  public inline fun <C> crosswalk(fa: (B) -> Iterable<C>): List<Ior<A, C>> =
    fold(
      { emptyList() },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) } }
    )

  public inline fun <K, V> crosswalkMap(fa: (B) -> Map<K, V>): Map<K, Ior<A, V>> =
    fold(
      { emptyMap() },
      { b -> fa(b).mapValues { Right(it.value) } },
      { a, b -> fa(b).mapValues { Both(a, it.value) } }
    )

  public inline fun <A, B, C> crosswalkNull(ior: Ior<A, B>, fa: (B) -> C?): Ior<A, C>? =
    ior.fold(
      { a -> Left(a) },
      { b -> fa(b)?.let { Right(it) } },
      { a, b -> fa(b)?.let { Both(a, it) } }
    )

  public inline fun all(predicate: (B) -> Boolean): Boolean {
    contract { callsInPlace(predicate, InvocationKind.AT_MOST_ONCE) }
    return fold({ true }, predicate, { _, b -> predicate(b) })
  }

  /**
   * Returns `false` if [Left] or returns the result of the application of
   * the given predicate to the [Right] value.
   *
   * Example:
   * ```kotlin
   * import arrow.core.Ior
   *
   * fun main() {
   *   Ior.Both(5, 12).exists { it > 10 } // Result: true
   *   Ior.Right(12).exists { it > 10 }   // Result: true
   *   Ior.Right(7).exists { it > 10 }    // Result: false
   *   val left: Ior<Int, Int> = Ior.Left(12)
   *   left.exists { it > 10 }      // Result: false
   * }
   * ```
 * <!--- KNIT example-ior-13.kt -->
   */
  public inline fun exists(predicate: (B) -> Boolean): Boolean {
    contract { callsInPlace(predicate, InvocationKind.AT_MOST_ONCE) }
    return fold({ false }, predicate, { _, b -> predicate(b) })
  }

  public inline fun findOrNull(predicate: (B) -> Boolean): B? {
    contract { callsInPlace(predicate, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
      is Left -> null
      is Right -> if (predicate(this.value)) this.value else null
      is Both -> if (predicate(this.rightValue)) this.rightValue else null
    }
  }

  public fun isEmpty(): Boolean {
    contract {
      returns(true) implies (this@Ior is Left<A>)
      returns(false) implies (this@Ior is Right<B> || this@Ior is Both<A, B>)
    }
    return isLeft
  }

  public fun isNotEmpty(): Boolean {
    contract {
      returns(false) implies (this@Ior is Left<A>)
      returns(true) implies (this@Ior is Right<B> || this@Ior is Both<A, B>)
    }
    return !isLeft
  }

  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <C> traverse(fa: (B) -> Iterable<C>): List<Ior<A, C>> {
    contract { callsInPlace(fa, InvocationKind.AT_MOST_ONCE) }
    return fold(
      { a -> listOf(Left(a)) },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) } }
    )
  }

  @Deprecated("traverseEither is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
  public inline fun <AA, C> traverseEither(fa: (B) -> Either<AA, C>): Either<AA, Ior<A, C>> =
    traverse(fa)

  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <AA, C> traverse(fa: (B) -> Either<AA, C>): Either<AA, Ior<A, C>> {
    contract { callsInPlace(fa, InvocationKind.AT_MOST_ONCE) }
    return fold(
      { a -> Either.Right(Left(a)) },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) } }
    )
  }

  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <C> traverse(fa: (B) -> Option<C>): Option<Ior<A, C>> {
    contract { callsInPlace(fa, InvocationKind.AT_MOST_ONCE) }
    return fold(
      { a -> Some(Left(a)) },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) } }
    )
  }

  @Deprecated("traverseOption is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
  public inline fun <C> traverseOption(fa: (B) -> Option<C>): Option<Ior<A, C>> =
    traverse(fa)

  public inline fun <C> traverseNullable(fa: (B) -> C?): Ior<A, C>? {
    contract { callsInPlace(fa, InvocationKind.AT_MOST_ONCE) }
    return fold(
      { a -> Left(a) },
      { b -> fa(b)?.let { Right(it) } },
      { a, b -> fa(b)?.let { Both(a, it) } }
    )
  }

  @OptIn(ExperimentalTypeInference::class)
  @OverloadResolutionByLambdaReturnType
  public inline fun <AA, C> traverse(fa: (B) -> Validated<AA, C>): Validated<AA, Ior<A, C>> {
    contract { callsInPlace(fa, InvocationKind.AT_MOST_ONCE) }
    return fold(
      { a -> Valid(Left(a)) },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) } }
    )
  }

  @Deprecated("traverseValidated is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
  public inline fun <AA, C> traverseValidated(fa: (B) -> Validated<AA, C>): Validated<AA, Ior<A, C>> =
    traverse(fa)

  public fun void(): Ior<A, Unit> =
    map { Unit }
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
    is Both ->
      f(this@flatMap.rightValue).fold(
        { a -> Left(combine(this@flatMap.leftValue, a)) },
        { d -> Both(this@flatMap.leftValue, d) },
        { ll, rr -> Both(combine(this@flatMap.leftValue, ll), rr) }
      )
  }

/**
 * Binds the given function across [Ior.Right].
 *
 * @param f The function to bind across [Ior.Right].
 */
@Deprecated(SemigroupDeprecation, ReplaceWith("SG.run { flatMap({ x, y -> x + y}, f) }"))
public inline fun <A, B, D> Ior<A, B>.flatMap(SG: Semigroup<A>, f: (B) -> Ior<A, D>): Ior<A, D> =
  SG.run { flatMap({ x, y -> x + y}, f) }

public inline fun <A, B> Ior<A, B>.getOrElse(default: () -> B): B {
  contract {callsInPlace(default, InvocationKind.AT_MOST_ONCE) }
  return fold({ default() }, ::identity, { _, b -> b })
}

public fun <A, B> Pair<A, B>.bothIor(): Ior<A, B> = Ior.Both(this.first, this.second)

public fun <A> A.leftIor(): Ior<A, Nothing> = Ior.Left(this)

public fun <A> A.rightIor(): Ior<Nothing, A> = Ior.Right(this)

public fun <A, B> Ior<Iterable<A>, Iterable<B>>.bisequence(): List<Ior<A, B>> =
  bitraverse(::identity, ::identity)

public fun <A, B, C> Ior<Either<A, B>, Either<A, C>>.bisequenceEither(): Either<A, Ior<B, C>> =
  bitraverseEither(::identity, ::identity)

public fun <B, C> Ior<Option<B>, Option<C>>.bisequenceOption(): Option<Ior<B, C>> =
  bitraverseOption(::identity, ::identity)

public fun <B, C> Ior<B?, C?>.bisequenceNullable(): Ior<B, C>? =
  bitraverseNullable(::identity, ::identity)

public fun <A, B, C> Ior<Validated<A, B>, Validated<A, C>>.bisequenceValidated(combine: (A, A) -> A): Validated<A, Ior<B, C>> =
  bitraverseValidated(combine, ::identity, ::identity)

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { bisequenceValidated { x, y -> x + y } }"))
public fun <A, B, C> Ior<Validated<A, B>, Validated<A, C>>.bisequenceValidated(SA: Semigroup<A>): Validated<A, Ior<B, C>> =
  SA.run { bisequenceValidated { x, y -> x + y } }

public fun <A, B> Ior<A, B>.combine(combineA: (A, A) -> A, combineB: (B, B) -> B, other: Ior<A, B>): Ior<A, B> =
  when (val a = this@combine) {
    is Ior.Left -> when (other) {
      is Ior.Left -> Ior.Left(combineA(a.value, other.value))
      is Ior.Right -> Ior.Both(a.value, other.value)
      is Ior.Both -> Ior.Both(combineA(a.value, other.leftValue), other.rightValue)
    }
    is Ior.Right -> when (other) {
      is Ior.Left -> Ior.Both(other.value, a.value)
      is Ior.Right -> Ior.Right(combineB(a.value, other.value))
      is Ior.Both -> Ior.Both(other.leftValue, combineB(a.value, other.rightValue))
    }
    is Ior.Both -> when (other) {
      is Ior.Left -> Ior.Both(combineA(a.leftValue, other.value), a.rightValue)
      is Ior.Right -> Ior.Both(a.leftValue, combineB(a.rightValue, other.value))
      is Ior.Both -> Ior.Both(combineA(a.leftValue, other.leftValue), combineB(a.rightValue, other.rightValue))
    }
  }

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { SB.run { combine({ x, y -> x + y }, { x, y -> x + y }, other) }}"))
public fun <A, B> Ior<A, B>.combine(SA: Semigroup<A>, SB: Semigroup<B>, other: Ior<A, B>): Ior<A, B> =
  SA.run { SB.run { combine({ x, y -> x + y }, { x, y -> x + y }, other) }}

public inline fun <A, B> Ior<A, Ior<A, B>>.flatten(combine: (A, A) -> A): Ior<A, B> =
  flatMap(combine, ::identity)

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { flatten { x, y -> x + y } }"))
public inline fun <A, B> Ior<A, Ior<A, B>>.flatten(SA: Semigroup<A>): Ior<A, B> =
  SA.run { flatten { x, y -> x + y } }

public fun <A, B> Ior<A, B>.replicate(combineA: (A, A) -> A, n: Int): Ior<A, List<B>> =
  this.map { listOf(it) }.replicate(combineA, n, emptyList()) { x, y -> x + y }

public fun <A, B> Ior<A, B>.replicate(combineA: (A, A) -> A, n: Int, emptyB: B, combineB: (B, B) -> B): Ior<A, B> =
  if (n <= 0) Ior.Right(emptyB)
  else when (this) {
    is Ior.Right -> Ior.Right(List(n) { value }.fold(emptyB, combineB))
    is Ior.Left -> this
    is Ior.Both -> bimap(
      { List(n - 1) { leftValue }.fold(leftValue, combineA) },
      { List(n) { rightValue }.fold(emptyB, combineB) }
    )
  }

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { replicate({ x, y -> x + y }, n) }"))
public fun <A, B> Ior<A, B>.replicate(SA: Semigroup<A>, n: Int): Ior<A, List<B>> =
  SA.run { replicate({ x, y -> x + y }, n) }

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { MB.run { replicate({ x, y -> x + y }, n, empty(), { x, y -> x + y }) } }"))
public fun <A, B> Ior<A, B>.replicate(SA: Semigroup<A>, n: Int, MB: Monoid<B>): Ior<A, B> =
  SA.run { MB.run { replicate({ x, y -> x + y }, n, empty(), { x, y -> x + y }) } }

public fun <A, B> Ior<A, Iterable<B>>.sequence(): List<Ior<A, B>> =
  traverse(::identity)

@Deprecated("sequenceEither is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence()", "arrow.core.sequence"))
public fun <A, B, C> Ior<A, Either<B, C>>.sequenceEither(): Either<B, Ior<A, C>> =
  sequence()

public fun <A, B, C> Ior<A, Either<B, C>>.sequence(): Either<B, Ior<A, C>> =
  traverse(::identity)

@Deprecated("sequenceOption is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence()", "arrow.core.sequence"))
public fun <A, B> Ior<A, Option<B>>.sequenceOption(): Option<Ior<A, B>> =
  sequence()

public fun <A, B> Ior<A, Option<B>>.sequence(): Option<Ior<A, B>> =
  traverse(::identity)

@Deprecated("sequenceOption is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence()", "arrow.core.sequence"))
public fun <A, B> Ior<A, B?>.sequenceNullable(): Ior<A, B>? =
  sequence()

public fun <A, B> Ior<A, B?>.sequence(): Ior<A, B>? =
  traverseNullable(::identity)

@Deprecated("sequenceValidated is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence()", "arrow.core.sequence"))
public fun <A, B, C> Ior<A, Validated<B, C>>.sequenceValidated(): Validated<B, Ior<A, C>> =
  sequence()

public fun <A, B, C> Ior<A, Validated<B, C>>.sequence(): Validated<B, Ior<A, C>> =
  traverse(::identity)

/**
 * Given [B] is a sub type of [C], re-type this value from Ior<A, B> to Ior<A, B>
 *
 * ```kotlin
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val string: Ior<Int, String> = Ior.Right("Hello")
 *   val chars: Ior<Int, CharSequence> =
 *     string.widen<Int, CharSequence, String>()
 *   //sampleEnd
 *   println(chars)
 * }
 * ```
 * <!--- KNIT example-ior-14.kt -->
 */
public fun <A, C, B : C> Ior<A, B>.widen(): Ior<A, C> =
  this

public fun <AA, A : AA, B> Ior<A, B>.leftWiden(): Ior<AA, B> =
  this

public fun <A, B, C> Ior<A, B>.zip(combine: (A, A) -> A, fb: Ior<A, C>): Ior<A, Pair<B, C>> =
  zip(combine, fb, ::Pair)

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { zip({ x, y -> x + y }, fb) }"))
public fun <A, B, C> Ior<A, B>.zip(SA: Semigroup<A>, fb: Ior<A, C>): Ior<A, Pair<B, C>> =
  SA.run { zip({ x, y -> x + y }, fb) }

public inline fun <A, B, C, D> Ior<A, B>.zip(
  combine: (A, A) -> A,
  c: Ior<A, C>,
  map: (B, C) -> D
): Ior<A, D> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(combine, c, Right.unit, Right.unit, Right.unit, Right.unit, Right.unit, Right.unit, Right.unit, Right.unit) { b, c, _, _, _, _, _, _, _, _ -> map(b, c) }
}

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { zip({ x, y -> x + y }, c, map) }"))
public inline fun <A, B, C, D> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  map: (B, C) -> D
): Ior<A, D> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return SA.run { zip({ x, y -> x + y }, c, map) }
}

public inline fun <A, B, C, D, E> Ior<A, B>.zip(
  combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  map: (B, C, D) -> E
): Ior<A, E> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(combine, c, d, Right.unit, Right.unit, Right.unit, Right.unit, Right.unit, Right.unit, Right.unit) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }
}

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { zip({ x, y -> x + y }, c, d, map) }"))
public inline fun <A, B, C, D, E> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  map: (B, C, D) -> E
): Ior<A, E> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return SA.run { zip({ x, y -> x + y }, c, d, map) }
}

public inline fun <A, B, C, D, E, F> Ior<A, B>.zip(
  combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  map: (B, C, D, E) -> F
): Ior<A, F> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(combine, c, d, e, Right.unit, Right.unit, Right.unit, Right.unit, Right.unit, Right.unit) { b, c, d, e, _, _, _, _, _, _ -> map(b, c, d, e) }
}

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { zip({ x, y -> x + y }, c, d, e, map) }"))
public inline fun <A, B, C, D, E, F> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  map: (B, C, D, E) -> F
): Ior<A, F> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return SA.run { zip({ x, y -> x + y }, c, d, e, map) }
}

public inline fun <A, B, C, D, E, F, G> Ior<A, B>.zip(
  combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  map: (B, C, D, E, F) -> G
): Ior<A, G> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(combine, c, d, e, f, Right.unit, Right.unit, Right.unit, Right.unit, Right.unit) { b, c, d, e, f, _, _, _, _, _ -> map(b, c, d, e, f) }
}

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { zip({ x, y -> x + y }, c, d, e, f, map) }"))
public inline fun <A, B, C, D, E, F, G> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  map: (B, C, D, E, F) -> G
): Ior<A, G> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return SA.run { zip({ x, y -> x + y }, c, d, e, f, map) }
}

public inline fun <A, B, C, D, E, F, G, H> Ior<A, B>.zip(
  combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  map: (B, C, D, E, F, G) -> H
): Ior<A, H> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(combine, c, d, e, f, g, Right.unit, Right.unit, Right.unit, Right.unit) { b, c, d, e, f, g, _, _, _, _ -> map(b, c, d, e, f, g) }
}

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { zip({ x, y -> x + y }, d, c, e, f, g, map) }"))
public inline fun <A, B, C, D, E, F, G, H> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  map: (B, C, D, E, F, G) -> H
): Ior<A, H> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return SA.run { zip({ x, y -> x + y }, c, d, e, f, g, map) }
}

public inline fun <A, B, C, D, E, F, G, H, I> Ior<A, B>.zip(
  combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  map: (B, C, D, E, F, G, H) -> I
): Ior<A, I> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(combine, c, d, e, f, g, h, Right.unit, Right.unit, Right.unit) { b, c, d, e, f, g, h, _, _, _ -> map(b, c, d, e, f, g, h) }
}

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { zip({ x, y -> x + y }, c, d, e, f, g, h, map) }"))
public inline fun <A, B, C, D, E, F, G, H, I> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  map: (B, C, D, E, F, G, H) -> I
): Ior<A, I> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return SA.run { zip({ x, y -> x + y }, c, d, e, f, g, h, map) }
}

public inline fun <A, B, C, D, E, F, G, H, I, J> Ior<A, B>.zip(
  combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  i: Ior<A, I>,
  map: (B, C, D, E, F, G, H, I) -> J
): Ior<A, J> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(combine, c, d, e, f, g, h, i, Right.unit, Right.unit) { b, c, d, e, f, g, h, i, _, _ -> map(b, c, d, e, f, g, h, i) }
}

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { zip({ x, y -> x + y }, c, d, e, f, g, h, i, map) }"))
public inline fun <A, B, C, D, E, F, G, H, I, J> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  i: Ior<A, I>,
  map: (B, C, D, E, F, G, H, I) -> J
): Ior<A, J> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return SA.run { zip({ x, y -> x + y }, c, d, e, f, g, h, i, map) }
}

public inline fun <A, B, C, D, E, F, G, H, I, J, K> Ior<A, B>.zip(
  combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  i: Ior<A, I>,
  j: Ior<A, J>,
  map: (B, C, D, E, F, G, H, I, J) -> K
): Ior<A, K> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return zip(combine, c, d, e, f, g, h, i, j, Right.unit) { b, c, d, e, f, g, h, i, j, _ -> map(b, c, d, e, f, g, h, i, j) }
}

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { zip({ x, y -> x + y }, c, d, e, f, g, h, i, j, map) }"))
public inline fun <A, B, C, D, E, F, G, H, I, J, K> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  i: Ior<A, I>,
  j: Ior<A, J>,
  map: (B, C, D, E, F, G, H, I, J) -> K
): Ior<A, K> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return SA.run { zip({ x, y -> x + y }, c, d, e, f, g, h, i, j, map) }
}

public inline fun <A, B, C, D, E, F, G, H, I, J, K, L> Ior<A, B>.zip(
  combine: (A, A) -> A,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  i: Ior<A, I>,
  j: Ior<A, J>,
  k: Ior<A, K>,
  map: (B, C, D, E, F, G, H, I, J, K) -> L
): Ior<A, L> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  // If any of the values is Right or Both then we can calculate L otherwise it results in MY_NULL
  val rightValue: Any? = if (
    (this@zip.isRight || this@zip.isBoth) &&
    (c.isRight || c.isBoth) &&
    (d.isRight || d.isBoth) &&
    (e.isRight || e.isBoth) &&
    (f.isRight || f.isBoth) &&
    (g.isRight || g.isBoth) &&
    (h.isRight || h.isBoth) &&
    (i.isRight || i.isBoth) &&
    (j.isRight || j.isBoth) &&
    (k.isRight || k.isBoth)
  ) {
    map(
      this@zip.orNull() as B,
      c.orNull() as C,
      d.orNull() as D,
      e.orNull() as E,
      f.orNull() as F,
      g.orNull() as G,
      h.orNull() as H,
      i.orNull() as I,
      j.orNull() as J,
      k.orNull() as K
    )
  } else EmptyValue

  var accumulatedLeft: Any? = EmptyValue

  if (this@zip is Left) return Left(this@zip.value)
  accumulatedLeft = if (this@zip is Both) this@zip.leftValue else accumulatedLeft

  if (c is Left) return Left(emptyCombine(accumulatedLeft, c.value, combine))
  accumulatedLeft = if (c is Both) emptyCombine(accumulatedLeft, c.leftValue, combine) else accumulatedLeft

  if (d is Left) return Left(emptyCombine(accumulatedLeft, d.value, combine))
  accumulatedLeft = if (d is Both) emptyCombine(accumulatedLeft, d.leftValue, combine) else accumulatedLeft

  if (e is Left) return Left(emptyCombine(accumulatedLeft, e.value, combine))
  accumulatedLeft = if (e is Both) emptyCombine(accumulatedLeft, e.leftValue, combine) else accumulatedLeft

  if (f is Left) return Left(emptyCombine(accumulatedLeft, f.value, combine))
  accumulatedLeft = if (f is Both) emptyCombine(accumulatedLeft, f.leftValue, combine) else accumulatedLeft

  if (g is Left) return Left(emptyCombine(accumulatedLeft, g.value, combine))
  accumulatedLeft = if (g is Both) emptyCombine(accumulatedLeft, g.leftValue, combine) else accumulatedLeft

  if (h is Left) return Left(emptyCombine(accumulatedLeft, h.value, combine))
  accumulatedLeft = if (h is Both) emptyCombine(accumulatedLeft, h.leftValue, combine) else accumulatedLeft

  if (i is Left) return Left(emptyCombine(accumulatedLeft, i.value, combine))
  accumulatedLeft = if (i is Both) emptyCombine(accumulatedLeft, i.leftValue, combine) else accumulatedLeft

  if (j is Left) return Left(emptyCombine(accumulatedLeft, j.value, combine))
  accumulatedLeft = if (j is Both) emptyCombine(accumulatedLeft, j.leftValue, combine) else accumulatedLeft

  if (k is Left) return Left(emptyCombine(accumulatedLeft, k.value, combine))
  accumulatedLeft = if (k is Both) emptyCombine(accumulatedLeft, k.leftValue, combine) else accumulatedLeft

  return when {
    rightValue != EmptyValue && accumulatedLeft == EmptyValue -> Right(rightValue as L)
    rightValue != EmptyValue && accumulatedLeft != EmptyValue -> Both(accumulatedLeft as A, rightValue as L)
    rightValue == EmptyValue && accumulatedLeft != EmptyValue -> Left(accumulatedLeft as A)
    else -> throw ArrowCoreInternalException
  }
}

@Deprecated(SemigroupDeprecation, ReplaceWith("SA.run { zip({ x, y -> x + y }, c, d, e, f, g, h, i, j, k, map) }"))
public inline fun <A, B, C, D, E, F, G, H, I, J, K, L> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  i: Ior<A, I>,
  j: Ior<A, J>,
  k: Ior<A, K>,
  map: (B, C, D, E, F, G, H, I, J, K) -> L
): Ior<A, L> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return SA.run { zip({ x, y -> x + y }, c, d, e, f, g, h, i, j, k, map) }
}

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
