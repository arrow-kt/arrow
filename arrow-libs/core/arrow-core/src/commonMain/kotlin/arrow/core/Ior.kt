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
import arrow.typeclasses.SemigroupDeprecation
import arrow.typeclasses.combine
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.js.JsName
import kotlin.jvm.JvmName
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
  @Deprecated(
    RedundantAPI + "Use isRight()",
    ReplaceWith("isRight()")
  )
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
  @Deprecated(
    RedundantAPI + "Use isLeft()",
    ReplaceWith("isLeft()")
  )
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

  @Deprecated(
    RedundantAPI + "Use isBoth()",
    ReplaceWith("isBoth()")
  )
  public abstract val isBoth: Boolean


  @JsName("_isLeft")
  @JvmName("_isLeft")
  public fun isLeft(): Boolean {
    contract {
      returns(true) implies (this@Ior is Ior.Left<A>)
      returns(false) implies (this@Ior is Ior.Right<B>)
      returns(false) implies (this@Ior is Ior.Both<A, B>)
    }
    return this@Ior is Ior.Left<A>
  }

  @JsName("_isRight")
  @JvmName("_isRight")
  public fun isRight(): Boolean {
    contract {
      returns(true) implies (this@Ior is Ior.Right<B>)
      returns(false) implies (this@Ior is Ior.Left<A>)
      returns(false) implies (this@Ior is Ior.Both<A, B>)
    }
    return this@Ior is Ior.Right<B>
  }

  @JsName("_isBoth")
  @JvmName("_isBoth")
  public fun isBoth(): Boolean {
    contract {
      returns(false) implies (this@Ior is Ior.Right<B>)
      returns(false) implies (this@Ior is Ior.Left<A>)
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
    @Deprecated(
      RedundantAPI + "Prefer explicitly creating lambdas",
      ReplaceWith("{ it.map(f) }")
    )
    public fun <A, B, C> lift(f: (B) -> C): (Ior<A, B>) -> Ior<A, C> =
      { it.map(f) }

    @JvmStatic
    @Deprecated(
      RedundantAPI + "Prefer explicitly creating lambdas",
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
    ReplaceWith("this.fold<C>({ MN.empty() }, { f }, { _, b -> f(b) })")
  )
  public inline fun <C> foldMap(MN: Monoid<C>, f: (B) -> C): C {
    contract { callsInPlace(f, InvocationKind.AT_MOST_ONCE) }
    return MN.run {
      fold({ MN.empty() }, { f(it) }, { _, b -> f(b) })
    }
  }

  @Deprecated(
    NicheAPI + "Prefer when or fold instead",
    ReplaceWith("this.fold<C>({ f(c, it) }, { g(c, it) }, { a, b -> g(f(c, a), b) })")
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
      fold({ f(it) }, { g(it) }, { a, b -> f(a).combine(g(b)) })
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
  @Deprecated(
    "padNull is being renamed to toPair to be more consistent with the Kotlin Standard Library naming",
    ReplaceWith("toPair()")
  )
  public fun padNull(): Pair<A?, B?> = fold(
    { Pair(it, null) },
    { Pair(null, it) },
    { a, b -> Pair(a, b) }
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
  @Deprecated(
    NicheAPI + "Prefer using fold",
    ReplaceWith(
      "this.fold({ Invalid(it) }, { Valid(it) }, { _, b -> Valid(b) })",
      "arrow.core.Validated",
      "arrow.core.Valid",
      "arrow.core.Invalid"
    )
  )
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
    ReplaceWith(
      "fold({ a -> fa(a).map { it.leftIor() } }, { b -> fb(b).map { it.rightIor() } },{ a, b -> fa(a).align(fb(b)) })",
      "arrow.core.leftIor",
      "arrow.core.rightIor",
      "arrow.core.align"
    )
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
    ReplaceWith(
      "this.fold<K, Ior<C, D>>( { a -> fa(a).mapValues { it.value.leftIor() } },{ b -> fb(b).mapValues { it.value.rightIor() } },{ a, b -> fa(a).align(fb(b)) })",
      "arrow.core.leftIor",
      "arrow.core.rightIor",
      "arrow.core.align"
    )
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
    ReplaceWith("fold({ a -> fa(a)?.let { Ior.Left(it) } },{ b -> fb(b)?.let { Ior.Right(it) } },{ a, b -> fromNullables(fa(a), fb(b)) })")
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
    ReplaceWith(
      "fold({ a -> fa(a).map { Ior.Left(it) } },{ b -> fb(b).map { Ior.Right(it) } },{ a, b -> fa(a).zip(fb(b)) { aa, c -> Ior.Both(aa, c) } })",
      "arrow.core.Ior"
    )
  )
  public inline fun <AA, C> bitraverse(fa: (A) -> Iterable<AA>, fb: (B) -> Iterable<C>): List<Ior<AA, C>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> fa(a).zip(fb(b)) { aa, c -> Both(aa, c) } }
    )


  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith(
      "fold({ a -> fa(a).map { Ior.Left(it) } },{ b -> fb(b).map { Ior.Right(it) } },{ a, b -> either { Ior.Both(fa(a).bind(), fb(b).bind())} })",
      "arrow.core.Ior",
      "arrow.core.raise.either"
    )
  )
  public inline fun <AA, C, D> bitraverseEither(
    fa: (A) -> Either<AA, C>,
    fb: (B) -> Either<AA, D>
  ): Either<AA, Ior<C, D>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> either { Both(fa(a).bind(), fb(b).bind()) } }
    )

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith(
      "fold({ a -> fa(a).map { Ior.Left(it) } },{ b -> fb(b).map { Ior.Right(it) } },{ a, b -> option { Ior.Both(fa(a).bind(), fb(b).bind())} })",
      "arrow.core.Ior",
      "arrow.core.raise.option"
    )
  )
  public inline fun <C, D> bitraverseOption(
    fa: (A) -> Option<C>,
    fb: (B) -> Option<D>
  ): Option<Ior<C, D>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> option { Both(fa(a).bind(), fb(b).bind()) } }
    )

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith(
      "fold({ a -> fa(a)?.let { Ior.Left(it) } },{ b -> fb(b)?.let { Ior.Right(it) } }, { a, b -> nullable { Ior.Both( fa(a).bind(), fb(b).bind()) } })",
      "arrow.core.Ior", "arrow.core.raise.nullable"
    )
  )
  public inline fun <C, D> bitraverseNullable(
    fa: (A) -> C?,
    fb: (B) -> D?
  ): Ior<C, D>? =
    fold(
      { a -> fa(a)?.let { Left(it) } },
      { b -> fb(b)?.let { Right(it) } },
      { a, b -> nullable { Both(fa(a).bind(), fb(b).bind()) } }
    )

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith(
      "this.fold({ a -> fa(a).map { Ior.Left(it) } }, { b -> fb(b).map { Ior.Right(it) } }, { a, b -> fa(a).zip(SA, fb(b)) { aa, c -> Ior.Both(aa, c) } })",
      "arrow.core.Ior"
    )
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
    ReplaceWith(
      "fold({ emptyList() },{ b -> fa(b).map { Ior.Right(it) } },{ a, b -> fa(b).map { Ior.Both(a, it) } })",
      "arrow.core.Ior"
    )
  )
  public inline fun <C> crosswalk(fa: (B) -> Iterable<C>): List<Ior<A, C>> =
    fold(
      { emptyList() },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) } }
    )

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith(
      "fold({ emptyMap() },{ b -> fa(b).mapValues { Ior.Right(it.value)} },{ a, b -> fa(b).mapValues { Ior.Both(a, it.value) })",
      "arrow.core.Ior"
    )
  )
  public inline fun <K, V> crosswalkMap(fa: (B) -> Map<K, V>): Map<K, Ior<A, V>> =
    fold(
      { emptyMap() },
      { b -> fa(b).mapValues { Right(it.value) } },
      { a, b -> fa(b).mapValues { Both(a, it.value) } }
    )

  @Deprecated(
    NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
    ReplaceWith(
      "fold({ a -> Ior.Left(a) },{ b -> fa(b)?.let { Ior.Right(it) } },{ a, b -> fa(b)?.let { Ior.Both(a, it) } })",
      "arrow.core.Ior"
    )
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
   * <!--- KNIT example-ior-14.kt -->
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
   * <!--- KNIT example-ior-15.kt -->
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
   * <!--- KNIT example-ior-16.kt -->
   */
  public inline fun isBoth(leftPredicate: (A) -> Boolean, rightPredicate: (B) -> Boolean): Boolean {
    contract {
      returns(true) implies (this@Ior is Both<A, B>)
      returns(false) implies (this@Ior is Left<A> || this@Ior is Right<B>)
    }
    return this@Ior is Both<A, B> && leftPredicate(leftValue) && rightPredicate(rightValue)
  }


  @Deprecated(
    NicheAPI + "Prefer using isLeft",
    ReplaceWith("isLeft()")
  )
  public fun isEmpty(): Boolean {
    contract {
      returns(true) implies (this@Ior is Left<A>)
      returns(false) implies (this@Ior is Right<B> || this@Ior is Both<A, B>)
    }
    return isLeft
  }

  @Deprecated(
    NicheAPI + "Prefer using isRight and isBoth",
    ReplaceWith("(this.isRight() || this.isBoth())")
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
    ReplaceWith(
      "fold({ a -> listOf(Ior.Left(a)) }, { b -> fa(b).map { Ior.Right(it) } }, { a, b -> fa(b).map { Ior.Both(a, it) } })",
      "arrow.core.Ior"
    )
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
    ReplaceWith(
      "fold({ a -> Either.Right(Ior.Left(a)) }, { b -> fa(b).map { Ior.Right(it) } }, { a, b -> fa(b).map { Ior.Both(a, it) } })",
      "arrow.core.Either",
      "arrow.core.Ior"
    )
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
    ReplaceWith(
      "fold({ a -> Some(Ior.Left(a)) }, { b -> fa(b).map { Ior.Right(it) } }, { a, b -> fa(b).map { Ior.Both(a, it) } })",
      "arrow.core.Ior",
      "arrow.core.Some"
    )
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
    ReplaceWith(
      "fold({ a -> Ior.Left(a) }, {b -> fa(b)?.let { Ior.Right(it) } }, { a, b -> fa(b)?.let { Ior.Both(a, it) } })",
      "arrow.core.Ior"
    )
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
    ReplaceWith(
      "fold({ a -> Valid(Ior.Left(a)) }, {b -> fa(b).map { Ior.Right(it) } }, { a, b -> fa(b).map { Ior.Both(a, it) } })",
      "arrow.core.Ior"
    )
  )
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

@Deprecated(
  "$SemigroupDeprecation.",
  ReplaceWith("flatMap(SA::combine, f)", "arrow.typeclasses.combine")
)
public inline fun <A, B, D> Ior<A, B>.flatMap(SG: Semigroup<A>, f: (B) -> Ior<A, D>): Ior<A, D> =
  flatMap(SG::combine, f)

/**
 * Binds the given function across [Ior.Right].
 *
 * @param f The function to bind across [Ior.Right].
 */
public inline fun <A, B, D> Ior<A, B>.flatMap(combine: (A, A) -> A, f: (B) -> Ior<A, D>): Ior<A, D> =
  when (this) {
    is Left -> this
    is Right -> f(value)
    is Both -> f(rightValue).fold(
      { a -> Left(combine(leftValue, a)) },
      { d -> Both(leftValue, d) },
      { ll, rr -> Both(combine(leftValue, ll), rr) }
    )
  }

@Deprecated(
  RedundantAPI + "This API is overloaded with an API with a single argument",
  level = DeprecationLevel.HIDDEN
)
public inline fun <A, B> Ior<A, B>.getOrElse(default: () -> B): B {
  contract { callsInPlace(default, InvocationKind.AT_MOST_ONCE) }
  return fold({ default() }, ::identity, { _, b -> b })
}

public inline fun <A, B> Ior<A, B>.getOrElse(default: (A) -> B): B {
  contract { callsInPlace(default, InvocationKind.AT_MOST_ONCE) }
  return fold(default, ::identity) { _, b -> b }
}


public fun <A, B> Pair<A, B>.bothIor(): Ior<A, B> = Ior.Both(this.first, this.second)

public fun <A> A.leftIor(): Ior<A, Nothing> = Ior.Left(this)

public fun <A> A.rightIor(): Ior<Nothing, A> = Ior.Right(this)

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith(
    "this.fold<List<Ior<A, B>>>({ a: List<A> -> a.map<A, Ior.Left<A>> { Ior.Left(it) } }, {b: List<B> -> b.map<B, Ior.Right<B>>{ Ior.Right(it) } }, { a, b -> a.zip<A, B, Ior.Both<A, B>>(b) { aa, c -> Ior.Both(aa, c) } })",
    "arrow.core.Ior"
  )
)
public fun <A, B> Ior<Iterable<A>, Iterable<B>>.bisequence(): List<Ior<A, B>> =
  fold({ a -> a.map { Left(it) } },
    { b -> b.map { Right(it) } },
    { a, b -> a.zip(b) { aa, c -> Both(aa, c) } })

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith(
    "this.fold<Either<A, Ior<B, C>>>({ a -> a.map<Ior.Left<B>> { Ior.Left(it) } }, {b -> b.map<Ior.Right<C>>{ Ior.Right(it) } }, { a: Either<A, B>, b: Either<A, C> -> either<A, Ior.Both<B,C>> { Ior.Both(a.bind(), b.bind()) } })",
    "arrow.core.Ior",
    "arrow.core.raise.either"
  )
)
public fun <A, B, C> Ior<Either<A, B>, Either<A, C>>.bisequenceEither(): Either<A, Ior<B, C>> =
  fold({ a -> a.map { Left(it) } },
    { b -> b.map { Right(it) } },
    { a, b -> either { Both(a.bind(), b.bind()) } })

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith(
    "this.fold<Option<Ior<B, C>>>({ a -> a.map<Ior.Left<B>> { Ior.Left(it) } }, {b -> b.map<Ior.Right<C>>{ Ior.Right(it) } }, { a, b -> option<Ior.Both<B, C>> { Ior.Both(a.bind(), b.bind()) } })",
    "arrow.core.Ior",
    "arrow.core.raise.option"
  )
)
public fun <B, C> Ior<Option<B>, Option<C>>.bisequenceOption(): Option<Ior<B, C>> =
  fold({ a -> a.map { Left(it) } },
    { b -> b.map { Right(it) } },
    { a, b -> option { Both(a.bind(), b.bind()) } })

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith(
    "fold({ a -> a?.let<B, Ior.Left<B>>{ Ior.Left<B>(it) } }, {b -> b?.let<C, Ior.Right<C>>{ Ior.Right<C>(it) } }, { a, b -> nullable<Ior.Both<B, C>> { Ior.Both(a.bind<B>(), b.bind<C>()) } })",
    "arrow.core.Ior",
    "arrow.core.raise.nullable"
  )
)
public fun <B, C> Ior<B?, C?>.bisequenceNullable(): Ior<B, C>? =
  fold(
    { a -> a?.let { Left(it) } },
    { b -> b?.let { Right(it) } },
    { a, b -> nullable { Both(a.bind(), b.bind()) } })

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith(
    "this.fold({ a -> fa(a).map { Ior.Left(it) } }, { b -> fb(b).map { Ior.Right(it) } }, { a, b -> fa(a).zip(SA, fb(b)) { aa, c -> Ior.Both(aa, c) } })",
    "arrow.core.Ior"
  )
)
public fun <A, B, C> Ior<Validated<A, B>, Validated<A, C>>.bisequenceValidated(SA: Semigroup<A>): Validated<A, Ior<B, C>> =
  bitraverseValidated(SA, ::identity, ::identity)

