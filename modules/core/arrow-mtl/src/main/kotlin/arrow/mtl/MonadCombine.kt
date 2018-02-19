package arrow.mtl

import arrow.*
import arrow.core.Tuple2
import arrow.typeclasses.*

/**
 * The combination of a Monad with a MonoidK
 */
@typeclass(syntax = false)
interface MonadCombine<F> : MonadFilter<F>, Alternative<F>, TC {

    fun <G, A> unite(fga: Kind<F, Kind<G, A>>, FG: Foldable<G>): Kind<F, A> =
            flatMap(fga, { ga -> FG.foldLeft(ga, empty<A>(), { acc, a -> combineK(acc, pure(a)) }) })

    fun <G, A, B> separate(fgab: Kind<F, Kind2<G, A, B>>, BFG: Bifoldable<G>): Tuple2<Kind<F, A>, Kind<F, B>> {
        val asep = flatMap(fgab, { gab -> BFG.bifoldMap(gab, { pure(it) }, { _ -> empty<A>() }, algebra<A>()) })
        val bsep = flatMap(fgab, { gab -> BFG.bifoldMap(gab, { _ -> empty<B>() }, { pure(it) }, algebra<B>()) })
        return Tuple2(asep, bsep)
    }
}

inline fun <F, reified G, A> MonadCombine<F>.uniteF(fga: Kind<F, Kind<G, A>>, FG: Foldable<G> = foldable()) = unite(fga, FG)

inline fun <F, reified G, A, B> MonadCombine<F>.separateF(fgab: Kind<F, Kind2<G, A, B>>, BFG: Bifoldable<G> = bifoldable()) = separate(fgab, BFG)

interface MonadCombineSyntax<F> : MonadFilterSyntax<F>, AlternativeSyntax<F> {

    fun monadCombine(): MonadCombine<F>

    override fun monadFilter(): MonadFilter<F> = monadCombine()

    override fun alternative(): Alternative<F> = monadCombine()

    override fun monad(): Monad<F> = monadCombine()

    override fun applicative(): Applicative<F> = monadCombine()

    override fun functor(): Functor<F> = monadCombine()

    override fun functorFilter(): FunctorFilter<F> = monadCombine()

    override fun monoidK(): MonoidK<F> = monadCombine()

    override fun semigroupK(): SemigroupK<F> = monadCombine()

    fun <G, A, B> Kind<F, Kind2<G, A, B>>.`separate`(dummy: Unit = Unit, BFG: Bifoldable<G>): Tuple2<Kind<F, A>, Kind<F, B>> =
            this@MonadCombineSyntax.monadCombine().`separate`(this, BFG)

    fun <G, A> Kind<F, Kind<G, A>>.`unite`(dummy: Unit = Unit, FG: Foldable<G>): Kind<F, A> =
            this@MonadCombineSyntax.monadCombine().`unite`(this, FG)

    override fun <A> empty(dummy: Unit): Kind<F, A> =
            this@MonadCombineSyntax.monadCombine().`empty`()
}
