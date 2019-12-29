package arrow.core

import arrow.Kind
import arrow.core.Ior.Left
import arrow.core.Ior.Right
import arrow.higherkind
import arrow.typeclasses.Applicative
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
@higherkind
sealed class Ior<out A, out B> : IorOf<A, B> {

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
     * Create an [Ior] from two Options if at least one of them is defined.
     *
     * @param oa an element (optional) for the left side of the [Ior]
     * @param ob an element (optional) for the right side of the [Ior]
     *
     * @return [None] if both [oa] and [ob] are [None]. Otherwise [Some] wrapping
     * an [Ior.Left], [Ior.Right], or [Ior.Both] if [oa], [ob], or both are defined (respectively).
     */

    fun <A, B> fromOptions(oa: Option<A>, ob: Option<B>): Option<Ior<A, B>> = when (oa) {
      is Some -> when (ob) {
        is Some -> Some(Both(oa.t, ob.t))
        is None -> Some<Ior<A, B>>(Left(oa.t))
      }
      is None -> when (ob) {
        is Some -> Some(Right(ob.t))
        is None -> None
      }
    }

    private tailrec fun <L, A, B> Semigroup<L>.loop(v: Ior<L, Either<A, B>>, f: (A) -> IorOf<L, Either<A, B>>): Ior<L, B> = when (v) {
      is Left -> Left(v.value)
      is Right -> when (v.value) {
        is Either.Right -> Right(v.value.b)
        is Either.Left -> loop(f(v.value.a).fix(), f)
      }
      is Both -> when (v.rightValue) {
        is Either.Right -> Both(v.leftValue, v.rightValue.b)
        is Either.Left -> {
          val fnb = f(v.rightValue.a).fix()
          when (fnb) {
            is Left -> Left(v.leftValue.combine(fnb.value))
            is Right -> loop(Both(v.leftValue, fnb.value), f)
            is Both -> loop(Both(v.leftValue.combine(fnb.leftValue), fnb.rightValue), f)
          }
        }
      }
    }

    fun <L, A, B> tailRecM(a: A, f: (A) -> IorOf<L, Either<A, B>>, SL: Semigroup<L>): Ior<L, B> =
      SL.run { loop(f(a).fix(), f) }

    fun <A, B> leftNel(a: A): IorNel<A, B> = Left(NonEmptyList.of(a))

    fun <A, B> bothNel(a: A, b: B): IorNel<A, B> = Both(NonEmptyList.of(a), b)
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

  fun <C> foldLeft(c: C, f: (C, B) -> C): C = fold({ c }, { f(c, it) }, { _, b -> f(c, b) })