@Deprecated(
  "$SemigroupDeprecation",
  ReplaceWith("combine(other, SA::combine, SB::combine)", "arrow.typeclasses.combine")
)
public fun <A, B> Ior<A, B>.combine(SA: Semigroup<A>, SB: Semigroup<B>, other: Ior<A, B>): Ior<A, B> =
  combine(other, SA::combine, SB::combine)

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

@Suppress("NOTHING_TO_INLINE")
@Deprecated(
  "$SemigroupDeprecation.",
  ReplaceWith("flatten(SA::combine)", "arrow.typeclasses.combine")
)
public inline fun <A, B> Ior<A, Ior<A, B>>.flatten(SA: Semigroup<A>): Ior<A, B> =
  flatMap(SA, ::identity)

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith(
    "if (n <= 0) Ior.Right<List<B>>(emptyList<B>())\n" +
      "  else when (this) {\n" +
      "    is Ior.Right -> Ior.Right<List<B>>(List(n) { value })\n" +
      "    is Ior.Left -> this\n" +
      "    is Ior.Both -> map { List(n) { rightValue } }.mapLeft<A>{ List(n - 1) { leftValue }.fold(leftValue) { acc, a ->  acc + a } }\n" +
      "  }", "arrow.core.Ior"
  )
)
public fun <A, B> Ior<A, B>.replicate(SA: Semigroup<A>, n: Int): Ior<A, List<B>> =
  if (n <= 0) Ior.Right(emptyList())
  else when (this) {
    is Ior.Right -> Ior.Right(List(n) { value })
    is Ior.Left -> this
    is Ior.Both -> map { List(n) { rightValue } }.mapLeft { List(n - 1) { leftValue }.fold(leftValue) { acc, a -> SA.run { acc + a } } }
  }

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith(
    "if (n <= 0) Ior.Right(MB.empty())\n" +
      "  else when (this) {\n" +
      "    is Ior.Right -> Ior.Right(MB.run { List(n) { value }.fold() })\n" +
      "    is Ior.Left -> this\n" +
      "    is Ior.Both -> map { MB.run { List(n) { rightValue }.fold() } }.mapLeft {\n" +
      "      List(n - 1) { leftValue }.fold(\n" +
      "        leftValue\n" +
      "      ) { acc, a -> SA.run { acc + a } }\n" +
      "    }\n" +
      "  }", "arrow.core.Ior"
  )
)
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
  ReplaceWith(
    "fold({ a -> listOf<Ior.Left<A>>(Ior.Left(a)) }, {b -> b.map<B, Ior.Right<B>>{ Ior.Right(it) } }, { a, b -> b.map<B, Ior.Both<A, B>>{ Ior.Both(a, it) } })",
    "arrow.core.Ior"
  )
)
public fun <A, B> Ior<A, Iterable<B>>.sequence(): List<Ior<A, B>> =
  fold(
    { a -> listOf(Left(a)) },
    { b -> b.map { Right(it) } },
    { a, b -> b.map { Both(a, it) } })

