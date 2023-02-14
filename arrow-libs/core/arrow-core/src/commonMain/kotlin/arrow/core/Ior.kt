@file:OptIn(ExperimentalContracts::class)
package arrow.core

import arrow.core.Ior.Both
import arrow.core.Ior.Left
import arrow.core.Ior.Right
import arrow.core.raise.either
import arrow.core.raise.ior
import arrow.core.raise.nullable
import arrow.core.raise.option
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
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
 * [Semigroup]<`A`>, while other methods, like [toEither], ignore the `A` value in a [Ior.Both Both].
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
    @Deprecated(RedundantAPI + "Prefer explicitly creating lambdas",
      ReplaceWith("{ it.map(f) }")
    )
    public fun <A, B, C> lift(f: (B) -> C): (Ior<A, B>) -> Ior<A, C> =
      { it.map(f) }

    @JvmStatic
    @Deprecated(RedundantAPI + "Prefer explicitly creating lambdas",
      ReplaceWith("{ it.map(fb).mapLeft(fa) }")
    )
    public fun <A, B, C, D> lift(fa: (A) -> C, fb: (B) -> D): (Ior<A, B>) -> Ior<C, D> =
      { it.map(fb).mapLeft(fa) }
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

  @Deprecated(
    NicheAPI + "Prefer when or fold instead",
    ReplaceWith("fold({ c }, { f(c, it) }, { _, b -> f(c, b) })")
  )
  public inline fun <C> foldLeft(c: C, f: (C, B) -> C): C {
    contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
    return fold({ c }, { f(c, it) }, { _, b -> f(c, b) })
  }

  @Deprecated(
    NicheAPI + "Prefer when or fold instead",
    ReplaceWith("fold({ MN.empty() }, { f(it) }, { _, b -> f(b) })")
  )
  public inline fun <C> foldMap(MN: Monoid<C>, f: (B) -> C): C {
    contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
    return MN.run {
      fold({ MN.empty() }, { f(it) }, { _, b -> f(b) })
    }
  }

  @Deprecated(
    NicheAPI + "Prefer when or fold instead",
    ReplaceWith("fold({ f(c, it) }, { g(c, it) }, { a, b -> g(f(c, a), b) })")
  )
  public inline fun <C> bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C {
    contract {
      callsInPlace(f, InvocationKind.AT_MOST_ONCE)
      callsInPlace(g, InvocationKind.AT_MOST_ONCE)
    }
    return fold({ f(c, it) }, { g(c, it) }, { a, b -> g(f(c, a), b) })
  }

  @Deprecated(
    NicheAPI + "Prefer when or fold instead",
    ReplaceWith("  MN.run { fold({ f(it) },{ g(it) },{ a,b -> f(a).combine(g(b)) })}")
  )
  public inline fun <C> bifoldMap(MN: Monoid<C>, f: (A) -> C, g: (B) -> C): C {
    contract {
      callsInPlace(f, InvocationKind.AT_MOST_ONCE)
      callsInPlace(g, InvocationKind.AT_MOST_ONCE)
    }
    return MN.run {
      fold({ f(it) },{ g(it) },{ a,b -> f(a).combine(g(b)) })
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
  @Deprecated(
    NicheAPI + "Prefer using the Either DSL, or map + mapLeft",
    ReplaceWith("map(fb).mapLeft(fa)")
  )
  public inline fun <C, D> bimap(fa: (A) -> C, fb: (B) -> D): Ior<C, D> {
    contract {
      callsInPlace(fa, InvocationKind.AT_MOST_ONCE)
      callsInPlace(fb, InvocationKind.AT_MOST_ONCE)
    }
    return map(fb).mapLeft(fa)
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
  @Deprecated(
    "orNull is being renamed to getOrNull to be more consistent with the Kotlin Standard Library naming",
    ReplaceWith("getOrNull()")
  )
  public fun orNull(): B? {
    contract {
      returns(null) implies (this@Ior is Left<A>)
      returnsNotNull() implies ((this@Ior is Right<B>) || (this@Ior is Both<A, B>))
    }
    return fold({ null }, { it }, { _, b -> b })
  }

  public fun getOrNull(): B? {
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

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ a -> fa(a).map { it.leftIor() } }, { b -> fb(b).map { it.rightIor() } },{ a, b -> fa(a).align(fb(b)) })")
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

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ a -> fa(a).mapValues { it.value.leftIor() } },{ b -> fb(b).mapValues { it.value.rightIor() } },{ a, b -> fa(a).align(fb(b)) })")
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

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ a -> fa(a)?.let { Left(it) } },{ b -> fb(b)?.let { Right(it) } },{ a, b -> fromNullables(fa(a), fb(b)) })")
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

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ a -> fa(a).map { Left(it) } },{ b -> fb(b).map { Right(it) } },{ a, b -> fa(a).zip(fb(b)) { aa, c -> Both(aa, c) } })")
  )
  public inline fun <AA, C> bitraverse(fa: (A) -> Iterable<AA>, fb: (B) -> Iterable<C>): List<Ior<AA, C>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> fa(a).zip(fb(b)) { aa, c -> Both(aa, c) } }
    )


  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ a -> fa(a).map { Left(it) } },{ b -> fb(b).map { Right(it) } },{ a, b -> either { Both(fa(a).bind(), fb(b).bind())} })")
  )
  public inline fun <AA, C, D> bitraverseEither(
    fa: (A) -> Either<AA, C>,
    fb: (B) -> Either<AA, D>
  ): Either<AA, Ior<C, D>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> either { Both(fa(a).bind(), fb(b).bind())} }
    )

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ a -> fa(a).map { Left(it) } },{ b -> fb(b).map { Right(it) } },{ a, b -> option { Both(fa(a).bind(), fb(b).bind())} })")
  )
  public inline fun <C, D> bitraverseOption(
    fa: (A) -> Option<C>,
    fb: (B) -> Option<D>
  ): Option<Ior<C, D>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> option { Both(fa(a).bind(), fb(b).bind())} }
    )

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ a -> fa(a)?.let { Left(it) } },{ b -> fb(b)?.let { Right(it) } }, { a, b -> nullable { Both( fa(a).bind(), fb(b).bind()) } })")
  )
  public inline fun <C, D> bitraverseNullable(
    fa: (A) -> C?,
    fb: (B) -> D?
  ): Ior<C, D>? =
    fold(
      { a -> fa(a)?.let { Left(it) } },
      { b -> fb(b)?.let { Right(it) } },
      { a, b -> nullable{ Both( fa(a).bind(), fb(b).bind()) } }
    )

