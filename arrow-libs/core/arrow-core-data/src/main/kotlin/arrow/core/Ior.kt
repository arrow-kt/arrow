package arrow.core

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

typealias IorNel<A, B> = Ior<Nel<A>, B>

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
 * `[Semigroup]<`A`>, while other methods, like [toEither], ignore the `A` value in a [Ior.Both Both].
 *
 * [Ior]<`A`,`B`> is isomorphic to [Either]<[Either]<`A`,`B`>, [Pair]<`A`,`B`>>, but provides methods biased toward `B`
 * values, regardless of whether the `B` values appear in a [Ior.Right] or a [Ior.Both].
 * The isomorphic Either form can be accessed via the [unwrap] method.
 */
sealed class Ior<out A, out B> {

  /**
   * Returns `true` if this is a [Right], `false` otherwise.
   *
   * Example:
   * ```
   * Left("tulip").isRight           // Result: false
   * Right("venus fly-trap").isRight // Result: true
   * Both("venus", "fly-trap").isRight // Result: false
   * ```
   */
  abstract val isRight: Boolean

  /**
   * Returns `true` if this is a [Left], `false` otherwise.
   *
   * Example:
   * ```
   * Left("tulip").isLeft           // Result: true
   * Right("venus fly-trap").isLeft // Result: false
   * Both("venus", "fly-trap").isLeft // Result: false
   * ```
   */
  abstract val isLeft: Boolean

  /**
   * Returns `true` if this is a [Both], `false` otherwise.
   *
   * Example:
   * ```
   * Left("tulip").isBoth           // Result: false
   * Right("venus fly-trap").isBoth // Result: false
   * Both("venus", "fly-trap").isBoth // Result: true
   * ```
   */
  abstract val isBoth: Boolean

  companion object {
    /**
     * Create an [Ior] from two nullables if at least one of them is defined.
     *
     * @param a an element (nullable) for the left side of the [Ior]
     * @param b an element (nullable) for the right side of the [Ior]
     *
     * @return [null] if both [a] and [b] are [null]. Otherwise
     * an [Ior.Left], [Ior.Right], or [Ior.Both] if [a], [b], or both are defined (respectively).
     */
    fun <A, B> fromNullables(a: A?, b: B?): Ior<A, B>? =
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

    private tailrec fun <L, A, B> Semigroup<L>.loop(v: Ior<L, Either<A, B>>, f: (A) -> Ior<L, Either<A, B>>): Ior<L, B> = when (v) {
      is Left -> Left(v.value)
      is Right -> when (v.value) {
        is Either.Right -> Right(v.value.value)
        is Either.Left -> loop(f(v.value.value), f)
      }
      is Both -> when (v.rightValue) {
        is Either.Right -> Both(v.leftValue, v.rightValue.value)
        is Either.Left -> {
          when (val fnb = f(v.rightValue.value)) {
            is Left -> Left(v.leftValue.combine(fnb.value))
            is Right -> loop(Both(v.leftValue, fnb.value), f)
            is Both -> loop(Both(v.leftValue.combine(fnb.leftValue), fnb.rightValue), f)
          }
        }
      }
    }

    fun <L, A, B> tailRecM(a: A, f: (A) -> Ior<L, Either<A, B>>, SL: Semigroup<L>): Ior<L, B> =
      SL.run { loop(f(a), f) }

    fun <A, B> leftNel(a: A): IorNel<A, B> = Left(nonEmptyListOf(a))

    fun <A, B> bothNel(a: A, b: B): IorNel<A, B> = Both(nonEmptyListOf(a), b)

    /**
     *  Lifts a function `(B) -> C` to the [Ior] structure returning a polymorphic function
     *  that can be applied over all [Ior] values in the shape of Ior<A, B>
     *
     *  ```kotlin:ank:playground
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
    fun <A, B, C> lift(f: (B) -> C): (Ior<A, B>) -> Ior<A, C> =
      { it.map(f) }

    fun <A, B, C, D> lift(fa: (A) -> C, fb: (B) -> D): (Ior<A, B>) -> Ior<C, D> =
      { it.bimap(fa, fb) }

    val unit: Ior<Nothing, Unit> = Right(Unit)

    fun <L> unit(): Ior<L, Unit> = unit
  }

  /**
   * Applies `fa` if this is a [Left], `fb` if this is a [Right] or `fab` if this is a [Both]
   *
   * Example:
   * ```
   * val result: Ior<EmailContactInfo, PostalContactInfo> = obtainContactInfo()
   * result.fold(
   *      { log("only have this email info: $it") },
   *      { log("only have this postal info: $it") },
   *      { email, postal -> log("have this postal info: $postal and this email info: $email") }
   * )
   * ```
   *
   * @param fa the function to apply if this is a [Left]
   * @param fb the function to apply if this is a [Right]
   * @param fab the function to apply if this is a [Both]
   * @return the results of applying the function
   */
  inline fun <C> fold(fa: (A) -> C, fb: (B) -> C, fab: (A, B) -> C): C = when (this) {
    is Left -> fa(value)
    is Right -> fb(value)
    is Both -> fab(leftValue, rightValue)
  }

  inline fun <C> foldLeft(c: C, f: (C, B) -> C): C =
    fold({ c }, { f(c, it) }, { _, b -> f(c, b) })

  inline fun <C> foldRight(lc: Eval<C>, crossinline f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fold({ lc }, { Eval.defer { f(it, lc) } }, { _, b -> Eval.defer { f(b, lc) } })

  inline fun <C> foldMap(MN: Monoid<C>, f: (B) -> C): C = MN.run {
    foldLeft(MN.empty()) { b, a -> b.combine(f(a)) }
  }

  inline fun <C> bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
    fold({ f(c, it) }, { g(c, it) }, { a, b -> g(f(c, a), b) })

  inline fun <C> bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fold({ f(it, c) }, { g(it, c) }, { a, b -> f(a, g(b, c)) })

  inline fun <C> bifoldMap(MN: Monoid<C>, f: (A) -> C, g: (B) -> C): C = MN.run {
    bifoldLeft(MN.empty(), { c, a -> c.combine(f(a)) }, { c, b -> c.combine(g(b)) })
  }

  /**
   * The given function is applied if this is a [Right] or [Both] to `B`.
   *
   * Example:
   * ```
   * Ior.Right(12).map { "flower" } // Result: Right("flower")
   * Ior.Left(12).map { "flower" }  // Result: Left(12)
   * Ior.Both(12, "power").map { "flower $it" }  // Result: Both(12, "flower power")
   * ```
   */
  inline fun <D> map(f: (B) -> D): Ior<A, D> = fold(
    ::Left,
    { Right(f(it)) },
    { a, b -> Both(a, f(b)) }
  )

  /**
   * Apply `fa` if this is a [Left] or [Both] to `A`
   * and apply `fb` if this is [Right] or [Both] to `B`
   *
   * Example:
   * ```
   * Ior.Right(12).bimap ({ "flower" }, { 12 }) // Result: Right(12)
   * Ior.Left(12).bimap({ "flower" }, { 12 })  // Result: Left("flower")
   * Ior.Both(12, "power").bimap ({ a, b -> "flower $b" },{ a * 2})  // Result: Both("flower power", 24)
   * ```
   */
  inline fun <C, D> bimap(fa: (A) -> C, fb: (B) -> D): Ior<C, D> = fold(
    { Left(fa(it)) },
    { Right(fb(it)) },
    { a, b -> Both(fa(a), fb(b)) }
  )

  /**
   * The given function is applied if this is a [Left] or [Both] to `A`.
   *
   * Example:
   * ```
   * Ior.Right(12).map { "flower" } // Result: Right(12)
   * Ior.Left(12).map { "flower" }  // Result: Left("power")
   * Ior.Both(12, "power").map { "flower $it" }  // Result: Both("flower 12", "power")
   * ```
   */
  inline fun <C> mapLeft(fa: (A) -> C): Ior<C, B> = fold(
    { Left(fa(it)) },
    ::Right,
    { a, b -> Both(fa(a), b) }
  )

  /**
   * If this is a [Left], then return the left value in [Right] or vice versa,
   * when this is [Both] , left and right values are swap
   *
   * Example:
   * ```
   * Left("left").swap()   // Result: Right("left")
   * Right("right").swap() // Result: Left("right")
   * Both("left", "right").swap() // Result: Both("right", "left")
   * ```
   */
  fun swap(): Ior<B, A> = fold(
    { Right(it) },
    { Left(it) },
    { a, b -> Both(b, a) }
  )

  /**
   * Return the isomorphic [Either] of this [Ior]
   */
  fun unwrap(): Either<Either<A, B>, Pair<A, B>> = fold(
    { Either.Left(Either.Left(it)) },
    { Either.Left(Either.Right(it)) },
    { a, b -> Either.Right(Pair(a, b)) }
  )

  /**
   * Return this [Ior] as [Pair] of nullables]
   *
   * Example:
   * ```kotlin:ank:playground
   * import arrow.core.Ior
   *
   * //sampleStart
   * val right = Ior.Right(12).padNull()         // Result: Pair(null, 12)
   * val left = Ior.Left(12).padNull()           // Result: Pair(12, null)
   * val both = Ior.Both("power", 12).padNull()  // Result: Pair("power", 12)
   * //sampleEnd
   *
   * fun main() {
   *   println("right = $right")
   *   println("left = $left")
   *   println("both = $both")
   * }
   * ```
   */
  fun padNull(): Pair<A?, B?> = fold(
    { Pair(it, null) },
    { Pair(null, it) },
    { a, b -> Pair(a, b) }
  )

  /**
   * Returns a [Either.Right] containing the [Right] value or `B` if this is [Right] or [Both]
   * and [Either.Left] if this is a [Left].
   *
   * Example:
   * ```
   * Right(12).toEither() // Result: Either.Right(12)
   * Left(12).toEither()  // Result: Either.Left(12)
   * Both("power", 12).toEither()  // Result: Either.Right(12)
   * ```
   */
  fun toEither(): Either<A, B> =
    fold({ Either.Left(it) }, { Either.Right(it) }, { _, b -> Either.Right(b) })

  /**
   * Returns the [Right] value or `B` if this is [Right] or [Both]
   * and [null] if this is a [Left].
   *
   * Example:
   * ```kotlin:ank:playground
   * import arrow.core.Ior
   *
   * //sampleStart
   * val right = Ior.Right(12).orNull()         // Result: 12
   * val left = Ior.Left(12).orNull()           // Result: null
   * val both = Ior.Both(12, "power").orNull()  // Result: "power"
   * //sampleEnd
   * fun main() {
   *   println("right = $right")
   *   println("left = $left")
   *   println("both = $both")
   * }
   * ```
   */
  fun orNull(): B? =
    fold({ null }, { it }, { _, b -> b })

  /**
   * Returns the [Left] value or `A` if this is [Left] or [Both]
   * and [null] if this is a [Right].
   *
   * Example:
   * ```kotlin:ank:playground
   * import arrow.core.Ior
   *
   * //sampleStart
   * val right = Ior.Right(12).leftOrNull()         // Result: null
   * val left = Ior.Left(12).leftOrNull()           // Result: 12
   * val both = Ior.Both(12, "power").leftOrNull()  // Result: 12
   * //sampleEnd
   *
   * fun main() {
   *   println("right = $right")
   *   println("left = $left")
   *   println("both = $both")
   * }
   * ```
   */
  fun leftOrNull(): A? =
    fold({ it }, { null }, { a, _ -> a })

  /**
   * Returns a [Validated.Valid] containing the [Right] value or `B` if this is [Right] or [Both]
   * and [Validated.Invalid] if this is a [Left].
   *
   * Example:
   * ```
   * Right(12).toValidated() // Result: Valid(12)
   * Left(12).toValidated()  // Result: Invalid(12)
   * Both(12, "power").toValidated()  // Result: Valid("power")
   * ```
   */
  fun toValidated(): Validated<A, B> =
    fold({ Invalid(it) }, { Valid(it) }, { _, b -> Valid(b) })

  data class Left<out A>(val value: A) : Ior<A, Nothing>() {
    override val isRight: Boolean get() = false
    override val isLeft: Boolean get() = true
    override val isBoth: Boolean get() = false

    override fun toString(): String = "Ior.Left($value)"

    companion object
  }

  data class Right<out B>(val value: B) : Ior<Nothing, B>() {
    override val isRight: Boolean get() = true
    override val isLeft: Boolean get() = false
    override val isBoth: Boolean get() = false

    override fun toString(): String = "Ior.Right($value)"

    companion object
  }

  data class Both<out A, out B>(val leftValue: A, val rightValue: B) : Ior<A, B>() {
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

  inline fun <C, D> bicrosswalk(
    fa: (A) -> Iterable<C>,
    fb: (B) -> Iterable<D>
  ): List<Ior<C, D>> =
    fold(
      { a -> fa(a).map { it.leftIor() } },
      { b -> fb(b).map { it.rightIor() } },
      { a, b -> fa(a).align(fb(b)) }
    )

  inline fun <C, D, K> bicrosswalkMap(
    fa: (A) -> Map<K, C>,
    fb: (B) -> Map<K, D>
  ): Map<K, Ior<C, D>> =
    fold(
      { a -> fa(a).mapValues { it.value.leftIor() } },
      { b -> fb(b).mapValues { it.value.rightIor() } },
      { a, b -> fa(a).align(fb(b)) }
    )

  inline fun <C, D> bicrosswalkNull(
    fa: (A) -> C?,
    fb: (B) -> D?
  ): Ior<C, D>? =
    fold(
      { a -> fa(a)?.let { Left(it) } },
      { b -> fb(b)?.let { Right(it) } },
      { a, b -> fromNullables(fa(a), fb(b)) }
    )

  inline fun <AA, C> bitraverse(fa: (A) -> Iterable<AA>, fb: (B) -> Iterable<C>): List<Ior<AA, C>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> fa(a).zip(fb(b)) { aa, c -> Both(aa, c) } }
    )

  inline fun <AA, C, D> bitraverseEither(
    fa: (A) -> Either<AA, C>,
    fb: (B) -> Either<AA, D>
  ): Either<AA, Ior<C, D>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> fa(a).zip(fb(b)) { aa, c -> Both(aa, c) } }
    )

  inline fun <AA, C, D> bitraverseValidated(
    SA: Semigroup<AA>,
    fa: (A) -> Validated<AA, C>,
    fb: (B) -> Validated<AA, D>
  ): Validated<AA, Ior<C, D>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> fa(a).zip(SA, fb(b)) { aa, c -> Both(aa, c) } }
    )

  inline fun <C> crosswalk(fa: (B) -> Iterable<C>): List<Ior<A, C>> =
    fold(
      { emptyList() },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) } }
    )

  inline fun <K, V> crosswalkMap(fa: (B) -> Map<K, V>): Map<K, Ior<A, V>> =
    fold(
      { emptyMap() },
      { b -> fa(b).mapValues { Right(it.value) } },
      { a, b -> fa(b).mapValues { Both(a, it.value) } }
    )

  inline fun <A, B, C> crosswalkNull(ior: Ior<A, B>, fa: (B) -> C?): Ior<A, C>? =
    ior.fold(
      { a -> Left(a) },
      { b -> fa(b)?.let { Right(it) } },
      { a, b -> fa(b)?.let { Both(a, it) } }
    )

  inline fun all(predicate: (B) -> Boolean): Boolean =
    fold({ true }, predicate, { _, b -> predicate(b) })

  /**
   * Returns `false` if [Left] or returns the result of the application of
   * the given predicate to the [Right] value.
   *
   * Example:
   * ```
   * Ior.Both(5, 12).exists { it > 10 } // Result: true
   * Ior.Right(12).exists { it > 10 }   // Result: true
   * Ior.Right(7).exists { it > 10 }    // Result: false
   *
   * val left: Ior<Int, Int> = Ior.Left(12)
   * left.exists { it > 10 }      // Result: false
   * ```
   */
  inline fun exists(predicate: (B) -> Boolean): Boolean =
    fold({ false }, predicate, { _, b -> predicate(b) })

  inline fun findOrNull(predicate: (B) -> Boolean): B? =
    when (this) {
      is Left -> null
      is Right -> if (predicate(this.value)) this.value else null
      is Both -> if (predicate(this.rightValue)) this.rightValue else null
    }

  /**
   *  Applies [f] to an [B] inside [Ior] and returns the [Ior] structure with a pair of the [B] value and the
   *  computed [C] value as result of applying [f]
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Ior.Right("Hello").fproduct<String>({ "$it World" })
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  inline fun <C> fproduct(f: (B) -> C): Ior<A, Pair<B, C>> =
    map { b -> b to f(b) }

  fun isEmpty(): Boolean = isLeft

  fun isNotEmpty(): Boolean = !isLeft

  /**
   *  Replaces [B] inside [Ior] with [C] resulting in a Ior<A, C>
   *
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Ior.Left("Hello World").mapConst<String>("...")
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <C> mapConst(c: C): Ior<A, C> =
    map { c }

  inline fun <C> traverse(fa: (B) -> Iterable<C>): List<Ior<A, C>> =
    fold(
      { a -> listOf(Left(a)) },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) } }
    )

  inline fun <AA, C> traverseEither(fa: (B) -> Either<AA, C>): Either<AA, Ior<A, C>> =
    fold(
      { a -> Either.Right(Left(a)) },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) } }
    )

  inline fun <AA, C> traverseValidated(fa: (B) -> Validated<AA, C>): Validated<AA, Ior<A, C>> =
    fold(
      { a -> Valid(Left(a)) },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) } }
    )

  /**
   *  Pairs [C] with [B] returning a Ior<A, Pair<C, B>>
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Ior.Right("Hello").tupleLeft<String>("World")
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <C> tupleLeft(c: C): Ior<A, Pair<C, B>> =
    map { b -> c to b }

  /**
   *  Pairs [C] with [B] returning a Ior<A, Pair<B, C>>
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Ior.Right("Hello").tupleRight<String>("World")
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <C> tupleRight(c: C): Ior<A, Pair<B, C>> =
    map { b -> b to c }

  fun void(): Ior<A, Unit> =
    mapConst(Unit)
}

/**
 * Binds the given function across [Ior.Right].
 *
 * @param f The function to bind across [Ior.Right].
 */
