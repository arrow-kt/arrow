package arrow.data

import java.lang.ref.WeakReference
import arrow.*
import arrow.core.*

/**
 * Represents an object that can stop existing when no references to it exists. Backed by
 * a [WeakReference] instance. In a similar fashion to [Option] this allows
 */
@higherkind
data class Weak<out A>(val provider: () -> A?) : WeakKind<A> {

    companion object {

        private val EMPTY: Weak<Nothing> = Weak { null }

        @Suppress("UNCHECKED_CAST")
        fun <B> emptyWeak(): Weak<B> = EMPTY

        operator fun <A> invoke(a: A): Weak<A> {
            val reference = WeakReference(a)
            return Weak { reference.get() }
        }
    }

    inline fun <B> fold(fn: () -> B, f: (A) -> B): B = provider().let {
        when (it) {
            null -> fn()
            else -> f(it)
        }
    }

    inline fun <B> map(crossinline f: (A) -> B): Weak<B> = fold({ emptyWeak() }, { f(it).weak() })

    inline fun <B> flatMap(crossinline f: (A) -> WeakKind<B>): Weak<B> = fold({ emptyWeak() }, { a -> f(a).ev() })

    /**
     * Returns this Weak as long as the provided predicate confirms the value is to be kept
     *
     * @param p Predicate used for testing
     */
    inline fun filter(crossinline p: (A) -> Boolean): Weak<A> = fold({ emptyWeak() }, { a -> if (p(a)) a.weak() else emptyWeak() })

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