public inline fun <AA, C, D> bitraverseValidated(
    SA: Semigroup<AA>,
    fa: (A) -> Validated<AA, C>,
    fb: (B) -> Validated<AA, D>
  ): Validated<AA, Ior<C, D>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> fa(a).zip(SA, fb(b)) { aa, c -> Both(aa, c) } }
    )

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ emptyList() },{ b -> fa(b).map { Right(it) } },{ a, b -> fa(b).map { Both(a, it) } })")
  )
  public inline fun <C> crosswalk(fa: (B) -> Iterable<C>): List<Ior<A, C>> =
    fold(
      { emptyList() },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) } }
    )

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ emptyMap() },{ b -> fa(b).mapValues { Right(it.value)} },{ a, b -> fa(b).mapValues { Both(a, it.value) })")
  )
  public inline fun <K, V> crosswalkMap(fa: (B) -> Map<K, V>): Map<K, Ior<A, V>> =
    fold(
      { emptyMap() },
      { b -> fa(b).mapValues { Right(it.value) } },
      { a, b -> fa(b).mapValues { Both(a, it.value) } }
    )

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ a -> Left(a) },{ b -> fa(b)?.let { Right(it) } },{ a, b -> fa(b)?.let { Both(a, it) } })")
  )
  public inline fun <A, B, C> crosswalkNull(ior: Ior<A, B>, fa: (B) -> C?): Ior<A, C>? =
    ior.fold(
      { a -> Left(a) },
      { b -> fa(b)?.let { Right(it) } },
      { a, b -> fa(b)?.let { Both(a, it) } }
    )

  @Deprecated(
    NicheAPI + "Prefer using fold, or map + getOrElse",
    ReplaceWith("fold({ true }, predicate, { _, b -> predicate(b) })")
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
  @Deprecated(
    NicheAPI + "Prefer using fold, or map + getOrElse",
    ReplaceWith("fold({ false }, predicate, { _, b -> predicate(b) })")
  )
  public inline fun exists(predicate: (B) -> Boolean): Boolean {
    contract { callsInPlace(predicate, InvocationKind.AT_MOST_ONCE) }
    return fold({ false }, predicate, { _, b -> predicate(b) })
  }

  @Deprecated(
    NicheAPI + "Prefer Kotlin nullable syntax instead",
    ReplaceWith("getOrNull()?.takeIf(predicate)")
  )
  public inline fun findOrNull(predicate: (B) -> Boolean): B? {
    contract { callsInPlace(predicate, InvocationKind.AT_MOST_ONCE) }
    return getOrNull()?.takeIf(predicate)
  }

  @Deprecated(
    NicheAPI + "Prefer using isLeft",
    ReplaceWith("isLeft")
  )
  public fun isEmpty(): Boolean {
    contract {
      returns(true) implies (this@Ior is Left<A>)
      returns(false) implies (this@Ior is Right<B> || this@Ior is Both<A, B>)
    }
    return isLeft
  }

  @Deprecated(
    NicheAPI + "Prefer using isRight",
    ReplaceWith("isRight")
  )
  public fun isNotEmpty(): Boolean {
    contract {
      returns(false) implies (this@Ior is Left<A>)
      returns(true) implies (this@Ior is Right<B> || this@Ior is Both<A, B>)
    }
    return !isLeft
  }

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ a -> listOf(Left(a)) }, { b -> fa(b).map { Right(it) } }, { a, b -> fa(b).map { Both(a, it) } })")
  )
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
    fold(
        { a -> Either.Right(Left(a)) },
        { b -> fa(b).map { Right(it) } },
        { a, b -> fa(b).map { Both(a, it) } })

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ a -> Either.Right(Left(a)) }, { b -> fa(b).map { Right(it) } }, { a, b -> fa(b).map { Both(a, it) } })")
  )
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

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ a -> Some(Left(a)) }, { b -> fa(b).map { Right(it) } }, { a, b -> fa(b).map { Both(a, it) } })")
  )
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
    fold(
        { a -> Some(Left(a)) },
        { b -> fa(b).map { Right(it) } },
        { a, b -> fa(b).map { Both(a, it) } })

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ a -> Left(a) }, {b -> fa(b)?.let { Right(it) } }, { a, b -> fa(b)?.let { Both(a, it) } })")
  )
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
  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith("fold({ a -> Valid(Left(a)) }, {b -> fa(b).map { Right(it) } }, { a, b -> fa(b).map { Both(a, it) } })")
  )public inline fun <AA, C> traverse(fa: (B) -> Validated<AA, C>): Validated<AA, Ior<A, C>> {
    contract { callsInPlace(fa, InvocationKind.AT_MOST_ONCE) }
    return fold(
      { a -> Valid(Left(a)) },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) } }
    )
  }

  @Deprecated("traverseValidated is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(fa)"))
  public inline fun <AA, C> traverseValidated(fa: (B) -> Validated<AA, C>): Validated<AA, Ior<A, C>> =
    fold(
        { a -> Valid(Left(a)) },
        { b -> fa(b).map { Right(it) } },
        { a, b -> fa(b).map { Both(a, it) } })

  @Deprecated(
    NicheAPI + "Prefer using map",
    ReplaceWith("map { }")
  )
  public fun void(): Ior<A, Unit> = map { }
}