inline fun <A, B, D> Ior<A, B>.flatMap(SG: Semigroup<A>, f: (B) -> Ior<A, D>): Ior<A, D> = fold(
  { Ior.Left(it) },
  f,
  { l, r ->
    with(SG) {
      f(r).fold(
        {
          Ior.Left(l.combine(it))
        },
        {
          Ior.Both(l, it)
        },
        { ll, rr ->
          Ior.Both(l.combine(ll), rr)
        }
      )
    }
  }
)

fun <A, B, D> Ior<A, B>.ap(SG: Semigroup<A>, ff: Ior<A, (B) -> D>): Ior<A, D> =
  flatMap(SG) { a -> ff.map { f -> f(a) } }

fun <A, B, D> Ior<A, B>.apEval(SG: Semigroup<A>, ff: Eval<Ior<A, (B) -> D>>): Eval<Ior<A, D>> =
  ff.map { ap(SG, it) }

inline fun <A, B> Ior<A, B>.getOrElse(default: () -> B): B =
  fold({ default() }, ::identity, { _, b -> b })

fun <A, B> Pair<A, B>.bothIor(): Ior<A, B> = Ior.Both(this.first, this.second)

fun <A> A.leftIor(): Ior<A, Nothing> = Ior.Left(this)

fun <A> A.rightIor(): Ior<Nothing, A> = Ior.Right(this)