@Deprecated(
  "sequenceEither is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <A, B, C> Ior<A, Either<B, C>>.sequenceEither(): Either<B, Ior<A, C>> =
  sequence()

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith(
    "fold({ a -> Either.Right<Ior.Left<A>>(Ior.Left(a)) }, {b -> b.map<Ior.Right<C>> { Ior.Right(it) } }, { a, b -> b.map<Ior.Both<A, C>>{ Ior.Both(a, it) } })",
    "arrow.core.Ior"
  )
)
public fun <A, B, C> Ior<A, Either<B, C>>.sequence(): Either<B, Ior<A, C>> =
  fold(
    { a -> Either.Right(Left(a)) },
    { b -> b.map { Right(it) } },
    { a, b -> b.map { Both(a, it) } })

@Deprecated(
  "sequenceOption is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <A, B> Ior<A, Option<B>>.sequenceOption(): Option<Ior<A, B>> =
  sequence()

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith(
    "fold({ a -> Some<Ior.Left<A>>(Ior.Left(a)) }, {b -> b.map<Ior.Right<B>> { Ior.Right(it) } }, { a, b -> b.map<Ior.Both<A, B>>{ Ior.Both(a, it) } })",
    "arrow.core.Ior",
    "arrow.core.Some"
  )
)
public fun <A, B> Ior<A, Option<B>>.sequence(): Option<Ior<A, B>> =
  fold(
    { a -> Some(Left(a)) },
    { b -> b.map { Right(it) } },
    { a, b -> b.map { Both(a, it) } })

