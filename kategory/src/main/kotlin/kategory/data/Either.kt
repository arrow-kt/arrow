package kategory

typealias EitherKind<A, B> = HK2<Either.F, A, B>
typealias EitherF<L> = HK<Either.F, L>

fun <A, B> EitherKind<A, B>.ev(): Either<A, B> =
        this as Either<A, B>

/**
 * Port of https://github.com/scala/scala/blob/v2.12.1/src/library/scala/util/Either.scala
 *
 * Represents a value of one of two possible types (a disjoint union.)
 * An instance of Either is either an instance of [Left] or [Right].
 */
sealed class Either<out A, out B> : EitherKind<A, B> {

    class F private constructor()

    /**
     * Returns `true` if this is a [Right], `false` otherwise.
     *
     * Example:
     * ```
     * Left("tulip").isRight           // Result: false
     * Right("venus fly-trap").isRight // Result: true
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
     * ```
     */
    abstract val isLeft: Boolean

    /**
     * Applies `fa` if this is a [Left] or `fb` if this is a [Right].
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
     * @param fa the function to apply if this is a [Left]
     * @param fb the function to apply if this is a [Right]
     * @return the results of applying the function
     */
    inline fun <C> fold(fa: (A) -> C, fb: (B) -> C): C = when (this) {
        is Right -> fb(b)
        is Left -> fa(a)
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
    fun swap(): Either<B, A> =
            fold({ Right(it) }, { Left(it) })

    /**
     * The given function is applied if this is a `Right`.
     *
     * Example:
     * ```
     * Right(12).map { "flower" } // Result: Right("flower")
     * Left(12).map { "flower" }  // Result: Left(12)
     * ```
     */
    inline fun <C> map(f: (B) -> C): Either<A, C> =
            fold({ Left(it) }, { Right(f(it)) })

    /**
     * Map over Left and Right of this Either
     */
    inline fun <C, D> bimap(fa: (A) -> C, fb: (B) -> D): Either<C, D> =
            fold({ Left(fa(it)) }, { Right(fb(it)) })

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
    inline fun exists(predicate: (B) -> Boolean): Boolean =
            fold({ false }, { predicate(it) })

    /**
     * Returns a [Option.Some] containing the [Right] value
     * if it exists or a [Option.None] if this is a [Left].
     *
     * Example:
     * ```
     * Right(12).toOption() // Result: Some(12)
     * Left(12).toOption()  // Result: None
     * ```
     */
    fun toOption(): Option<B> =
            fold({ Option.None }, { Option.Some(it) })

    /**
     * The left side of the disjoint union, as opposed to the [Right] side.
     */
    data class Left<out A>(val a: A) : Either<A, Nothing>() {
        override val isLeft = true
        override val isRight = false
    }

    /**
     * The right side of the disjoint union, as opposed to the [Left] side.
     */
    data class Right<out B>(val b: B) : Either<Nothing, B>() {
        override val isLeft = false
        override val isRight = true
    }

    companion object {

        fun <L> instances(): EitherInstances<L> = object : EitherInstances<L> {}

        fun <L> functor(): Functor<EitherF<L>> = instances()

        fun <L> applicative(): Applicative<EitherF<L>> = instances()

        fun <L> monad(): Monad<EitherF<L>> = instances()

        fun <L> foldable(): Foldable<EitherF<L>> = instances()

        fun <L> traverse(): Traverse<EitherF<L>> = instances()

        fun <L> monadError(): MonadError<EitherF<L>, L> = instances()

    }
}

/**
 * Binds the given function across [Either.Right].
 *
 * @param f The function to bind across [Either.Right].
 */
inline fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> =
        fold({ Either.Left(it) }, { f(it) })

/**
 * Returns the value from this [Either.Right] or the given argument if this is a [Either.Left].
 *
 * Example:
 * ```
 * Right(12).getOrElse(17) // Result: 12
 * Left(12).getOrElse(17)  // Result: 17
 * ```
 */
inline fun <B> Either<*, B>.getOrElse(default: () -> B): B =
        fold({ default() }, { it })

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
inline fun <A, B> Either<A, B>.filterOrElse(predicate: (B) -> Boolean, default: () -> A): Either<A, B> =
        fold({ Either.Left(it) }, { if (predicate(it)) Either.Right(it) else Either.Left(default()) })

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
fun <A, B> Either<A, B>.contains(elem: B): Boolean =
        fold({ false }, { it == elem })