fun <A, B> Ior<Iterable<A>, Iterable<B>>.bisequence(): List<Ior<A, B>> =
  bitraverse(::identity, ::identity)

fun <A, B, C> Ior<Either<A, B>, Either<A, C>>.bisequenceEither(): Either<A, Ior<B, C>> =
  bitraverseEither(::identity, ::identity)

fun <A, B, C> Ior<Validated<A, B>, Validated<A, C>>.bisequenceValidated(SA: Semigroup<A>): Validated<A, Ior<B, C>> =
  bitraverseValidated(SA, ::identity, ::identity)

fun <A, B> Ior<A, B>.combine(SA: Semigroup<A>, SB: Semigroup<B>, other: Ior<A, B>): Ior<A, B> =
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
inline fun <A, B> Ior<A, Ior<A, B>>.flatten(SA: Semigroup<A>): Ior<A, B> =
  flatMap(SA, ::identity)

inline fun <A, B> Ior<A, Boolean>.ifM(SA: Semigroup<A>, ifTrue: () -> Ior<A, B>, ifFalse: () -> Ior<A, B>): Ior<A, B> =
  flatMap(SA) { if (it) ifTrue() else ifFalse() }

inline fun <A, B, C> Ior<A, B>.mproduct(SA: Semigroup<A>, f: (B) -> Ior<A, C>): Ior<A, Pair<B, C>> =
  flatMap(SA) { a ->
    f(a).map { b -> a to b }
  }

