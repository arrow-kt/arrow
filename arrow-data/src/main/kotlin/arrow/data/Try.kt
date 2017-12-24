package arrow

typealias Failure<A> = Try.Failure<A>
typealias Success<A> = Try.Success<A>

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

        fun <A> pure(a: A): Try<A> = Success(a)

        tailrec fun <A, B> tailRecM(a: A, f: (A) -> TryKind<Either<A, B>>): Try<B> {
            val ev: Try<Either<A, B>> = f(a).ev()
            return when (ev) {
                is Failure -> Try.monadError().raiseError(ev.exception)
                is Success -> {
                    val b: Either<A, B> = ev.value
                    when (b) {
                        is Either.Left<A, B> -> tailRecM(b.a, f)
                        is Either.Right<A, B> -> Success(b.b)
                    }
                }
            }

        }

        inline operator fun <A> invoke(f: () -> A): Try<A> =
                try {
                    Success(f())
                } catch (e: Throwable) {
                    Failure(e)
                }

        fun <A> raise(e: Throwable): Try<A> = Failure(e)

        fun <A, B> merge(
                op1: () -> A,
                op2: () -> B): Try<Tuple2<A, B>> =
                applicative().tupled(
                        invoke(op1),
                        invoke(op2)
                ).ev()

        fun <A, B, C> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C): Try<Tuple3<A, B, C>> =
                applicative().tupled(
                        invoke(op1),
                        invoke(op2),
                        invoke(op3)
                ).ev()

        fun <A, B, C, D> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D): Try<Tuple4<A, B, C, D>> =
                applicative().tupled(
                        invoke(op1),
                        invoke(op2),
                        invoke(op3),
                        invoke(op4)
                ).ev()

        fun <A, B, C, D, E> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D,
                op5: () -> E): Try<Tuple5<A, B, C, D, E>> =
                applicative().tupled(
                        invoke(op1),
                        invoke(op2),
                        invoke(op3),
                        invoke(op4),
                        invoke(op5)
                ).ev()

        fun <A, B, C, D, E, F> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D,
                op5: () -> E,
                op6: () -> F): Try<Tuple6<A, B, C, D, E, F>> =
                applicative().tupled(
                        invoke(op1),
                        invoke(op2),
                        invoke(op3),
                        invoke(op4),
                        invoke(op5),
                        invoke(op6)
                ).ev()

        fun <A, B, C, D, E, F, G> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D,
                op5: () -> E,
                op6: () -> F,
                op7: () -> G): Try<Tuple7<A, B, C, D, E, F, G>> =
                applicative().tupled(
                        invoke(op1),
                        invoke(op2),
                        invoke(op3),
                        invoke(op4),
                        invoke(op5),
                        invoke(op6),
                        invoke(op7)
                ).ev()

        fun <A, B, C, D, E, F, G, H> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D,
                op5: () -> E,
                op6: () -> F,
                op7: () -> G,
                op8: () -> H): Try<Tuple8<A, B, C, D, E, F, G, H>> =
                applicative().tupled(
                        invoke(op1),
                        invoke(op2),
                        invoke(op3),
                        invoke(op4),
                        invoke(op5),
                        invoke(op6),
                        invoke(op7),
                        invoke(op8)
                ).ev()

        fun <A, B, C, D, E, F, G, H, I> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D,
                op5: () -> E,
                op6: () -> F,
                op7: () -> G,
                op8: () -> H,
                op9: () -> I): Try<Tuple9<A, B, C, D, E, F, G, H, I>> =
                applicative().tupled(
                        invoke(op1),
                        invoke(op2),
                        invoke(op3),
                        invoke(op4),
                        invoke(op5),
                        invoke(op6),
                        invoke(op7),
                        invoke(op8),
                        invoke(op9)
                ).ev()

        fun <A, B, C, D, E, F, G, H, I, J> merge(
                op1: () -> A,
                op2: () -> B,
                op3: () -> C,
                op4: () -> D,
                op5: () -> E,
                op6: () -> F,
                op7: () -> G,
                op8: () -> H,
                op9: () -> I,
                op10: () -> J): Try<Tuple10<A, B, C, D, E, F, G, H, I, J>> =
                applicative().tupled(
                        invoke(op1),
                        invoke(op2),
                        invoke(op3),
                        invoke(op4),
                        invoke(op5),
                        invoke(op6),
                        invoke(op7),
                        invoke(op8),
                        invoke(op9),
                        invoke(op10)
                ).ev()
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
    inline fun <B> fold(fa: (Throwable) -> B, fb: (A) -> B): B =
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

fun <A, B> Try<A>.foldLeft(b: B, f: (B, A) -> B): B = this.ev().fold({ b }, { f(b, it) })

fun <A, B> Try<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = this.ev().fold({ lb }, { f(it, lb) })

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
fun <B> Try<B>.recoverWith(f: (Throwable) -> Try<B>): Try<B> = fold({ f(it) }, { Success(it) })

/**
 * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
 * This is like map for the exception.
 */
fun <B> Try<B>.recover(f: (Throwable) -> B): Try<B> = fold({ Success(f(it)) }, { Success(it) })

/**
 * Completes this `Try` by applying the function `f` to this if this is of type `Failure`,
 * or conversely, by applying `s` if this is a `Success`.
 */
fun <A, B> Try<A>.transform(s: (A) -> Try<B>, f: (Throwable) -> Try<B>): Try<B> = fold({ f(it) }, { flatMap(s) })

fun <A> (() -> A).try_(): Try<A> = Try(this)