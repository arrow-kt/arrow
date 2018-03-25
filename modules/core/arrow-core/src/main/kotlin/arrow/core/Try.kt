package arrow.core

import arrow.higherkind
import arrow.legacy.Disjunction

typealias Failure<A> = Try.Failure<A>
typealias Success<A> = Try.Success<A>

/**
 * The `Try` type represents a computation that may either result in an exception, or return a
 * successfully computed value.
 *
 * Port of https://github.com/scala/scala/blob/v2.12.1/src/library/scala/util/Try.scala
 */
@higherkind
sealed class Try<out A> : TryOf<A> {

    companion object {

        fun <A> pure(a: A): Try<A> = Success(a)

        tailrec fun <A, B> tailRecM(a: A, f: (A) -> TryOf<Either<A, B>>): Try<B> {
            val ev: Try<Either<A, B>> = f(a).fix()
            return when (ev) {
                is Failure -> Failure<B>(ev.exception).fix()
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

    fun <B> ap(ff: TryOf<(A) -> B>): Try<B> = ff.fix().flatMap { f -> map(f) }.fix()

    /**
     * Returns the given function applied to the value from this `Success` or returns this if this is a `Failure`.
     */
    inline fun <B> flatMap(crossinline f: (A) -> TryOf<B>): Try<B> = fold({ raise(it) }, { f(it).fix() })

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
     */
    inline fun <B> fold(fa: (Throwable) -> B, fb: (A) -> B): B =
            when (this) {
                is Failure -> fa(exception)
                is Success -> fb(value)
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

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = this.fix().fold({ b }, { f(b, it) })

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = this.fix().fold({ lb }, { f(it, lb) })

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

/**
 * Returns the value from this `Success` or the given `default` argument if this is a `Failure`.
 *
 * ''Note:'': This will throw an exception if it is not a success and default throws an exception.
 */
fun <B> TryOf<B>.getOrDefault(default: () -> B): B = fix().fold({ default() }, { it })

/**
 * Returns the value from this `Success` or the given `default` argument if this is a `Failure`.
 *
 * ''Note:'': This will throw an exception if it is not a success and default throws an exception.
 */
fun <B> TryOf<B>.getOrElse(default: (Throwable) -> B): B = fix().fold(default, { it })

fun <B, A: B> TryOf<A>.orElse(f: () -> TryOf<B>): Try<B> = when (this.fix()) {
    is Try.Success -> this.fix()
    is Try.Failure -> f().fix()
}

/**
 * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
 * This is like `flatMap` for the exception.
 */
fun <B> TryOf<B>.recoverWith(f: (Throwable) -> TryOf<B>): Try<B> = fix().fold({ f(it).fix() }, { Success(it) })

@Deprecated(DeprecatedAmbiguity, ReplaceWith("recoverWith(f)"))
fun <A> TryOf<A>.rescue(f: (Throwable) -> TryOf<A>): Try<A> = fix().recoverWith(f)

/**
 * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
 * This is like map for the exception.
 */
fun <B> TryOf<B>.recover(f: (Throwable) -> B): Try<B> = fix().fold({ Success(f(it)) }, { Success(it) })

@Deprecated(DeprecatedAmbiguity, ReplaceWith("recover(f)"))
fun <A> TryOf<A>.handle(f: (Throwable) -> A): Try<A> = fix().recover(f)

/**
 * Completes this `Try` by applying the function `f` to this if this is of type `Failure`,
 * or conversely, by applying `s` if this is a `Success`.
 */
fun <A, B> TryOf<A>.transform(s: (A) -> TryOf<B>, f: (Throwable) -> TryOf<B>): Try<B> = fix().fold({ f(it).fix() }, { fix().flatMap(s) })

fun <A> (() -> A).try_(): Try<A> = Try(this)

fun <T> TryOf<TryOf<T>>.flatten(): Try<T> = fix().flatMap(::identity)