fun <A, B> Ior<A, B>.replicate(SA: Semigroup<A>, n: Int): Ior<A, List<B>> =
  if (n <= 0) Ior.Right(emptyList())
  else when (this) {
    is Ior.Right -> Ior.Right(List(n) { value })
    is Ior.Left -> this
    is Ior.Both -> bimap(
      { List(n - 1) { leftValue }.fold(leftValue, { acc, a -> SA.run { acc + a } }) },
      { List(n) { rightValue } }
    )
  }

fun <A, B> Ior<A, B>.replicate(SA: Semigroup<A>, n: Int, MB: Monoid<B>): Ior<A, B> =
  if (n <= 0) Ior.Right(MB.empty())
  else when (this) {
    is Ior.Right -> Ior.Right(MB.run { List(n) { value }.combineAll() })
    is Ior.Left -> this
    is Ior.Both -> bimap(
      { List(n - 1) { leftValue }.fold(leftValue, { acc, a -> SA.run { acc + a } }) },
      { MB.run { List(n) { rightValue }.combineAll() } }
    )
  }

fun <A, B, C> Ior<A, Either<B, C>>.selectM(SA: Semigroup<A>, f: Ior<A, (B) -> C>): Ior<A, C> =
  flatMap(SA) {
    it.fold(
      { b -> f.map { ff -> ff(b) } },
      { c -> Ior.Right(c) }
    )
  }