/**
 * Binds the given function across [Ior.Right].
 *
 * @param f The function to bind across [Ior.Right].
 */
public inline fun <A, B, D> Ior<A, B>.flatMap(SG: Semigroup<A>, f: (B) -> Ior<A, D>): Ior<A, D> =
  when (this) {
    is Left -> this
    is Right -> f(value)
    is Both -> with(SG) {
      f(this@flatMap.rightValue).fold(
        { a -> Left(this@flatMap.leftValue.combine(a)) },
        { d -> Both(this@flatMap.leftValue, d) },
        { ll, rr -> Both(this@flatMap.leftValue.combine(ll), rr) }
      )
    }
  }

public inline fun <A, B> Ior<A, B>.getOrElse(default: () -> B): B {
  contract {callsInPlace(default, InvocationKind.AT_MOST_ONCE) }
  return fold({ default() }, ::identity, { _, b -> b })
}

public fun <A, B> Pair<A, B>.bothIor(): Ior<A, B> = Ior.Both(this.first, this.second)

public fun <A> A.leftIor(): Ior<A, Nothing> = Ior.Left(this)

public fun <A> A.rightIor(): Ior<Nothing, A> = Ior.Right(this)

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith("fold({ a -> identity(a).map { Left(it) } }, {b -> identity(b).map{ Right(it) } }, { a, b -> identity(a).zip(identity(b)) { aa, c -> Both(aa, c) } })")
)
public fun <A, B> Ior<Iterable<A>, Iterable<B>>.bisequence(): List<Ior<A, B>> =
  fold({a -> identity(a).map { Left(it) } },
        { b -> identity(b).map{ Right(it) } },
        { a, b -> identity(a).zip(identity(b)) { aa, c -> Both(aa, c) } })

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith("fold({ a -> identity(a).map { Left(it) } }, {b -> identity(b).map{ Right(it) } }, { a, b -> either { Both(identity(a).bind(), identity(b).bind()) } })")
)
public fun <A, B, C> Ior<Either<A, B>, Either<A, C>>.bisequenceEither(): Either<A, Ior<B, C>> =
  fold({ a -> identity(a).map{ Left(it) } },
        { b -> identity(b).map{ Right(it) } },
        { a, b -> either { Both(identity(a).bind(), identity(b).bind()) } })

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith("fold({ a -> identity(a).map { Left(it) } }, {b -> identity(b).map{ Right(it) } }, { a, b -> option { Both(identity(a).bind(), identity(b).bind()) } })")
)
public fun <B, C> Ior<Option<B>, Option<C>>.bisequenceOption(): Option<Ior<B, C>> =
  fold({ a -> identity(a).map{ Left(it) } },
        { b -> identity(b).map{ Right(it) } },
        { a, b -> option { Both(identity(a).bind(), identity(b).bind()) } })

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith("fold({ a -> identity(a)?.let{ Left(it) } }, {b -> identity(b)?.let{ Right(it) } }, { a, b -> nullable { Both(identity(a).bind(), identity(b).bind()) } })")
)
public fun <B, C> Ior<B?, C?>.bisequenceNullable(): Ior<B, C>? =
  fold(
        { a -> identity(a)?.let{ Left(it) } },
        { b -> identity(b)?.let{ Right(it) } },
        { a, b -> nullable { Both(identity(a).bind(), identity(b).bind()) } })

