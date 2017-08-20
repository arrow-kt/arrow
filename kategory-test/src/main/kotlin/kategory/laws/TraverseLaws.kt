package kategory

import io.kotlintest.properties.forAll

typealias TI<A> = Tuple2<IdKind<A>, IdKind<A>>

typealias TIK<A> = HK<TIF, A>

@Suppress("UNCHECKED_CAST")
fun <A> TIK<A>.ev(): TIC<A> =
        this as TIC<A>

data class TIC<out A>(val ti: TI<A>) : TIK<A>

class TIF {
    private constructor()
}

object TraverseLaws {
    // FIXME(paco): this implementation will crash the inliner. Wait for fix: https://youtrack.jetbrains.com/issue/KT-18660
    /*
    inline fun <reified F> laws(TF: Traverse<F> = traverse<F>(), AF: Applicative<F> = applicative<F>(), EQ: Eq<HK<F, Int>>): List<Law> =
        FoldableLaws.laws(TF, { AF.pure(it) }, Eq.any()) + FunctorLaws.laws(AF, EQ) + listOf(
                Law("Traverse Laws: Identity", { identityTraverse(TF, AF, { AF.pure(it) }, EQ) }),
                Law("Traverse Laws: Sequential composition", { sequentialComposition(TF, { AF.pure(it) }, EQ) }),
                Law("Traverse Laws: Parallel composition", { parallelComposition(TF, { AF.pure(it) }, EQ) }),
                Law("Traverse Laws: FoldMap derived", { foldMapDerived(TF, { AF.pure(it) }) })
        )
    */

    inline fun <reified F> laws(TF: Traverse<F> = traverse<F>(), FF: Functor<F> = functor<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): List<Law> =
            FoldableLaws.laws(TF, cf, Eq.any()) + FunctorLaws.laws(FF, cf, EQ) + listOf(
                    Law("Traverse Laws: Identity", { identityTraverse(TF, FF, cf, EQ) }),
                    Law("Traverse Laws: Sequential composition", { sequentialComposition(TF, cf, EQ) }),
                    Law("Traverse Laws: Parallel composition", { parallelComposition(TF, cf, EQ) }),
                    Law("Traverse Laws: FoldMap derived", { foldMapDerived(TF, cf) })
            )

    inline fun <reified F> identityTraverse(FT: Traverse<F>, FF: Functor<F> = functor<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>) =
            forAll(genFunctionAToB<Int, HK<IdHK, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> HK<IdHK, Int>, fa: HK<F, Int> ->
                FT.traverse(fa, f, Id).value().equalUnderTheLaw(FF.map(fa, f).map(FF) { it.value() }, EQ)
            })

    inline fun <reified F> sequentialComposition(FT: Traverse<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>) =
            forAll(genFunctionAToB<Int, HK<IdHK, Int>>(genConstructor(genIntSmall(), ::Id)), genFunctionAToB<Int, HK<IdHK, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> HK<IdHK, Int>, g: (Int) -> HK<IdHK, Int>, fha: HK<F, Int> ->
                val fa = fha.traverse(FT, Id, f).ev()
                val composed = Id.map(fa, { it.traverse(FT, Id, g) }).value.value()
                val expected = fha.traverse(FT, ComposedApplicative(Id, Id), { a: Int -> Id.map(f(a), g).lift() }).lower().value().value()
                composed.equalUnderTheLaw(expected, EQ)
            })

    inline fun <reified F> parallelComposition(FT: Traverse<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>) =
            forAll(genFunctionAToB<Int, HK<IdHK, Int>>(genConstructor(genIntSmall(), ::Id)), genFunctionAToB<Int, HK<IdHK, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> HK<IdHK, Int>, g: (Int) -> HK<IdHK, Int>, fha: HK<F, Int> ->
                val TIA = object : Applicative<TIF> {
                    override fun <A> pure(a: A): HK<TIF, A> =
                            TIC(Id(a) toT Id(a))


                    override fun <A, B> ap(fa: HK<TIF, A>, ff: HK<TIF, (A) -> B>): HK<TIF, B> {
                        val (fam, fan) = fa.ev().ti
                        val (fm, fn) = ff.ev().ti
                        return TIC(Id.ap(fam, fm) toT Id.ap(fan, fn))
                    }

                }

                val TIEQ: Eq<TI<HK<F, Int>>> = object : Eq<TI<HK<F, Int>>> {
                    override fun eqv(a: TI<HK<F, Int>>, b: TI<HK<F, Int>>): Boolean =
                            EQ.eqv(a.a.value(), b.a.value()) && EQ.eqv(a.b.value(), b.b.value())
                }

                val seen: TI<HK<F, Int>> = FT.traverse(fha, { TIC(f(it) toT g(it)) }, TIA).ev().ti
                val expected: TI<HK<F, Int>> = TIC(FT.traverse(fha, f, Id) toT FT.traverse(fha, g, Id)).ti

                seen.equalUnderTheLaw(expected, TIEQ)
            })

    inline fun <reified F> foldMapDerived(FT: Traverse<F>, crossinline cf: (Int) -> HK<F, Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: HK<F, Int> ->
                val traversed = fa.traverse(FT, Const.applicative(IntMonoid), { a -> f(a).const() }).value()
                val mapped = fa.foldMap(FT, IntMonoid, f)
                mapped.equalUnderTheLaw(traversed, Eq.any())
            })
}