fun <A, B> Ior<A, Iterable<B>>.sequence(): List<Ior<A, B>> =
  traverse(::identity)

fun <A, B, C> Ior<A, Either<B, C>>.sequenceEither(): Either<B, Ior<A, C>> =
  traverseEither(::identity)

fun <A, B, C> Ior<A, Validated<B, C>>.sequenceValidated(): Validated<B, Ior<A, C>> =
  traverseValidated(::identity)

/**
 * Given [B] is a sub type of [C], re-type this value from Ior<A, B> to Ior<A, B>
 *
 * ```kotlin:ank:playground:extension
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
 */
fun <A, C, B : C> Ior<A, B>.widen(): Ior<A, C> =
  this

fun <AA, A : AA, B> Ior<A, B>.leftWiden(): Ior<AA, B> =
  this

fun <A, B, C> Ior<A, B>.zip(SA: Semigroup<A>, fb: Ior<A, C>): Ior<A, Pair<B, C>> =
  zip(SA, fb, ::Pair)

inline fun <A, B, C, D> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  map: (B, C) -> D
): Ior<A, D> =
  zip(SA, c, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit) { b, c, _, _, _, _, _, _, _, _ -> map(b, c) }

inline fun <A, B, C, D, E> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  map: (B, C, D) -> E
): Ior<A, E> =
  zip(SA, c, d, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }

inline fun <A, B, C, D, E, F> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  map: (B, C, D, E) -> F
): Ior<A, F> =
  zip(SA, c, d, e, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit) { b, c, d, e, _, _, _, _, _, _ -> map(b, c, d, e) }

inline fun <A, B, C, D, E, F, G> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  map: (B, C, D, E, F) -> G
): Ior<A, G> =
  zip(SA, c, d, e, f, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit) { b, c, d, e, f, _, _, _, _, _ -> map(b, c, d, e, f) }

inline fun <A, B, C, D, E, F, G, H> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  map: (B, C, D, E, F, G) -> H
): Ior<A, H> =
  zip(SA, c, d, e, f, g, Ior.unit, Ior.unit, Ior.unit, Ior.unit) { b, c, d, e, f, g, _, _, _, _ -> map(b, c, d, e, f, g) }

inline fun <A, B, C, D, E, F, G, H, I> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  map: (B, C, D, E, F, G, H) -> I
): Ior<A, I> =
  zip(SA, c, d, e, f, g, h, Ior.unit, Ior.unit, Ior.unit) { b, c, d, e, f, g, h, _, _, _ -> map(b, c, d, e, f, g, h) }

inline fun <A, B, C, D, E, F, G, H, I, J> Ior<A, B>.zip(
  SA: Semigroup<A>,
  c: Ior<A, C>,
  d: Ior<A, D>,
  e: Ior<A, E>,
  f: Ior<A, F>,
  g: Ior<A, G>,
  h: Ior<A, H>,
  i: Ior<A, I>,
  map: (B, C, D, E, F, G, H, I) -> J
): Ior<A, J> =
  zip(SA, c, d, e, f, g, h, i, Ior.unit, Ior.unit) { b, c, d, e, f, g, h, i, _, _ -> map(b, c, d, e, f, g, h, i) }