public fun <A, B, C> Ior<Validated<A, B>, Validated<A, C>>.bisequenceValidated(SA: Semigroup<A>): Validated<A, Ior<B, C>> =
  bitraverseValidated(SA, ::identity, ::identity)

public fun <A, B> Ior<A, B>.combine(SA: Semigroup<A>, SB: Semigroup<B>, other: Ior<A, B>): Ior<A, B> =
  with(SA) {
    with(SB) {
      when (val a = this@combine) {
        is Ior.Left -> when (other) {
          is Ior.Left -> Ior.Left(a.value + other.value)
          is Ior.Right -> Ior.Both(a.value, other.value)
          is Ior.Both -> Ior.Both(a.value + other.leftValue, other.rightValue)
        }
        is Ior.Right -> when (other) {
          is Ior.Left -> Ior.Both(other.value, a.value)
          is Ior.Right -> Ior.Right(a.value + other.value)
          is Ior.Both -> Ior.Both(other.leftValue, a.value + other.rightValue)
        }
        is Ior.Both -> when (other) {
          is Ior.Left -> Ior.Both(a.leftValue + other.value, a.rightValue)
          is Ior.Right -> Ior.Both(a.leftValue, a.rightValue + other.value)
          is Ior.Both -> Ior.Both(a.leftValue + other.leftValue, a.rightValue + other.rightValue)
        }
      }
    }
  }

@Suppress("NOTHING_TO_INLINE")
public inline fun <A, B> Ior<A, Ior<A, B>>.flatten(SA: Semigroup<A>): Ior<A, B> =
  flatMap(SA, ::identity)

public fun <A, B> Ior<A, B>.replicate(SA: Semigroup<A>, n: Int): Ior<A, List<B>> =
  if (n <= 0) Ior.Right(emptyList())
  else when (this) {
    is Ior.Right -> Ior.Right(List(n) { value })
    is Ior.Left -> this
    is Ior.Both -> map { List(n) { rightValue } }.mapLeft{ List(n - 1) { leftValue }.fold(leftValue) { acc, a -> SA.run { acc + a } } }
  }

