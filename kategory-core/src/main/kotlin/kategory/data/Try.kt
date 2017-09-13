package kategory

/**
 * The `Try` type represents a computation that may either result in an exception, or return a
 * successfully computed value.
 *
 * Port of https://github.com/scala/scala/blob/v2.12.1/src/library/scala/util/Try.scala
 */
@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class,
        Foldable::class,
        Traverse::class)
sealed class Try<out A> : TryKind<A> {

    companion object {

        fun <A> pure(a: A): Try<A> = Try.Success(a)

        fun <A, B> tailRecM(a: A, f: (A) -> TryKind<Either<A, B>>): Try<B> =
                f(a).ev().fold({ Try.monadError().raiseError(it) }, { either -> either.fold({ tailRecM(it, f) }, { Try.Success(it) }) })

        inline operator fun <A> invoke(f: () -> A): Try<A> =
                try {
                    Success(f())
                } catch (e: Throwable) {
                    Failure(e)
                }

        fun <A> raise(e: Throwable): Try<A> = Failure(e)

        fun monadError(): TryMonadErrorInstance =
                TryMonadErrorInstanceImplicits.instance()

    }

    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Try<B>> =
            this.ev().fold({ GA.pure(Try.raise(IllegalStateException())) }, { GA.map(f(it), { Try { it } }) })

    fun <B> ap(ff: TryKind<(A) -> B>): Try<B> = ff.flatMap { f -> map(f) }.ev()

    /**
     * Returns the given function applied to the value from this `Success` or returns this if this is a `Failure`.
     */
    inline fun <B> flatMap(crossinline f: (A) -> TryKind<B>): Try<B> = fold({ Try.raise(it) }, { f(it).ev() })

    /**
     * Maps the given function to the value from this `Success` or returns this if this is a `Failure`.
     */
    inline fun <B> map(crossinline f: (A) -> B): Try<B> = fold({ Failure(it) }, { Success(f(it)) })

    /**
     * Converts this to a `Failure` if the predicate is not satisfied.
     */
    inline fun filter(crossinline p: (A) -> Boolean): Try<A> =
            fold(
                    { Failure(it) },
                    { if (p(it)) Success(it) else Failure(TryException.PredicateException("Predicate does not hold for $it")) }
            )

    /**
     * Inverts this `Try`. If this is a `Failure`, returns its exception wrapped in a `Success`.
     * If this is a `Success`, returns a `Failure` containing an `UnsupportedOperationException`.
     */
    fun failed(): Try<Throwable> =
            fold(
                    { Success(it) },
                    { Failure(TryException.UnsupportedOperationException("Success.failed")) }
            )

    /**
     * Applies `fa` if this is a `Failure` or `fb` if this is a `Success`.
     * If `fb` is initially applied and throws an exception,
     * then `fa` is applied with this exception.
     */
    fun <B> fold(fa: (Throwable) -> B, fb: (A) -> B): B =
            when (this) {
                is Failure -> fa(exception)
                is Success -> try {
                    fb(value)
                } catch (e: Throwable) {
                    fa(e)
                }
            }

    /**
     * The `Failure` type represents a computation that result in an exception.
     */
    data class Failure<out A>(val exception: Throwable) : Try<A>()

    /**
     * The `Success` type represents a computation that return a successfully computed value.
     */
    data class Success<out A>(val value: A) : Try<A>()
}

sealed class TryException(override val message: String) : kotlin.Exception(message) {
    data class PredicateException(override val message: String) : TryException(message)
    data class UnsupportedOperationException(override val message: String) : TryException(message)
}

fun <A, B> Try<A>.foldL(b: B, f: (B, A) -> B): B = this.ev().fold({ b }, { f(b, it) })

fun <A, B> Try<A>.foldR(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = this.ev().fold({ lb }, { f(it, lb) })

/**
 * Returns the value from this `Success` or the given `default` argument if this is a `Failure`.
 *
 * ''Note:'': This will throw an exception if it is not a success and default throws an exception.
 */
fun <B> Try<B>.getOrElse(default: () -> B): B = fold({ default() }, { it })

/**
 * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
 * This is like `flatMap` for the exception.
 */
fun <B> Try<B>.recoverWith(f: (Throwable) -> Try<B>): Try<B> = fold({ f(it) }, { Try.Success(it) })

/**
 * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
 * This is like map for the exception.
 */
fun <B> Try<B>.recover(f: (Throwable) -> B): Try<B> = fold({ Try.Success(f(it)) }, { Try.Success(it) })

/**
 * Completes this `Try` by applying the function `f` to this if this is of type `Failure`,
 * or conversely, by applying `s` if this is a `Success`.
 */
fun <B> Try<B>.transform(s: (B) -> Try<B>, f: (Throwable) -> Try<B>): Try<B> = fold({ f(it) }, { flatMap(s) })

fun <A> (() -> A).try_(): Try<A> = Try(this)