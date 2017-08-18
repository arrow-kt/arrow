package kategory

import kategory.Eval.Companion.always

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
interface Foldable<in F> : Typeclass {

    /**
     * Left associative fold on F using the provided function.
     */
    fun <A, B> foldL(fa: HK<F, A>, b: B, f: (B, A) -> B): B

    /**
     * Right associative lazy fold on F using the provided function.
     *
     * This method evaluates lb lazily (in some cases it will not be needed), and returns a lazy value. We are using
     * (A, Eval<B>) => Eval<B> to support laziness in a stack-safe way. Chained computation should be performed via
     * .map and .flatMap.
     *
     * For more detailed information about how this method works see the documentation for Eval<A>.
     */
    fun <A, B> foldR(fa: HK<F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B>

    /**
     * Fold implemented using the given Monoid<A> instance.
     */
    fun <A> fold(ma: Monoid<A>, fa: HK<F, A>): A = foldL(fa, ma.empty(), { acc, a -> ma.combine(acc, a) })

    fun <A, B> reduceLeftToOption(fa: HK<F, A>, f: (A) -> B, g: (B, A) -> B): Option<B> =
            foldL(fa, Option.empty()) { option, a -> when (option) {
                is Option.Some<B> -> Option.Some(g(option.value, a))
                is Option.None -> Option.Some(f(a))
            }}

    fun <A, B> reduceRightToOption(fa: HK<F, A>, f: (A) -> B, g: (A, Eval<B>) -> Eval<B>): Eval<Option<B>> =
    foldR(fa, Eval.Now(Option.empty[B])) { (a, lb) =>
        lb.flatMap {
            case Some(b) => g(a, Now(b)).map(Some(_))
            case None => Later(Some(f(a)))
        }
    }*/

    /**
     * Alias for fold.
     */
    fun <A> combineAll(m: Monoid<A>, fa: HK<F, A>): A = fold(m, fa)

    /**
     * Fold implemented by mapping A values into B and then combining them using the given Monoid<B> instance.
     */
    fun <A, B> foldMap(mb: Monoid<B>, fa: HK<F, A>, f: (A) -> B): B = foldL(fa, mb.empty(), { b, a -> mb.combine(b, f(a)) })

    /**
     * Traverse F<A> using Applicative<G>.
     *
     * A typed values will be mapped into G<B> by function f and combined using Applicative#map2.
     *
     * This method is primarily useful when G<_> represents an action or effect, and the specific A aspect of G<A> is
     * not otherwise needed.
     */
    fun <G, A, B> traverse_(ag: Applicative<G>, fa: HK<F, A>, f: (A) -> HK<G, B>): HK<G, Unit> = foldR(fa, always { ag.pure(Unit) }, { a, acc -> ag.map2Eval(f(a), acc) { Unit } }).value()

    /**
     * Sequence F<G<A>> using Applicative<G>.
     *
     * Similar to traverse except it operates on F<G<A>> values, so no additional functions are needed.
     */
    fun <G, A> sequence_(ag: Applicative<G>, fga: HK<F, HK<G, A>>): HK<G, Unit> = traverse_(ag, fga, { it })

    /**
     * Find the first element matching the predicate, if one exists.
     */
    fun <A> find(fa: HK<F, A>, f: (A) -> Boolean): Option<A> =
            foldR(fa, Eval.now<Option<A>>(Option.None), { a, lb ->
                if (f(a)) Eval.now(Option.Some(a)) else lb
            }).value()

    /**
     * Check whether at least one element satisfies the predicate.
     *
     * If there are no elements, the result is false.
     */
    fun <A> exists(fa: HK<F, A>, p: (A) -> Boolean): Boolean = foldR(fa, Eval.False, { a, lb -> if (p(a)) Eval.True else lb }).value()

    /**
     * Check whether all elements satisfy the predicate.
     *
     * If there are no elements, the result is true.
     */
    fun <A> forall(fa: HK<F, A>, p: (A) -> Boolean): Boolean = foldR(fa, Eval.True, { a, lb -> if (p(a)) lb else Eval.False }).value()

    /**
     * Returns true if there are no elements. Otherwise false.
     */
    fun <A> isEmpty(fa: HK<F, A>): Boolean = foldR(fa, Eval.True, { _, _ -> Eval.False }).value()

    fun <A> nonEmpty(fa: HK<F, A>): Boolean = !isEmpty(fa)

    companion object {
        fun <A, B> iterateRight(it: Iterator<A>, lb: Eval<B>): (f: (A, Eval<B>) -> Eval<B>) -> Eval<B> = {
            f: (A, Eval<B>) -> Eval<B> ->
            fun loop(): Eval<B> =
                    Eval.defer { if (it.hasNext()) f(it.next(), loop()) else lb }
            loop()
        }
    }
}

/**
 * Monadic folding on F by mapping A values to G<B>, combining the B values using the given Monoid<B> instance.
 *
 * Similar to foldM, but using a Monoid<B>.
 */
inline fun <F, reified G, A, reified B> Foldable<F>.foldMapM(fa: HK<F, A>, noinline f: (A) -> HK<G, B>, MG: Monad<G> = monad(), bb: Monoid<B> = monoid()): HK<G, B> = foldM(fa, bb.empty(), { b, a -> MG.map(f(a)) { bb.combine(b, it) } }, MG)

/**
 * Left associative monadic folding on F.
 *
 * The default implementation of this is based on foldL, and thus will always fold across the entire structure.
 * Certain structures are able to implement this in such a way that folds can be short-circuited (not traverse the
 * entirety of the structure), depending on the G result produced at a given step.
 */
inline fun <F, reified G, A, B> Foldable<F>.foldM(fa: HK<F, A>, z: B, crossinline f: (B, A) -> HK<G, B>, MG: Monad<G> = monad()): HK<G, B> = foldL(fa, MG.pure(z), { gb, a -> MG.flatMap(gb) { f(it, a) } })

inline fun <reified F> foldable(): Foldable<F> = instance(InstanceParametrizedType(Foldable::class.java, listOf(F::class.java)))

inline fun <reified F, A, B> HK<F, A>.foldL(FT: Foldable<F> = foldable(), b: B, noinline f: (B, A) -> B): B = FT.foldL(this, b, f)

inline fun <reified F, A, B> HK<F, A>.foldR(FT: Foldable<F> = foldable(), b: Eval<B>, noinline f: (A, Eval<B>) -> Eval<B>): Eval<B> = FT.foldR(this, b, f)

inline fun <reified F, reified A> HK<F, A>.fold(FT: Foldable<F> = foldable(), MA: Monoid<A> = monoid()): A = FT.fold(MA, this)

inline fun <reified F, reified A> HK<F, A>.combineAll(FT: Foldable<F> = foldable(), MA: Monoid<A> = monoid()): A = FT.combineAll(MA, this)

inline fun <reified F, A, reified B> HK<F, A>.foldMap(FT: Foldable<F> = foldable(), MB: Monoid<B> = monoid(), noinline f: (A) -> B): B = FT.foldMap(MB, this, f)

inline fun <reified F, reified G, A, B> HK<F, A>.traverse_(FT: Foldable<F> = foldable(), AG: Applicative<G> = applicative(), noinline f: (A) -> HK<G, B>): HK<G, Unit> = FT.traverse_(AG, this, f)

inline fun <reified F, reified G, A> HK<F, HK<G, A>>.sequence_(FT: Foldable<F> = foldable(), AG: Applicative<G> = applicative()): HK<G, Unit> = FT.sequence_(AG, this)

inline fun <reified F, A> HK<F, A>.find(FT: Foldable<F> = foldable(), noinline f: (A) -> Boolean): Option<A> = FT.find(this, f)

inline fun <reified F, A> HK<F, A>.exists(FT: Foldable<F> = foldable(), noinline f: (A) -> Boolean): Boolean = FT.exists(this, f)

inline fun <reified F, A> HK<F, A>.forall(FT: Foldable<F> = foldable(), noinline f: (A) -> Boolean): Boolean = FT.forall(this, f)

inline fun <reified F, A> HK<F, A>.isEmpty(FT: Foldable<F> = foldable()): Boolean = FT.isEmpty(this)

inline fun <reified F, A> HK<F, A>.nonEmpty(FT: Foldable<F> = foldable()): Boolean = FT.nonEmpty(this)

fun <A, B> Iterator<A>.iterateRight(lb: Eval<B>): (f: (A, Eval<B>) -> Eval<B>) -> Eval<B> = Foldable.iterateRight(this, lb)
