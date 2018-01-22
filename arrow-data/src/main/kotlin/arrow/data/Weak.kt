package arrow.data

import arrow.core.*
import arrow.higherkind
import java.lang.ref.WeakReference

/**
 * Represents an object that **can** stop existing when no references to it are made. Backed by
 * a [WeakReference] instance. In a similar fashion to [Option] this allows
 */
@higherkind
data class Weak<out A>(internal val provider: () -> A?) : WeakKind<A> {

    companion object {

        private val EMPTY: Weak<Nothing> = Weak { null }

        @Suppress("UNCHECKED_CAST")
        fun <B> emptyWeak(): Weak<B> = EMPTY

        operator fun <A> invoke(a: A): Weak<A> {
            val reference = WeakReference(a)
            return Weak { reference.get() }
        }

        tailrec fun <A, B> tailRectM(a: A, f: (A) -> WeakKind<Either<A, B>>): Weak<B> {
            val value: Either<A, B>? = f(a).ev().provider()
            return when (value) {
                null -> emptyWeak()
                is Either.Left -> tailRectM(value.a, f)
                is Either.Right -> value.b.weak()
            }
        }
    }

    fun asOption(): Option<A> = Option.fromNullable(provider())

    inline fun <B> fold(fn: () -> B, f: (A) -> B): B = asOption().fold(fn, f)

    inline fun <B> map(crossinline f: (A) -> B): Weak<B> = fold({ emptyWeak() }, { f(it).weak() })

    inline fun <B> flatMap(crossinline f: (A) -> WeakKind<B>): Weak<B> = fold({ emptyWeak() }, { a -> f(a).ev() })

    /**
     * Returns this Weak as long as the provided predicate confirms the value is to be kept
     *
     * @param p Predicate used for testing
     */
    inline fun filter(crossinline p: (A) -> Boolean): Weak<A> = fold({ emptyWeak() }, { a -> if (p(a)) a.weak() else emptyWeak() })

    fun <B> ap(ff: WeakKind<(A) -> B>): Weak<B> = ff.ev().flatMap { f -> map(f) }.ev()

    fun exists(predicate: Predicate<A>): Boolean = fold({ false }, { predicate(it) })

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = ev().fold({ b }, { f(b, it) })

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = ev().fold({ lb }, { f(it, lb) })

    fun forall(predicate: Predicate<A>): Boolean = fold({ false }, { predicate(it) })

    fun isEmpty(): Boolean = fold({ true }, { false })

    fun nonEmpty(): Boolean = fold({ false }, { true })
}

/**
 * Returns the internal value or an alternative if we've lost it.
 *
 * @param fallback provides a new value if we have lost the current one.
 */
fun <B> Weak<B>.getOrElse(fallback: () -> B): B = fold({ fallback() }, { it })

/**
 * Returns this Weak instance if present or an alternative.
 *
 * @param fallback provides a new value if we have lost the current one.
 */
fun <A, B : A> WeakKind<B>.orElse(fallback: () -> Weak<B>): Weak<B> = ev().provider()?.let { ev() } ?: fallback()

fun <A> A.weak(): Weak<A> = Weak(this)

fun <A> Option<A>.asWeak(): Weak<A> = fold({ Weak.emptyWeak() }, { it.weak() })