  fun <C> foldRight(lc: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fold({ lc }, { f(it, lc) }, { _, b -> f(b, lc) })

  fun <G, C> traverse(GA: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, Ior<A, C>> = GA.run {
    fold({ just(Left(it)) }, { f(it).map { Right<A, C>(it) } }, { _, b -> f(b).map { Right<A, C>(it) } })
  }

  fun <C> bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C = fold({ f(c, it) }, { g(c, it) }, { a, b -> g(f(c, a), b) })

  fun <C> bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fold({ f(it, c) }, { g(it, c) }, { a, b -> f(a, g(b, c)) })

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
  fun <D> map(f: (B) -> D): Ior<A, D> = fold(
    { Left(it) },
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
  fun <C, D> bimap(fa: (A) -> C, fb: (B) -> D): Ior<C, D> = fold(
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
  fun <C> mapLeft(fa: (A) -> C): Ior<C, B> = fold(
    { Left(fa(it)) },
    { Right((it)) },
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
   * Return this [Ior] as [Pair] of [Option]
   *
   * Example:
   * ```
   * Right(12).pad() // Result: Pair(None, Some(12))
   * Left(12).pad()  // Result: Pair(Some(12), None)
   * Both("power", 12).pad()  // Result: Pair(Some("power"), Some(12))
   * ```
   */
  fun pad(): Pair<Option<A>, Option<B>> = fold(
    { Pair(Some(it), None) },
    { Pair(None, Some(it)) },
    { a, b -> Pair(Some(a), Some(b)) }
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
  fun toEither(): Either<A, B> = fold({ Either.Left(it) }, { Either.Right(it) }, { _, b -> Either.Right(b) })

  /**
   * Returns a [Some] containing the [Right] value or `B` if this is [Right] or [Both]
   * and [None] if this is a [Left].
   *
   * Example:
   * ```
   * Right(12).toOption() // Result: Some(12)
   * Left(12).toOption()  // Result: None
   * Both(12, "power").toOption()  // Result: Some("power")
   * ```
   */
  fun toOption(): Option<B> = fold({ None }, { Some(it) }, { _, b -> Some(b) })

  /**
   * Returns a [Some] containing the [Left] value or `A` if this is [Left] or [Both]
   * and [None] if this is a [Right].
   *
   * Example:
   * ```
   * Right(12).toLeftOption() // Result: None
   * Left(12).toLeftOption()  // Result: Some(12)
   * Both(12, "power").toLeftOption()  // Result: Some(12)
   * ```
   */
  fun toLeftOption(): Option<A> =
    fold({ Option.just(it) }, { Option.empty() }, { a, _ -> Option.just(a) })

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
  fun toValidated(): Validated<A, B> = fold({ Invalid(it) }, { Valid(it) }, { _, b -> Valid(b) })

  data class Left<out A, out B> @PublishedApi internal constructor(val value: A) : Ior<A, B>() {
    override val isRight: Boolean get() = false
    override val isLeft: Boolean get() = true
    override val isBoth: Boolean get() = false

    companion object {
      operator fun <A> invoke(a: A): Ior<A, Nothing> = Left(a)
    }
  }

  data class Right<out A, out B> @PublishedApi internal constructor(val value: B) : Ior<A, B>() {
    override val isRight: Boolean get() = true
    override val isLeft: Boolean get() = false
    override val isBoth: Boolean get() = false

    companion object {
      operator fun <B> invoke(b: B): Ior<Nothing, B> = Right(b)
    }
  }

  data class Both<out A, out B>(val leftValue: A, val rightValue: B) : Ior<A, B>() {
    override val isRight: Boolean get() = false
    override val isLeft: Boolean get() = false
    override val isBoth: Boolean get() = true
  }
}

/**
 * Binds the given function across [Ior.Right].
 *
 * @param f The function to bind across [Ior.Right].
 */
fun <A, B, D> Ior<A, B>.flatMap(SG: Semigroup<A>, f: (B) -> Ior<A, D>): Ior<A, D> = fold(
  { Ior.Left(it) },
  f,
  { l, r ->
    with(SG) {
      f(r).fold({
        Ior.Left(l.combine(it))
      }, {
        Ior.Both(l, it)
      }, { ll, rr ->
        Ior.Both(l.combine(ll), rr)
      })
    }
  }
)

fun <A, B, D> Ior<A, B>.ap(SG: Semigroup<A>, ff: IorOf<A, (B) -> D>): Ior<A, D> =
  flatMap(SG) { a -> ff.fix().map { f -> f(a) } }

fun <A, B> Ior<A, B>.getOrElse(default: () -> B): B = fold({ default() }, ::identity, { _, b -> b })

fun <A, B, G> IorOf<A, Kind<G, B>>.sequence(GA: Applicative<G>): Kind<G, Ior<A, B>> =
  fix().traverse(GA, ::identity)

fun <A, B> Pair<A, B>.bothIor(): Ior<A, B> = Ior.Both(this.first, this.second)

fun <A, B> Tuple2<A, B>.bothIor(): Ior<A, B> = Ior.Both(this.a, this.b)

fun <A> A.leftIor(): Ior<A, Nothing> = Ior.Left(this)

fun <A> A.rightIor(): Ior<Nothing, A> = Ior.Right(this)