public fun <A, B> Ior<A, B>.replicate(SA: Semigroup<A>, n: Int, MB: Monoid<B>): Ior<A, B> =
  if (n <= 0) Ior.Right(MB.empty())
  else when (this) {
    is Ior.Right -> Ior.Right(MB.run { List(n) { value }.fold() })
    is Ior.Left -> this
    is Ior.Both -> map { MB.run { List(n) { rightValue }.fold() } }.mapLeft {
      List(n - 1) { leftValue }.fold(
        leftValue
      ) { acc, a -> SA.run { acc + a } }
    }
  }

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith("fold({ a -> listOf(Left(a)) }, {b -> identity(b).map { Right(it) } }, { a, b -> identity(b).map{ Both(a, it) } })")
)
public fun <A, B> Ior<A, Iterable<B>>.sequence(): List<Ior<A, B>> =
  fold(
        { a -> listOf(Left(a)) },
        { b -> identity(b).map { Right(it) } },
        { a, b -> identity(b).map{ Both(a, it) } })

@Deprecated("sequenceEither is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence()", "arrow.core.sequence"))
public fun <A, B, C> Ior<A, Either<B, C>>.sequenceEither(): Either<B, Ior<A, C>> =
  sequence()

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith("fold({ a -> Either.Right(Left(a)) }, {b -> identity(b).map { Right(it) } }, { a, b -> identity(b).map{ Both(a, it) } })")
)
public fun <A, B, C> Ior<A, Either<B, C>>.sequence(): Either<B, Ior<A, C>> =
  fold(
        { a -> Either.Right(Left(a)) },
        { b -> identity(b).map { Right(it) } },
        { a, b -> identity(b).map { Both(a, it) } })

@Deprecated("sequenceOption is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence()", "arrow.core.sequence"))
public fun <A, B> Ior<A, Option<B>>.sequenceOption(): Option<Ior<A, B>> =
  sequence()

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith("fold({ a -> Some(Left(a)) }, {b -> identity(b).map { Right(it) } }, { a, b -> identity(b).map{ Both(a, it) } })")
)
public fun <A, B> Ior<A, Option<B>>.sequence(): Option<Ior<A, B>> =
  fold(
        { a -> Some(Left(a)) },
        { b -> identity(b).map { Right(it) } },
        { a, b -> identity(b).map { Both(a, it) } })

@Deprecated("sequenceOption is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence()", "arrow.core.sequence"))
public fun <A, B> Ior<A, B?>.sequenceNullable(): Ior<A, B>? =
  sequence()

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith("fold({ a -> Left(a) }, {b -> identity(b)?.let { Right(it) } }, { a, b -> identity(b)?.let{ Both(a, it) } })")
)
public fun <A, B> Ior<A, B?>.sequence(): Ior<A, B>? =
  fold(
        { a -> Left(a) },
        { b -> identity(b)?.let { Right(it) } },
        { a, b -> identity(b)?.let{ Both(a, it) } })

@Deprecated("sequenceValidated is being renamed to sequence to simplify the Arrow API", ReplaceWith("sequence()", "arrow.core.sequence"))
public fun <A, B, C> Ior<A, Validated<B, C>>.sequenceValidated(): Validated<B, Ior<A, C>> =
  sequence()

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith("fold({ a -> Valid(Left(a)) }, {b -> identity(b).map { Right(it) } }, { a, b -> identity(b).map { Both(a, it) } })")
)
public fun <A, B, C> Ior<A, Validated<B, C>>.sequence(): Validated<B, Ior<A, C>> =
  fold(
        { a -> Valid(Left(a)) },
        { b -> identity(b).map { Right(it) } },
        { a, b -> identity(b).map { Both(a, it) } })

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

