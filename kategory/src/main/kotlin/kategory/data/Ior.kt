package kategory

import kategory.Either.Left
import kategory.Either.Right

typealias IorF<L> = HK<IorHK, L>

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
 *
 * El primogenito de @ffgiraldez
 */

@higherkind sealed class Ior<out A, out B> : IorKind<A, B> {

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
     * Both("venus", "fly-trap").isRight // Result: false
     * ```
     */
    abstract val isLeft: Boolean

    /**
     * Returns `true` if this is a [Left], `false` otherwise.
     *
     * Example:
     * ```
     * Left("tulip").isLeft           // Result: false
     * Right("venus fly-trap").isLeft // Result: false
     * Both("venus", "fly-trap").isRight // Result: true
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
         * @return [Option.None] if both [oa] and [ob] are [Option.None]. Otherwise [Option.Some] wrapping
         * an [Ior.Left], [Ior.Right], or [Ior.Both] if [oa], [ob], or both are defined (respectively).
         */

        @JvmStatic fun <A, B> fromOptions(oa: Option<A>, ob: Option<B>): Option<Ior<A, B>> = when (oa) {
            is Option.Some -> when (ob) {
                is Option.Some -> Option.Some(Both(oa.value, ob.value))
                is Option.None -> Option.Some(Left(oa.value))
            }
            is Option.None -> when (ob) {
                is Option.Some -> Option.Some(Right(ob.value))
                is Option.None -> Option.None
            }
        }

        inline fun <reified L> instances(SL: Semigroup<L> = semigroup<L>()): IorInstances<L> = object : IorInstances<L> {
            override fun SL(): Semigroup<L> = SL
        }

        inline fun <reified L> functor(SL: Semigroup<L> = semigroup<L>()): Functor<IorF<L>> = instances(SL)

        inline fun <reified L> applicative(SL: Semigroup<L> = semigroup<L>()): Applicative<IorF<L>> = instances(SL)

        inline fun <reified L> monad(SL: Semigroup<L> = semigroup<L>()): Monad<IorF<L>> = instances(SL)

        fun <L> foldable(): Foldable<HK<IorHK, L>> = object : IorTraverse<L> {}

        fun <L> traverse(): Traverse<HK<IorHK, L>> = object : IorTraverse<L> {}
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
    inline fun <C, D> bimap(fa: (A) -> C, fb: (B) -> D) = fold(
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
     * Right(12).pad() // Result: Pair(Option.None, Option.Some(12))
     * Left(12).pad()  // Result: Pair(Option.Some(12), Option.None)
     * Both("power", 12).pad()  // Result: Pair(Option.Some("power"), Option.Some(12))
     * ```
     */
    fun pad(): Pair<Option<A>, Option<B>> = fold(
            { Pair(Option.Some(it), Option.None) },
            { Pair(Option.None, Option.Some(it)) },
            { a, b -> Pair(Option.Some(a), Option.Some(b)) }
    )

    /**
     * Returns a [Either.Right] containing the [Right] value or `B` if this is [Both]
     * and [Either.Left] if this is a [Left].
     *
     * Example:
     * ```
     * Right(12).toEither() // Result: Either.Right(12)
     * Left(12).toEither()  // Result: Either.Left(12)
     * Both("power", 12).toEither()  // Result: Either.Righ(12)
     * ```
     */
    fun toEither(): Either<A, B> = fold({ Either.Left(it) }, { Either.Right(it) }, { _, b -> Either.Right(b) })

    /**
     * Returns a [Option.Some] containing the [Right] value or `B` if this is [Both]
     * and [Option.None] if this is a [Left].
     *
     * Example:
     * ```
     * Right(12).toOption() // Result: Some(12)
     * Left(12).toOption()  // Result: None
     * Both(12, "power").toOption()  // Result: Some("power")
     * ```
     */
    fun toOption(): Option<B> = fold({ Option.None }, { Option.Some(it) }, { _, b -> Option.Some(b) })

    data class Left<out A>(val value: A) : Ior<A, Nothing>() {
        override val isRight: Boolean = false
        override val isLeft: Boolean = true
        override val isBoth: Boolean = false
    }

    data class Right<out B>(val value: B) : Ior<Nothing, B>() {
        override val isRight: Boolean = true
        override val isLeft: Boolean = false
        override val isBoth: Boolean = false
    }

    data class Both<out A, out B>(val leftValue: A, val rightValue: B) : Ior<A, B>() {
        override val isRight: Boolean = false
        override val isLeft: Boolean = false
        override val isBoth: Boolean = true
    }
}

/**
 * Binds the given function across [Ior.Right].
 *
 * @param f The function to bind across [Ior.Right].
 */
inline fun <A, B, D> Ior<A, B>.flatMap(SA: Semigroup<A>, crossinline f: (B) -> Ior<A, D>): Ior<A, D> = when (this) {
    is Ior.Left -> Ior.Left(value)
    is Ior.Right -> f(value)
    is Ior.Both -> {
        val fm = f(rightValue)
        when (fm) {
            is Ior.Left -> Ior.Left(SA.combine(leftValue, fm.value))
            is Ior.Right -> Ior.Right(fm.value)
            is Ior.Both -> Ior.Both(SA.combine(leftValue, fm.leftValue), fm.rightValue)
        }
    }
}

inline fun <A, B> Ior<A, B>.getOrElse(crossinline default: () -> B): B = fold({ default() }, { it }, { _, b -> b })

fun <A, B> A.rightIor(): Ior<B, A> =
        Ior.Right(this)

fun <A, B> A.leftIor(): Ior<A, B> =
        Ior.Left(this)

fun <A, B> Pair<A, B>.bothIor(): Ior<A, B> =
        Ior.Both(this.first, this.second)

fun <A, B> Tuple2<A, B>.bothIor(): Ior<A, B> =
        Ior.Both(this.a, this.b)