inline fun <A, B, C, D, E, F, G, H, I, J, K> Ior<A, B>.zip(
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
): Ior<A, K> =
  zip(SA, c, d, e, f, g, h, i, j, Ior.unit) { b, c, d, e, f, g, h, i, j, _ -> map(b, c, d, e, f, g, h, i, j) }

inline fun <A, B, C, D, E, F, G, H, I, J, K, L> Ior<A, B>.zip(
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
): Ior<A, L> =
  if (this is Ior.Right && c is Ior.Right && d is Ior.Right && e is Ior.Right && f is Ior.Right && g is Ior.Right && h is Ior.Right && i is Ior.Right && j is Ior.Right && k is Ior.Right) {
    Ior.Right(map(value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value))
  } else if (this is Ior.Both && c is Ior.Both && d is Ior.Both && e is Ior.Both && f is Ior.Both && g is Ior.Both && h is Ior.Both && i is Ior.Both && j is Ior.Both && k is Ior.Both) {
    SA.run {
      Ior.Both(
        leftValue.combine(c.leftValue).combine(d.leftValue).combine(e.leftValue).combine(f.leftValue)
          .combine(g.leftValue).combine(h.leftValue).combine(i.leftValue).combine(j.leftValue).combine(k.leftValue),
        map(
          rightValue,
          c.rightValue,
          d.rightValue,
          e.rightValue,
          f.rightValue,
          g.rightValue,
          h.rightValue,
          i.rightValue,
          j.rightValue,
          k.rightValue
        )
      )
    }
  } else SA.run {
    var accumulatedLeft: A? = null
    accumulatedLeft = if (this@zip is Ior.Left) value.maybeCombine(accumulatedLeft) else accumulatedLeft
    accumulatedLeft = if (c is Ior.Left) c.value.maybeCombine(accumulatedLeft) else accumulatedLeft
    accumulatedLeft = if (d is Ior.Left) d.value.maybeCombine(accumulatedLeft) else accumulatedLeft
    accumulatedLeft = if (e is Ior.Left) e.value.maybeCombine(accumulatedLeft) else accumulatedLeft
    accumulatedLeft = if (f is Ior.Left) f.value.maybeCombine(accumulatedLeft) else accumulatedLeft
    accumulatedLeft = if (g is Ior.Left) g.value.maybeCombine(accumulatedLeft) else accumulatedLeft
    accumulatedLeft = if (h is Ior.Left) h.value.maybeCombine(accumulatedLeft) else accumulatedLeft
    accumulatedLeft = if (i is Ior.Left) i.value.maybeCombine(accumulatedLeft) else accumulatedLeft
    accumulatedLeft = if (j is Ior.Left) j.value.maybeCombine(accumulatedLeft) else accumulatedLeft
    accumulatedLeft = if (j is Ior.Left) j.value.maybeCombine(accumulatedLeft) else accumulatedLeft
    Ior.Left(accumulatedLeft!!)
  }

fun <A, B, C, Z> Ior<A, B>.zipEval(SA: Semigroup<A>, other: Eval<Ior<A, C>>, f: (B, C) -> Z): Eval<Ior<A, Z>> =
  other.map { zip(SA, it).map { a -> f(a.first, a.second) } }

fun <A, B> Semigroup.Companion.ior(SA: Semigroup<A>, SB: Semigroup<B>): Semigroup<Ior<A, B>> =
  IorSemigroup(SA, SB)

private class IorSemigroup<A, B>(
  private val SGA: Semigroup<A>,
  private val SGB: Semigroup<B>
) : Semigroup<Ior<A, B>> {

  override fun Ior<A, B>.combine(b: Ior<A, B>): Ior<A, B> =
    combine(SGA, SGB, b)

  override fun Ior<A, B>.maybeCombine(b: Ior<A, B>?): Ior<A, B> =
    b?.let { combine(SGA, SGB, it) } ?: this
}

operator fun <A : Comparable<A>, B : Comparable<B>> Ior<A, B>.compareTo(other: Ior<A, B>): Int = fold(
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