@Deprecated(
  NicheAPI + "Prefer using widen",
  ReplaceWith("widen")
)
public fun <AA, A : AA, B> Ior<A, B>.leftWiden(): Ior<AA, B> =
  this

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA){ Pair(this@zip.bind(), fb.bind()) }")
)
public fun <A, B, C> Ior<A, B>.zip(SA: Semigroup<A>, fb: Ior<A, C>): Ior<A, Pair<B, C>> {

  return ior(SA){ Pair(this@zip.bind(), fb.bind()) }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA){ map(this@zip.bind(), c.bind()) }")
)
public inline fun <A, B, C, D> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  map: (B, C) -> D
): Ior<A, D> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return ior(SA){
    map(this@zip.bind(), c.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA){ map(this@zip.bind(), c.bind(), d.bind()) }")
)
public inline fun <A, B, C, D, E> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  map: (B, C, D) -> E
): Ior<A, E> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return ior(SA){
    map(this@zip.bind(), c.bind(), d.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA){ map(this@zip.bind(), c.bind(), d.bind()), e.bind() }")
)
public inline fun <A, B, C, D, E, F> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  map: (B, C, D, E) -> F
): Ior<A, F> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return ior(SA){
    map(this@zip.bind(), c.bind(), d.bind(), e.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA){ map(this@zip.bind(), c.bind(), d.bind()), e.bind(), f.bind() }")
)
public inline fun <A, B, C, D, E, F, G> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  map: (B, C, D, E, F) -> G
): Ior<A, G> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return ior(SA){
    map(this@zip.bind(), c.bind(), d.bind(), e.bind(), f.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA){ map(this@zip.bind(), c.bind(), d.bind()), e.bind(), f.bind(), g.bind() }")
)
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
  return ior(SA){
    map(this@zip.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA){ map(this@zip.bind(), c.bind(), d.bind()), e.bind(), f.bind(), g.bind(), h.bind() }")
)
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
  return ior(SA){
    map(this@zip.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA){ map(this@zip.bind(), c.bind(), d.bind()), e.bind(), f.bind(), g.bind(), h.bind(), i.bind() }")
)
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
  return ior(SA){
    map(this@zip.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA){ map(this@zip.bind(), c.bind(), d.bind()), e.bind(), f.bind(), g.bind(), h.bind(), i.bind(), j.bind() }")
)
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
  return ior(SA){
    map(this@zip.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind(), j.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA){ map(this@zip.bind(), c.bind(), d.bind()), e.bind(), f.bind(), g.bind(), h.bind(), i.bind(), j.bind(), k.bind() }")
)
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

  val leftValue: Any? = SA.run {
    var accumulatedLeft: Any? = EmptyValue

    if (this@zip is Left) return@zip Left(this@zip.value)
    accumulatedLeft =
      if (this@zip is Both) this@zip.leftValue else accumulatedLeft

    if (c is Left) return@zip Left(emptyCombine(accumulatedLeft, c.value))
    accumulatedLeft = if (c is Both) emptyCombine(accumulatedLeft, c.leftValue) else accumulatedLeft

    if (d is Left) return@zip Left(emptyCombine(accumulatedLeft, d.value))
    accumulatedLeft = if (d is Both) emptyCombine(accumulatedLeft, d.leftValue) else accumulatedLeft

    if (e is Left) return@zip Left(emptyCombine(accumulatedLeft, e.value))
    accumulatedLeft = if (e is Both) emptyCombine(accumulatedLeft, e.leftValue) else accumulatedLeft

    if (f is Left) return@zip Left(emptyCombine(accumulatedLeft, f.value))
    accumulatedLeft = if (f is Both) emptyCombine(accumulatedLeft, f.leftValue) else accumulatedLeft

    if (g is Left) return@zip Left(emptyCombine(accumulatedLeft, g.value))
    accumulatedLeft = if (g is Both) emptyCombine(accumulatedLeft, g.leftValue) else accumulatedLeft

    if (h is Left) return@zip Left(emptyCombine(accumulatedLeft, h.value))
    accumulatedLeft = if (h is Both) emptyCombine(accumulatedLeft, h.leftValue) else accumulatedLeft

    if (i is Left) return@zip Left(emptyCombine(accumulatedLeft, i.value))
    accumulatedLeft = if (i is Both) emptyCombine(accumulatedLeft, i.leftValue) else accumulatedLeft

    if (j is Left) return@zip Left(emptyCombine(accumulatedLeft, j.value))
    accumulatedLeft = if (j is Both) emptyCombine(accumulatedLeft, j.leftValue) else accumulatedLeft

    if (k is Left) return@zip Left(emptyCombine(accumulatedLeft, k.value))
    accumulatedLeft = if (k is Both) emptyCombine(accumulatedLeft, k.leftValue) else accumulatedLeft

    accumulatedLeft
  }

  return when {
    rightValue != EmptyValue && leftValue == EmptyValue -> Right(rightValue as L)
    rightValue != EmptyValue && leftValue != EmptyValue -> Both(leftValue as A, rightValue as L)
    rightValue == EmptyValue && leftValue != EmptyValue -> Left(leftValue as A)
    else -> throw ArrowCoreInternalException
  }
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
