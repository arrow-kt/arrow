package arrow.test.laws

import arrow.Kind
import arrow.core.*
import arrow.data.Const
import arrow.data.applicative
import arrow.data.const
import arrow.data.value
import arrow.instances.ComposedApplicative
import arrow.instances.IntMonoid
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall
import arrow.typeclasses.*
import io.kotlintest.properties.forAll

typealias TI<A> = Tuple2<IdOf<A>, IdOf<A>>

typealias TIK<A> = Kind<TIF, A>

@Suppress("UNCHECKED_CAST")
fun <A> TIK<A>.fix(): TIC<A> =
        this as TIC<A>

data class TIC<out A>(val ti: TI<A>) : TIK<A>

class TIF {
    private constructor()
}

object TraverseLaws {
    // FIXME(paco): this implementation will crash the inliner. Wait for fix: https://youtrack.jetbrains.com/issue/KT-18660
    /*
    inline fun <F> laws(TF: Traverse<F>, AF: Applicative<F>, EQ: Eq<Kind<F, Int>>): List<Law> =
        FoldableLaws.laws(TF, { AF.pure(it) }, Eq.any()) + FunctorLaws.laws(AF, EQ) + listOf(
                Law("Traverse Laws: Identity", { identityTraverse(TF, AF, { AF.pure(it) }, EQ) }),
                Law("Traverse Laws: Sequential composition", { sequentialComposition(TF, { AF.pure(it) }, EQ) }),
                Law("Traverse Laws: Parallel composition", { parallelComposition(TF, { AF.pure(it) }, EQ) }),
                Law("Traverse Laws: FoldMap derived", { foldMapDerived(TF, { AF.pure(it) }) })
        )
    */

    inline fun <F> laws(TF: Traverse<F>, FF: Functor<F>, noinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
            FoldableLaws.laws(TF, cf, Eq.any()) + FunctorLaws.laws(FF, cf, EQ) + listOf(
                    Law("Traverse Laws: Identity", { TF.identityTraverse(FF, cf, EQ) }),
                    Law("Traverse Laws: Sequential composition", { TF.sequentialComposition(cf, EQ) }),
                    Law("Traverse Laws: Parallel composition", { TF.parallelComposition(cf, EQ) }),
                    Law("Traverse Laws: FoldMap derived", { TF.foldMapDerived(cf) })
            )

    inline fun <F> Traverse<F>.identityTraverse(FF: Functor<F>, crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>) =
            forAll(genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> Kind<ForId, Int>, fa: Kind<F, Int> ->
                Id.applicative().traverse(fa, f).value().equalUnderTheLaw(FF.map(FF.map(fa, f)) { it.value() }, EQ)
            })

    inline fun <F> Traverse<F>.sequentialComposition(crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>) =
            forAll(genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> Kind<ForId, Int>, g: (Int) -> Kind<ForId, Int>, fha: Kind<F, Int> ->
                val fa = Id.applicative().traverse(fha, f).fix()
                val composed = Id.functor().map(fa, { Id.applicative().traverse(it, g) }).value.value()
                val expected = ComposedApplicative(Id.applicative(), Id.applicative()).traverse(fha, { a: Int -> Id.functor().map(f(a), g).nest() }).unnest().value().value()
                composed.equalUnderTheLaw(expected, EQ)
            })

    inline fun <F> Traverse<F>.parallelComposition(crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>) =
            forAll(genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> Kind<ForId, Int>, g: (Int) -> Kind<ForId, Int>, fha: Kind<F, Int> ->
                val TIA = object : Applicative<TIF> {
                    override fun <A> pure(a: A): Kind<TIF, A> =
                            TIC(Id(a) toT Id(a))

                    override fun <A, B> ap(fa: Kind<TIF, A>, ff: Kind<TIF, (A) -> B>): Kind<TIF, B> {
                        val (fam, fan) = fa.fix().ti
                        val (fm, fn) = ff.fix().ti
                        return TIC(Id.applicative().ap(fam, fm) toT Id.applicative().ap(fan, fn))
                    }

                }

                val TIEQ: Eq<TI<Kind<F, Int>>> = Eq<TI<Kind<F, Int>>> { a, b ->
                    with(EQ) {
                        a.a.value().eqv(b.a.value()) && a.b.value().eqv(b.b.value())
                    }
                }

                val seen: TI<Kind<F, Int>> = TIA.traverse(fha, { TIC(f(it) toT g(it)) }).fix().ti
                val expected: TI<Kind<F, Int>> = TIC(Id.applicative().traverse(fha, f) toT Id.applicative().traverse(fha, g)).ti

                seen.equalUnderTheLaw(expected, TIEQ)
            })

    inline fun <F> Traverse<F>.foldMapDerived(crossinline cf: (Int) -> Kind<F, Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: Kind<F, Int> ->
                val traversed = Const.applicative(IntMonoid).traverse(fa, { a -> f(a).const() }).value()
                val mapped = IntMonoid.foldMap(fa, f)
                mapped.equalUnderTheLaw(traversed, Eq.any())
            })
}
