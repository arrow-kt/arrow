package kategory

/**
 * Data structures that can be reduced to a summary value.
 *
 * Reducible is like a non-empty Foldable. In addition to the fold methods it provides reduce
 * methods which do not require an initial value.
 *
 * In addition to the methods needed by `Foldable`, `Reducible` is implemented in terms of two methods:
 *
 *  - reduceLeftTo(fa)(f)(g) eagerly reduces with an additional mapping function
 *  - reduceRightTo(fa)(f)(g) lazily reduces with an additional mapping function
 */
interface Reducible<in F> : Foldable<F>, Typeclass {

    /**
     * Left-associative reduction on F using the function f.
     *
     * Implementations should override this method when possible.
     */
    fun <A> reduceLeft(fa: HK<F, A>, f: (Tuple2<A, A>) -> A): A =
            reduceLeftTo(fa, { a -> a }, f)

    /**
     * Right-associative reduction on F using the function f.
     */
    fun <A> reduceRight(fa: HK<F, A>, f: (Tuple2<A, Eval<A>>) -> Eval<A>): Eval<A> =
            reduceRightTo(fa, { a -> a }, f)

    /*companion object {
        fun <A, B> iterateRight(it: Iterator<A>, lb: Eval<B>): (f: (A, Eval<B>) -> Eval<B>) -> Eval<B> = { f: (A, Eval<B>) -> Eval<B> ->
            fun loop(): Eval<B> =
                    Eval.defer { if (it.hasNext()) f(it.next(), loop()) else lb }
            loop()
        }
    }*/
}

/**
 * Reduce a F<A> value using the given Semigroup<A>.
 */
inline fun <F, reified A> Reducible<F>.reduce(fa: HK<F, A>, SA: Semigroup<A> = semigroup()): A =
        reduceLeft(fa, { (a, b) -> SA.combine(a, b) })

/**
 * Reduce a F<G<A>> value using SemigroupK<G>, a universal semigroup for G<_>.
 *
 * This method is a generalization of reduce.
 */
inline fun <F, reified G, A> Reducible<F>.reduceK(fga: HK<F, HK<G, A>>, SGKG: SemigroupK<G>): HK<G, A> =
        reduce(fga, SGKG.algebra())