@Deprecated(
  "sequenceOption is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <A, B> Ior<A, B?>.sequenceNullable(): Ior<A, B>? =
  sequence()

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith(
    "fold({ a -> Ior.Left(a) }, {b -> b?.let<B, Ior.Right<B>> { Ior.Right(it) } }, { a, b -> b?.let<B, Ior.Both<A, B>>{ Ior.Both(a, it) } })",
    "arrow.core.Ior"
  )
)
public fun <A, B> Ior<A, B?>.sequence(): Ior<A, B>? =
  fold(
    { a -> Left(a) },
    { b -> b?.let { Right(it) } },
    { a, b -> b?.let { Both(a, it) } })

@Deprecated(
  "sequenceValidated is being renamed to sequence to simplify the Arrow API",
  ReplaceWith("sequence()", "arrow.core.sequence")
)
public fun <A, B, C> Ior<A, Validated<B, C>>.sequenceValidated(): Validated<B, Ior<A, C>> =
  sequence()

@Deprecated(
  NicheAPI + "Prefer using Ior DSL, or explicit fold, or when",
  ReplaceWith(
    "fold({ a -> Valid(Ior.Left(a)) }, {b -> b.map { Ior.Right(it) } }, { a, b -> b.map { Ior.Both(a, it) } })",
    "arrow.core.Ior",
    "arrow.core.Valid"
  )
)
public fun <A, B, C> Ior<A, Validated<B, C>>.sequence(): Validated<B, Ior<A, C>> =
  fold(
    { a -> Valid(Left(a)) },
    { b -> b.map { Right(it) } },
    { a, b -> b.map { Both(a, it) } })

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
 * <!--- KNIT example-ior-17.kt -->
 */
