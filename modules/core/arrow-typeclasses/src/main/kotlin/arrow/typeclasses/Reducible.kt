package arrow.typeclasses

import arrow.Kind
import arrow.core.*

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
interface Reducible<F> : Foldable<F> {

    /**
     * Left-associative reduction on F using the function f.
     *
     * Implementations should override this method when possible.
     */
    fun <A> Kind<F, A>.reduceLeft(f: (A, A) -> A): A = this.reduceLeftTo({ a -> a }, f)

    /**
     * Right-associative reduction on F using the function f.
     */
    fun <A> Kind<F, A>.reduceRight(f: (A, Eval<A>) -> Eval<A>): Eval<A> = this.reduceRightTo({ a -> a }, f)

    /**
     * Apply f to the "initial element" of fa and combine it with every other value using
     * the given function g.
     */
    fun <A, B> Kind<F, A>.reduceLeftTo(f: (A) -> B, g: (B, A) -> B): B

    override fun <A, B> Kind<F, A>.reduceLeftToOption(f: (A) -> B, g: (B, A) -> B): Option<B> = Some(reduceLeftTo(f, g))

    /**
     * Apply f to the "initial element" of fa and lazily combine it with every other value using the
     * given function g.
     */
    fun <A, B> Kind<F, A>.reduceRightTo(f: (A) -> B, g: (A, Eval<B>) -> Eval<B>): Eval<B>

    override fun <A, B> Kind<F, A>.reduceRightToOption(f: (A) -> B, g: (A, Eval<B>) -> Eval<B>): Eval<Option<B>> =
            reduceRightTo(f, g).map({ Some(it) })

    override fun <A> Kind<F, A>.isEmpty(): Boolean = false

    override fun <A> Kind<F, A>.nonEmpty(): Boolean = true

    /**
     * Reduce a F<A> value using the given Semigroup<A>.
     */
    fun <A> Kind<F, A>.reduce(SG: Semigroup<A>): A = SG.run {
        reduceLeft({ a, b -> a.combine(b) })
    }

    /**
     * Reduce a F<G<A>> value using SemigroupK<G>, a universal semigroup for G<_>.
     *
     * This method is a generalization of reduce.
     */
    fun <G, A> Kind<F, Kind<G, A>>.reduceK(SG: SemigroupK<G>): Kind<G, A> = SG.run {
        reduce(algebra())
    }

    /**
     * Apply f to each element of fa and combine them using the given Semigroup<B>.
     */
    fun <A, B> Kind<F, A>.reduceMap(SG: Semigroup<B>, f: (A) -> B): B = SG.run {
        reduceLeftTo(f, { b, a -> b.combine(f(a)) })
    }

}

/**
 * This class defines a Reducible<F> in terms of a Foldable<G> together with a split method, F<A> -> (A, G<A>).
 *
 * This class can be used on any type where the first value (A) and the "rest" of the values (G<A>) can be easily found.
 */
interface NonEmptyReducible<F, G> : Reducible<F> {

    fun FG(): Foldable<G>

    fun <A> split(fa: Kind<F, A>): Tuple2<A, Kind<G, A>>

    override fun <A, B> foldLeft(fa: Kind<F, A>, b: B, f: (B, A) -> B): B = FG().run {
        val (a, ga) = split(fa)
        foldLeft(ga, f(b, a), f)
    }

    override fun <A, B> foldRight(fa: Kind<F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            Eval.Always({ split(fa) }).flatMap { (a, ga) -> f(a, FG().foldRight(ga, lb, f)) }

    override fun <A, B> Kind<F, A>.reduceLeftTo(f: (A) -> B, g: (B, A) -> B): B = FG().run {
        val (a, ga) = split(this@reduceLeftTo)
        foldLeft(ga, f(a), { bb, aa -> g(bb, aa) })
    }

    override fun <A, B> Kind<F, A>.reduceRightTo(f: (A) -> B, g: (A, Eval<B>) -> Eval<B>): Eval<B> = FG().run {
        Eval.Always({ split(this@reduceRightTo) }).flatMap { (a, ga) ->
            ga.reduceRightToOption(f, g).flatMap { option ->
                when (option) {
                    is Some<B> -> g(a, Eval.Now(option.t))
                    is None -> Eval.Later({ f(a) })
                }
            }
        }
    }

    override fun <A> Kind<F, A>.fold(MN: Monoid<A>): A = MN.run {
        val (a, ga) = split(this@fold)
        return a.combine(FG().run { ga.fold(MN) })
    }

    override fun <A> Kind<F, A>.find(f: (A) -> Boolean): Option<A> = FG().run {
        val (a, ga) = split(this@find)
        return if (f(a)) Some(a) else ga.find(f)
    }

    override fun <A> Kind<F, A>.exists(p: (A) -> Boolean): Boolean = this@NonEmptyReducible.FG().run {
        val (a, ga) = this@NonEmptyReducible.split(this@exists)
        return p(a) || ga.exists(p)
    }

    override fun <A> Kind<F, A>.forAll(p: (A) -> Boolean): Boolean = FG().run {
        val (a, ga) = split(this@forAll)
        return p(a) && ga.forAll(p)
    }

    override fun <A> Kind<F, A>.size(MN: Monoid<Long>): Long = FG().run {
        val (_, tail) = split(this@size)
        return 1 + tail.size(MN)
    }

    override fun <A> Kind<F, A>.get(M: Monad<Kind<ForEither, A>>, idx: Long): Option<A> =
            if (idx == 0L)
                Some(split(this).a)
            else
                FG().run { split(this@get).b.get(M, idx - 1L) }

    fun <A, B> Kind<F, A>.foldM_(M: Monad<G>, z: B, f: (B, A) -> Kind<G, B>): Kind<G, B> = M.run {
        val (a, ga) = split(this@foldM_)
        return f(z, a).flatMap({ FG().run { ga.foldM(M, it, f) } })
    }
}
