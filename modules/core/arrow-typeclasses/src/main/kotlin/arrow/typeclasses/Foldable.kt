package arrow.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.core.Eval.Companion.always

/**
 * Data structures that can be folded to a summary value.
 *
 * Foldable<F> is implemented in terms of two basic methods:
 *
 *  - `foldLeft(fa, b)(f)` eagerly folds `fa` from left-to-right.
 *  - `foldRight(fa, b)(f)` lazily folds `fa` from right-to-left.
 *
 * Beyond these it provides many other useful methods related to folding over F<A> values.
 */
interface Foldable<F> {

    /**
     * Left associative fold on F using the provided function.
     */
    fun <A, B> foldLeft(fa: Kind<F, A>, b: B, f: (B, A) -> B): B

    /**
     * Right associative lazy fold on F using the provided function.
     *
     * This method evaluates lb lazily (in some cases it will not be needed), and returns a lazy value. We are using
     * (A, Eval<B>) => Eval<B> to support laziness in a stack-safe way. Chained computation should be performed via
     * .map and .flatMap.
     *
     * For more detailed information about how this method works see the documentation for Eval<A>.
     */
    fun <A, B> foldRight(fa: Kind<F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B>

    /**
     * Fold implemented using the given Monoid<A> instance.
     */
    fun <A> Monoid<A>.fold(fa: Kind<F, A>): A = foldLeft(fa, empty(), { acc, a -> acc.combine(a) })

    fun <A, B> reduceLeftToOption(fa: Kind<F, A>, f: (A) -> B, g: (B, A) -> B): Option<B> =
            foldLeft(fa, Option.empty()) { option, a ->
                when (option) {
                    is Some<B> -> Some(g(option.t, a))
                    is None -> Some(f(a))
                }
            }

    fun <A, B> reduceRightToOption(fa: Kind<F, A>, f: (A) -> B, g: (A, Eval<B>) -> Eval<B>): Eval<Option<B>> =
            foldRight(fa, Eval.Now(Option.empty())) { a, lb ->
                lb.flatMap { option ->
                    when (option) {
                        is Some<B> -> g(a, Eval.Now(option.t)).map({ Some(it) })
                        is None -> Eval.Later({ Some(f(a)) })
                    }
                }
            }

    /**
     * Reduce the elements of this structure down to a single value by applying the provided aggregation function in
     * a left-associative manner.
     *
     * @return None if the structure is empty, otherwise the result of combining the cumulative left-associative result
     * of the f operation over all of the elements.
     */
    fun <A> reduceLeftOption(fa: Kind<F, A>, f: (A, A) -> A): Option<A> = reduceLeftToOption(fa, { a -> a }, f)

    /**
     * Reduce the elements of this structure down to a single value by applying the provided aggregation function in
     * a right-associative manner.
     *
     * @return None if the structure is empty, otherwise the result of combining the cumulative right-associative
     * result of the f operation over the A elements.
     */
    fun <A> reduceRightOption(fa: Kind<F, A>, f: (A, Eval<A>) -> Eval<A>): Eval<Option<A>> = reduceRightToOption(fa, { a -> a }, f)

    /**
     * Alias for fold.
     */
    fun <A> Monoid<A>.combineAll(fa: Kind<F, A>): A = fold(fa)

    /**
     * Fold implemented by mapping A values into B and then combining them using the given Monoid<B> instance.
     */
    fun <A, B> Monoid<B>.foldMap(fa: Kind<F, A>, f: (A) -> B): B = foldLeft(fa, empty(), { b, a -> b.combine(f(a)) })

    /**
     * Traverse F<A> using Applicative<G>.
     *
     * A typed values will be mapped into G<B> by function f and combined using Applicative#map2.
     *
     * This method is primarily useful when G<_> represents an action or effect, and the specific A aspect of G<A> is
     * not otherwise needed.
     */
    fun <G, A, B> traverse_(GA: Applicative<G>, fa: Kind<F, A>, f: (A) -> Kind<G, B>): Kind<G, Unit> = GA.run {
        foldRight(fa, always { pure(Unit) }, { a, acc -> f(a).map2Eval(acc) { Unit } }).value()
    }

    /**
     * Sequence F<G<A>> using Applicative<G>.
     *
     * Similar to traverse except it operates on F<G<A>> values, so no additional functions are needed.
     */
    fun <G, A> sequence_(ag: Applicative<G>, fga: Kind<F, Kind<G, A>>): Kind<G, Unit> = traverse_(ag, fga, { it })

    /**
     * Find the first element matching the predicate, if one exists.
     */
    fun <A> find(fa: Kind<F, A>, f: (A) -> Boolean): Option<A> =
            foldRight(fa, Eval.now<Option<A>>(None), { a, lb ->
                if (f(a)) Eval.now(Some(a)) else lb
            }).value()

    /**
     * Check whether at least one element satisfies the predicate.
     *
     * If there are no elements, the result is false.
     */
    fun <A> exists(fa: Kind<F, A>, p: (A) -> Boolean): Boolean = foldRight(fa, Eval.False, { a, lb -> if (p(a)) Eval.True else lb }).value()

    /**
     * Check whether all elements satisfy the predicate.
     *
     * If there are no elements, the result is true.
     */
    fun <A> forall(fa: Kind<F, A>, p: (A) -> Boolean): Boolean = foldRight(fa, Eval.True, { a, lb -> if (p(a)) lb else Eval.False }).value()

    /**
     * Returns true if there are no elements. Otherwise false.
     */
    fun <A> isEmpty(fa: Kind<F, A>): Boolean = foldRight(fa, Eval.True, { _, _ -> Eval.False }).value()

    fun <A> nonEmpty(fa: Kind<F, A>): Boolean = !isEmpty(fa)

    /**
     * The size of this Foldable.
     *
     * This is overriden in structures that have more efficient size implementations
     * (e.g. Vector, Set, Map).
     *
     * Note: will not terminate for infinite-sized collections.
     */
    fun <A> Monoid<Long>.size(fa: Kind<F, A>): Long = foldMap(fa) { 1 }

    /**
     * Monadic folding on F by mapping A values to G<B>, combining the B values using the given Monoid<B> instance.
     *
     * Similar to foldM, but using a Monoid<B>.
     */
    fun <G, A, B, TC> TC.foldMapM(fa: Kind<F, A>, f: (A) -> Kind<G, B>): Kind<G, B>
            where TC : Monad<G>, TC : Monoid<B> =
            foldM(fa, empty(), { b, a -> map(f(a)) { b.combine(it) } })

    /**
     * Left associative monadic folding on F.
     *
     * The default implementation of this is based on foldL, and thus will always fold across the entire structure.
     * Certain structures are able to implement this in such a way that folds can be short-circuited (not traverse the
     * entirety of the structure), depending on the G result produced at a given step.
     */
    fun <G, A, B> Monad<G>.foldM(fa: Kind<F, A>, z: B, f: (B, A) -> Kind<G, B>): Kind<G, B> =
            foldLeft(fa, pure(z), { gb, a -> gb.flatMap() { f(it, a) } })

    /**
     * Get the element at the index of the Foldable.
     */
    fun <F, A, TC> TC.get(fa: Kind<F, A>, idx: Long): Option<A>
            where TC : Foldable<F>, TC : Monad<Kind<ForEither, A>> =
            if (idx < 0L)
                None
            else {
                foldM(fa, 0L, { i, a ->
                    if (i == idx) Left(a) else Right(i + 1L)
                }).fix().swap().toOption()
            }

    companion object {
        fun <A, B> iterateRight(it: Iterator<A>, lb: Eval<B>): (f: (A, Eval<B>) -> Eval<B>) -> Eval<B> = { f: (A, Eval<B>) -> Eval<B> ->
            fun loop(): Eval<B> =
                    Eval.defer { if (it.hasNext()) f(it.next(), loop()) else lb }
            loop()
        }
    }
}