public fun <A, C, B : C> Ior<A, B>.widen(): Ior<A, C> =
  this

@Deprecated(
  NicheAPI + "Prefer using widen",
  ReplaceWith("widen", "arrow.core.widen")
)
public fun <AA, A : AA, B> Ior<A, B>.leftWiden(): Ior<AA, B> =
  this

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA::combine) { Pair(this@zip.bind(), fb.bind()) }", "arrow.core.raise.ior", "arrow.typeclasses.combine")
)
public fun <A, B, C> Ior<A, B>.zip(SA: Semigroup<A>, fb: Ior<A, C>): Ior<A, Pair<B, C>> =
  ior(SA::combine) { Pair(this@zip.bind(), fb.bind()) }

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA::combine) { map(this@zip.bind(), c.bind()) }", "arrow.core.raise.ior", "arrow.typeclasses.combine")
)
public inline fun <A, B, C, D> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  map: (B, C) -> D
): Ior<A, D> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return ior(SA::combine) {
    map(this@zip.bind(), c.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA::combine) { map(this@zip.bind(), c.bind(), d.bind()) }", "arrow.core.raise.ior", "arrow.typeclasses.combine")
)
public inline fun <A, B, C, D, E> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  map: (B, C, D) -> E
): Ior<A, E> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return ior(SA::combine) {
    map(this@zip.bind(), c.bind(), d.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA::combine) { map(this@zip.bind(), c.bind(), d.bind()), e.bind() }", "arrow.core.raise.ior", "arrow.typeclasses.combine")
)
public inline fun <A, B, C, D, E, F> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  map: (B, C, D, E) -> F
): Ior<A, F> {
  contract { callsInPlace(map, InvocationKind.AT_MOST_ONCE) }
  return ior(SA::combine) {
    map(this@zip.bind(), c.bind(), d.bind(), e.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith("ior(SA::combine) { map(this@zip.bind(), c.bind(), d.bind()), e.bind(), f.bind() }", "arrow.core.raise.ior", "arrow.typeclasses.combine")
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
  return ior(SA::combine) {
    map(this@zip.bind(), c.bind(), d.bind(), e.bind(), f.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith(
    "ior(SA::combine) { map(this@zip.bind(), c.bind(), d.bind()), e.bind(), f.bind(), g.bind() }",
    "arrow.core.raise.ior", "arrow.typeclasses.combine"
  )
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
  return ior(SA::combine) {
    map(this@zip.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith(
    "ior(SA::combine) { map(this@zip.bind(), c.bind(), d.bind()), e.bind(), f.bind(), g.bind(), h.bind() }",
    "arrow.core.raise.ior", "arrow.typeclasses.combine"
  )
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
  return ior(SA::combine) {
    map(this@zip.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith(
    "ior(SA::combine) { map(this@zip.bind(), c.bind(), d.bind()), e.bind(), f.bind(), g.bind(), h.bind(), i.bind() }",
    "arrow.core.raise.ior", "arrow.typeclasses.combine"
  )
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
  return ior(SA::combine) {
    map(this@zip.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith(
    "ior(SA::combine) { map(this@zip.bind(), c.bind(), d.bind()), e.bind(), f.bind(), g.bind(), h.bind(), i.bind(), j.bind() }",
    "arrow.core.raise.ior", "arrow.typeclasses.combine"
  )
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
  return ior(SA::combine) {
    map(this@zip.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind(), j.bind())
  }
}

@Deprecated(
  NicheAPI + "Prefer using the inline ior DSL",
  ReplaceWith(
    "ior(SA::combine) { map(this@zip.bind(), c.bind(), d.bind()), e.bind(), f.bind(), g.bind(), h.bind(), i.bind(), j.bind(), k.bind() }",
    "arrow.core.raise.ior", "arrow.typeclasses.combine"
  )
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
    (this@zip.isRight() || this@zip.isBoth()) &&
    (c.isRight() || c.isBoth()) &&
    (d.isRight() || d.isBoth()) &&
    (e.isRight() || e.isBoth()) &&
    (f.isRight() || f.isBoth()) &&
    (g.isRight() || g.isBoth()) &&
    (h.isRight() || h.isBoth()) &&
    (i.isRight() || i.isBoth()) &&
    (j.isRight() || j.isBoth()) &&
    (k.isRight() || k.isBoth())
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
