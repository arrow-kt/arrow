package arrow.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.core.Eval

/**
 * https://www.youtube.com/watch?v=wvSP5qYiz4Y
 */
interface Nested<out F, out G>

typealias NestedType<F, G, A> = Kind<Nested<F, G>, A>

typealias UnnestedType<F, G, A> = Kind<F, Kind<G, A>>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> UnnestedType<F, G, A>.nest(): NestedType<F, G, A> = this as Kind<Nested<F, G>, A>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> NestedType<F, G, A>.unnest(): Kind<F, Kind<G, A>> = this as Kind<F, Kind<G, A>>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> Kind2<F, Kind2<G, A, B>, Kind2<G, A, B>>.binest(): Kind2<Nested<F, G>, A, B> = this as Kind2<Nested<F, G>, A, B>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> Kind2<Nested<F, G>, A, B>.biunnest(): Kind2<F, Kind2<G, A, B>, Kind2<G, A, B>> = this as Kind2<F, Kind2<G, A, B>, Kind2<G, A, B>>

interface ComposedFoldable<F, G> :
        Foldable<Nested<F, G>> {

    fun FF(): Foldable<F>

    fun GF(): Foldable<G>

    override fun <A, B> Kind<Nested<F, G>, A>.foldLeft(b: B, f: (B, A) -> B): B =
            FF().run { unnest().foldLeft(b, { bb, aa -> GF().run { aa.foldLeft(bb, f) } }) }

    fun <A, B> foldLC(fa: Kind<F, Kind<G, A>>, b: B, f: (B, A) -> B): B =
            fa.nest().foldLeft(b, f)

    override fun <A, B> Kind<Nested<F, G>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            FF().run { unnest().foldRight(lb, { laa, lbb -> GF().run { laa.foldRight(lbb, f) } }) }

    fun <A, B> Kind<F, Kind<G, A>>.foldRC(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            nest().foldRight(lb, f)

    companion object {
        operator fun <F, G> invoke(FF: Foldable<F>, GF: Foldable<G>): ComposedFoldable<F, G> =
                object : ComposedFoldable<F, G> {
                    override fun FF(): Foldable<F> = FF

                    override fun GF(): Foldable<G> = GF
                }
    }
}

fun <F, G> Foldable<F>.compose(GT: Foldable<G>): ComposedFoldable<F, G> = object :
        ComposedFoldable<F, G> {
    override fun FF(): Foldable<F> = this@compose

    override fun GF(): Foldable<G> = GT
}

interface ComposedTraverse<F, G> :
        Traverse<Nested<F, G>>,
        ComposedFoldable<F, G> {

    fun FT(): Traverse<F>

    fun GT(): Traverse<G>

    fun GA(): Applicative<G>

    override fun FF(): Foldable<F> = FT()

    override fun GF(): Foldable<G> = GT()

    override fun <H, A, B> Kind<Nested<F, G>, A>.traverse(AP: Applicative<H>, f: (A) -> Kind<H, B>): Kind<H, Kind<Nested<F, G>, B>> = AP.run {
        FT().run { unnest().traverse(AP, { ga -> GT().run { ga.traverse(AP, f) } }) }.map({ it.nest() })
    }

    fun <H, A, B> traverseC(fa: Kind<F, Kind<G, A>>, f: (A) -> Kind<H, B>, HA: Applicative<H>): Kind<H, Kind<Nested<F, G>, B>> =
            fa.nest().traverse(HA, f)

    companion object {
        operator fun <F, G> invoke(
                FF: Traverse<F>,
                GF: Traverse<G>,
                GA: Applicative<G>): ComposedTraverse<F, G> =
                object : ComposedTraverse<F, G> {
                    override fun FT(): Traverse<F> = FF

                    override fun GT(): Traverse<G> = GF

                    override fun GA(): Applicative<G> = GA
                }
    }
}

fun <F, G> Traverse<F>.compose(GT: Traverse<G>, GA: Applicative<G>): Traverse<Nested<F, G>> =
        object :
                ComposedTraverse<F, G> {
            override fun FT(): Traverse<F> = this@compose

            override fun GT(): Traverse<G> = GT

            override fun GA(): Applicative<G> = GA
        }

interface ComposedSemigroupK<F, G> : SemigroupK<Nested<F, G>> {

    fun F(): SemigroupK<F>

    override fun <A> Kind<Nested<F, G>, A>.combineK(y: Kind<Nested<F, G>, A>): Kind<Nested<F, G>, A> = F().run {
        unnest().combineK(y.unnest()).nest()
    }

    fun <A> combineKC(x: Kind<F, Kind<G, A>>, y: Kind<F, Kind<G, A>>): Kind<Nested<F, G>, A> =
            x.nest().combineK(y.nest())

    companion object {
        operator fun <F, G> invoke(SF: SemigroupK<F>): SemigroupK<Nested<F, G>> =
                object : ComposedSemigroupK<F, G> {
                    override fun F(): SemigroupK<F> = SF
                }
    }
}

fun <F, G> SemigroupK<F>.compose(): SemigroupK<Nested<F, G>> = object : ComposedSemigroupK<F, G> {
    override fun F(): SemigroupK<F> = this@compose
}

interface ComposedMonoidK<F, G> : MonoidK<Nested<F, G>>, ComposedSemigroupK<F, G> {

    override fun F(): MonoidK<F>

    override fun <A> empty(): Kind<Nested<F, G>, A> = F().empty<Kind<G, A>>().nest()

    fun <A> emptyC(): Kind<F, Kind<G, A>> = empty<A>().unnest()

    companion object {
        operator fun <F, G> invoke(MK: MonoidK<F>): MonoidK<Nested<F, G>> =
                object : ComposedMonoidK<F, G> {
                    override fun F(): MonoidK<F> = MK
                }
    }
}

fun <F, G> MonoidK<F>.compose(): MonoidK<Nested<F, G>> = object : ComposedMonoidK<F, G> {
    override fun F(): MonoidK<F> = this@compose
}

interface ComposedFunctor<F, G> : Functor<Nested<F, G>> {
    fun F(): Functor<F>

    fun G(): Functor<G>

    override fun <A, B> Kind<Nested<F, G>, A>.map(f: (A) -> B): Kind<Nested<F, G>, B> = F().run {
        unnest().map { G().run { it.map(f) } }.nest()
    }

    fun <A, B> mapC(fa: Kind<F, Kind<G, A>>, f: (A) -> B): Kind<F, Kind<G, B>> =
            fa.nest().map(f).unnest()

    companion object {
        operator fun <F, G> invoke(FF: Functor<F>, GF: Functor<G>): Functor<Nested<F, G>> =
                object : ComposedFunctor<F, G> {
                    override fun F(): Functor<F> = FF

                    override fun G(): Functor<G> = GF
                }
    }
}

fun <F, G> Functor<F>.compose(GF: Functor<G>): Functor<Nested<F, G>> = ComposedFunctor(this, GF)

interface ComposedApplicative<F, G> : Applicative<Nested<F, G>>, ComposedFunctor<F, G> {
    override fun F(): Applicative<F>

    override fun G(): Applicative<G>

    override fun <A, B> Kind<Nested<F, G>, A>.map(f: (A) -> B): Kind<Nested<F, G>, B> =
            ap(pure(f))

    override fun <A> pure(a: A): Kind<Nested<F, G>, A> = F().pure(G().pure(a)).nest()

    override fun <A, B> Kind<Nested<F, G>, A>.ap(ff: Kind<Nested<F, G>, (A) -> B>): Kind<Nested<F, G>, B> =
            F().run { unnest().ap(ff.unnest().map({ gfa: Kind<G, (A) -> B> -> { ga: Kind<G, A> -> G().run { ga.ap(gfa) } } })) }.nest()

    fun <A, B> apC(fa: Kind<F, Kind<G, A>>, ff: Kind<F, Kind<G, (A) -> B>>): Kind<F, Kind<G, B>> =
            fa.nest().ap(ff.nest()).unnest()

    companion object {
        operator fun <F, G> invoke(FF: Applicative<F>, GF: Applicative<G>)
                : Applicative<Nested<F, G>> =
                object : ComposedApplicative<F, G> {
                    override fun F(): Applicative<F> = FF

                    override fun G(): Applicative<G> = GF
                }
    }
}

fun <F, G> Applicative<F>.compose(GA: Applicative<G>): Applicative<Nested<F, G>> = ComposedApplicative(this, GA)

interface ComposedAlternative<F, G> : Alternative<Nested<F, G>>, ComposedApplicative<F, G>, ComposedMonoidK<F, G> {
    override fun F(): Alternative<F>

    companion object {
        operator fun <F, G> invoke(AF: Alternative<F>, AG: Applicative<G>)
                : Alternative<Nested<F, G>> =
                object : ComposedAlternative<F, G> {
                    override fun F(): Alternative<F> = AF

                    override fun G(): Applicative<G> = AG
                }
    }
}

fun <F, G> Alternative<F>.compose(GA: Applicative<G>): Alternative<Nested<F, G>> = ComposedAlternative(this, GA)

interface ComposedBifoldable<F, G> : Bifoldable<Nested<F, G>> {
    fun F(): Bifoldable<F>

    fun G(): Bifoldable<G>

    override fun <A, B, C> Kind2<Nested<F, G>, A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C = F().run {
        biunnest().bifoldLeft(c, { cc: C, gab: Kind2<G, A, B> -> G().run { gab.bifoldLeft(cc, f, g) } },
                { cc: C, gab: Kind2<G, A, B> -> G().run { gab.bifoldLeft(cc, f, g) } })
    }

    override fun <A, B, C> Kind2<Nested<F, G>, A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> = F().run {
        biunnest().bifoldRight(c, { gab: Kind2<G, A, B>, cc: Eval<C> -> G().run { gab.bifoldRight(cc, f, g) } },
                { gab: Kind2<G, A, B>, cc: Eval<C> -> G().run { gab.bifoldRight(cc, f, g) } })
    }

    fun <A, B, C> bifoldLeftC(fab: Kind2<F, Kind2<G, A, B>, Kind2<G, A, B>>, c: C, f: (C, A) -> C, g: (C, B) -> C): C =
            fab.binest().bifoldLeft(c, f, g)

    fun <A, B, C> bifoldRightC(fab: Kind2<F, Kind2<G, A, B>, Kind2<G, A, B>>, c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
            fab.binest().bifoldRight(c, f, g)

    companion object {
        operator fun <F, G> invoke(BF: Bifoldable<F>, BG: Bifoldable<G>): ComposedBifoldable<F, G> =
                object : ComposedBifoldable<F, G> {
                    override fun F(): Bifoldable<F> = BF

                    override fun G(): Bifoldable<G> = BG
                }
    }
}

fun <F, G> Bifoldable<F>.compose(BG: Bifoldable<G>): Bifoldable<Nested<F, G>> = ComposedBifoldable(this, BG)
