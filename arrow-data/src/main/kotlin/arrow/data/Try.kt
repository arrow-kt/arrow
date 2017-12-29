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
sealed class Try<out A> : TryKind<A> {

    companion object {

        fun <A> pure(a: A): Try<A> = Success(a)

        tailrec fun <A, B> tailRecM(a: A, f: (A) -> TryKind<Either<A, B>>): Try<B> {
            val ev: Try<Either<A, B>> = f(a).ev()
            return when (ev) {
                is Failure -> arrow.monadError<TryHK, Throwable>().raiseError<B>(ev.exception).ev()
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

    }

    @Deprecated(DeprecatedUnsafeAccess, ReplaceWith("getOrElse { ifEmpty }"))
    operator fun invoke() = get()

    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Try<B>> =
            this.ev().fold({ GA.pure(Try.raise(IllegalStateException())) }, { GA.map(f(it), { Try { it } }) })

    fun <B> ap(ff: TryKind<(A) -> B>): Try<B> = ff.ev().flatMap { f -> map(f) }.ev()

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
    inline fun filter(crossinline p: Predicate<A>): Try<A> =
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
                    { Failure(TryException.UnsupportedOperationException("Success")) }
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

    abstract fun isFailure(): Boolean

    abstract fun isSuccess(): Boolean

    @Deprecated(DeprecatedUnsafeAccess, ReplaceWith("fold({ Unit }, f)"))
    fun foreach(f: (A) -> Unit) {
        if (isSuccess()) f(get())
    }

    @Deprecated(DeprecatedUnsafeAccess, ReplaceWith("map { f(it); it }"))
    fun onEach(f: (A) -> Unit): Try<A> = map {
        f(it)
        it
    }

    fun exists(predicate: Predicate<A>): Boolean = fold({ false }, { predicate(it) })

    @Deprecated(DeprecatedUnsafeAccess, ReplaceWith("getOrElse { ifEmpty }"))
    abstract fun get(): A

    @Deprecated(DeprecatedUnsafeAccess, ReplaceWith("map { body(it); it }"))
    fun onSuccess(body: (A) -> Unit): Try<A> {
        foreach(body)
        return this
    }

    @Deprecated(DeprecatedUnsafeAccess, ReplaceWith("fold ({ Try { body(it); it }}, { Try.pure(it) })"))
    fun onFailure(body: (Throwable) -> Unit): Try<A> = when (this) {
        is Success -> this
        is Failure -> {
            body(exception)
            this
        }
    }

    fun toOption(): Option<A> = fold({ None }, { Some(it) })

    fun toEither(): Either<Throwable, A> = fold({ Left(it) }, { Right(it) })

    @Deprecated("arrow.data.Either is already right biased. This function will be removed in future releases", ReplaceWith("toEither()"))
    fun toDisjunction(): Disjunction<Throwable, A> = toEither().toDisjunction()

    /**
     * The `Failure` type represents a computation that result in an exception.
     */
    data class Failure<out A>(val exception: Throwable) : Try<A>() {
        override fun isFailure(): Boolean = true

        override fun isSuccess(): Boolean = false

        override fun get(): A {
            throw exception
        }
    }

    /**
     * The `Success` type represents a computation that return a successfully computed value.
     */
    data class Success<out A>(val value: A) : Try<A>() {
        override fun isFailure(): Boolean = false

        override fun isSuccess(): Boolean = true

        override fun get(): A = value
    }
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

fun <B, A: B> Try<A>.orElse(f: () -> Try<B>): Try<B> = when (this) {
    is Try.Success -> this
    is Try.Failure -> f()
}

/**
 * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
 * This is like `flatMap` for the exception.
 */
fun <B> Try<B>.recoverWith(f: (Throwable) -> Try<B>): Try<B> = fold({ f(it) }, { Success(it) })

@Deprecated(DeprecatedAmbiguity, ReplaceWith("recoverWith(f)"))
fun <A> Try<A>.rescue(f: (Throwable) -> Try<A>): Try<A> = recoverWith(f)

/**
 * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
 * This is like map for the exception.
 */
fun <B> Try<B>.recover(f: (Throwable) -> B): Try<B> = fold({ Success(f(it)) }, { Success(it) })

@Deprecated(DeprecatedAmbiguity, ReplaceWith("recover(f)"))
fun <A> Try<A>.handle(f: (Throwable) -> A): Try<A> = recover(f)

/**
 * Completes this `Try` by applying the function `f` to this if this is of type `Failure`,
 * or conversely, by applying `s` if this is a `Success`.
 */
fun <A, B> Try<A>.transform(s: (A) -> Try<B>, f: (Throwable) -> Try<B>): Try<B> = fold({ f(it) }, { flatMap(s) })

fun <A> (() -> A).try_(): Try<A> = Try(this)

fun <T> Try<Try<T>>.flatten(): Try<T> = flatMap(::identity)