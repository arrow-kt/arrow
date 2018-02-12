package arrow.weak

import arrow.core.*
import arrow.higherkind
import arrow.weak.internal.WeakRef

/**
 * Represents an object that **can** stop existing (recycled by the Garbage Collector) when is no longer referenced.
 * Backed by a [WeakReference] instance. In a similar fashion to [Option] this forces the consumer to check whether the
 * object is still valid.
 *
 * We have several ways to create a new [Weak] instance:
 *
 *  - `Weak(object)`: Creates an instance with a weak reference to the provided object
 *  - `object.weak()`: An alias for the previous method
 *  - `Weak.emptyWeak()`: Represents an instance that will never exist. Used for operations and tests.
 *
 *  At the time of usage we can either apply functional operators or unwrap it in two forms:
 *
 *   - `weakObject.eval`: [Eval] property that provides an [Option] with the result or lack thereof.
 *   - `weakObject.option()`: to get the [Option] instance directly.
 *
 *
 *   Basically, this is a Schrodinger's cat box.
 */
@higherkind
class Weak<out A> private constructor(val eval: Eval<Option<A>>) : WeakOf<A> {

    companion object {

        private val EMPTY: Weak<Nothing> = Weak(Eval.now(Option.empty()))

        @Suppress("UNCHECKED_CAST")
        fun <B> emptyWeak(): Weak<B> = EMPTY

        /**
         * Main public factory method
         */
        operator fun <A> invoke(a: A): Weak<A> {
            val reference = WeakRef(a)
            return Weak(Eval.always { Option.fromNullable(reference.get()) })// { reference.get() }
        }

        tailrec fun <A, B> tailRectM(a: A, f: (A) -> WeakOf<Either<A, B>>): Weak<B> {
            val option: Option<Either<A, B>> = f(a).fix().option()
            return when (option) {
                is None -> emptyWeak()
                is Some -> {
                    val either = option.t
                    when (either) {
                        is Either.Left -> tailRectM(either.a, f)
                        is Either.Right -> either.b.weak()
                    }
                }
            }
        }
    }

    /**
     * Evaluates the contents of this weak and returns [Some] is the subject is in memory or [None] otherwise
     */
    fun option(): Option<A> = eval.value()
}

fun <A, B> WeakOf<A>.ap(ff: WeakOf<(A) -> B>): Weak<B> = ff.fix().flatMap(::map).fix()

/**
 * If subject is not in memory this will return the result of [f], otherwise it returns the computed result from [fn]
 */
inline fun <A, B> WeakOf<A>.fold(fn: () -> B, f: (A) -> B): B = fix().option().fold(fn, f)

/**
 * Returns a new [Weak] instance from the result of [f] if the current subject is present or empty [Weak] otherwise.
 */
inline fun <A, B> WeakOf<A>.map(crossinline f: (A) -> B): Weak<B> = fold({ Weak.emptyWeak() }, { f(it).weak() })

/**
 * Similar to [map] but [f] is expected to return a [Weak] instance instead of a subject object.
 */
inline fun <A, B> WeakOf<A>.flatMap(crossinline f: (A) -> WeakOf<B>): Weak<B> = fold({ Weak.emptyWeak() }, { f(it).fix() })

/**
 * Returns this Weak as long as the provided predicate confirms the value is to be kept
 *
 * @param predicate Predicate used for testing
 */
inline fun <A> WeakOf<A>.filter(predicate: Predicate<A>): Weak<A> = fix().fold({ Weak.emptyWeak() }, { a -> if (predicate(a)) a.weak() else Weak.emptyWeak() })

/**
 * Returns true if the subject is in memory and passes the provided predicate
 */
inline fun <A> WeakOf<A>.exists(predicate: Predicate<A>): Boolean = fix().fold({ false }, predicate)

inline fun <A, B> WeakOf<A>.foldLeft(b: B, f: (B, A) -> B): B = fix().fold({ b }, { f(b, it) })

inline fun <A, B> WeakOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fix().fold({ lb }, { f(it, lb) })

/**
 * Returns true if the subject is available and passes the provided predicate
 */
inline fun <A> WeakOf<A>.forall(predicate: Predicate<A>): Boolean = fix().fold({ false }, predicate)

/**
 * Returns true if we have lost the reference to the subject
 */
fun <A> WeakOf<A>.isEmpty(): Boolean = fix().option().isEmpty()

/**
 * Returns true if we still have the reference to the subject
 */
fun <A> WeakOf<A>.nonEmpty(): Boolean = fix().option().nonEmpty()

/**
 * Returns the internal value or an alternative if we've lost it.
 *
 * @param fallback provides a new value if we have lost the current one.
 */
inline fun <B> WeakOf<B>.getOrElse(fallback: () -> B): B = fix().fold(fallback, { it })

/**
 * Returns this Weak instance if present or an alternative.
 *
 * @param fallback provides a new value if we have lost the current one.
 */
inline fun <A, B : A> WeakOf<B>.orElse(fallback: () -> Weak<B>): Weak<B> = fix().fold(fallback, { it.weak() })

/**
 * Creates a new Weak instance. Alias of [Weak.invoke].
 */
fun <A> A.weak(): Weak<A> = Weak(this)
