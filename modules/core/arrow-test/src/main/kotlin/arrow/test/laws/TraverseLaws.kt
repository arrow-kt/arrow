package arrow.test.laws

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.instances.*
import arrow.syntax.foldable.foldMap
import arrow.syntax.functor.*
import arrow.syntax.traverse.*
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall
import arrow.typeclasses.*
import io.kotlintest.properties.forAll

typealias TI<A> = Tuple2<IdKind<A>, IdKind<A>>

typealias TIK<A> = Kind<TIF, A>

@Suppress("UNCHECKED_CAST")
fun <A> TIK<A>.reify(): TIC<A> =
        this as TIC<A>

data class TIC<out A>(val ti: TI<A>) : TIK<A>

class TIF {
    private constructor()
}

object TraverseLaws {
    // FIXME(paco): this implementation will crash the inliner. Wait for fix: https://youtrack.jetbrains.com/issue/KT-18660
    /*
    inline fun <reified F> laws(TF: Traverse<F> = traverse<F>(), AF: Applicative<F> = applicative<F>(), EQ: Eq<Kind<F, Int>>): List<Law> =
        FoldableLaws.laws(TF, { AF.pure(it) }, Eq.any()) + FunctorLaws.laws(AF, EQ) + listOf(
                Law("Traverse Laws: Identity", { identityTraverse(TF, AF, { AF.pure(it) }, EQ) }),
                Law("Traverse Laws: Sequential composition", { sequentialComposition(TF, { AF.pure(it) }, EQ) }),
                Law("Traverse Laws: Parallel composition", { parallelComposition(TF, { AF.pure(it) }, EQ) }),
                Law("Traverse Laws: FoldMap derived", { foldMapDerived(TF, { AF.pure(it) }) })
        )
    */

    inline fun <reified F> laws(TF: Traverse<F> = traverse<F>(), FF: Functor<F> = functor<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>> = eq()): List<Law> =
            FoldableLaws.laws(TF, cf, eq<Int>()) + FunctorLaws.laws(FF, cf, EQ) + listOf(
                    Law("Traverse Laws: Identity", { identityTraverse(TF, FF, cf, EQ) }),
                    Law("Traverse Laws: Sequential composition", { sequentialComposition(TF, cf, EQ) }),
                    Law("Traverse Laws: Parallel composition", { parallelComposition(TF, cf, EQ) }),
                    Law("Traverse Laws: FoldMap derived", { foldMapDerived(TF, cf) })
            )

    inline fun <reified F> identityTraverse(FT: Traverse<F>, FF: Functor<F> = functor<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>) =
            forAll(genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> Kind<ForId, Int>, fa: Kind<F, Int> ->
                FT.traverse(fa, f, Id.applicative()).value().equalUnderTheLaw(FF.map(fa, f).map(FF) { it.value() }, EQ)
            })

    inline fun <reified F> sequentialComposition(FT: Traverse<F>, crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>) =
            forAll(genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> Kind<ForId, Int>, g: (Int) -> Kind<ForId, Int>, fha: Kind<F, Int> ->
                val fa = fha.traverse(FT, Id.applicative(), f).reify()
                val composed = Id.functor().map(fa, { it.traverse(FT, Id.applicative(), g) }).value.value()
                val expected = fha.traverse(FT, ComposedApplicative(Id.applicative(), Id.applicative()), { a: Int -> Id.functor().map(f(a), g).nest() }).unnest().value().value()
                composed.equalUnderTheLaw(expected, EQ)
            })

    inline fun <reified F> parallelComposition(FT: Traverse<F>, crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>) =
            forAll(genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genFunctionAToB<Int, Kind<ForId, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> Kind<ForId, Int>, g: (Int) -> Kind<ForId, Int>, fha: Kind<F, Int> ->
                val TIA = object : Applicative<TIF> {
                    override fun <A> pure(a: A): Kind<TIF, A> =
                            TIC(Id(a) toT Id(a))


                    override fun <A, B> ap(fa: Kind<TIF, A>, ff: Kind<TIF, (A) -> B>): Kind<TIF, B> {
                        val (fam, fan) = fa.reify().ti
                        val (fm, fn) = ff.reify().ti
                        return TIC(Id.applicative().ap(fam, fm) toT Id.applicative().ap(fan, fn))
                    }

                }

                val TIEQ: Eq<TI<Kind<F, Int>>> = Eq<TI<Kind<F, Int>>> { a, b ->
                    EQ.eqv(a.a.value(), b.a.value()) && EQ.eqv(a.b.value(), b.b.value())
                }

                val seen: TI<Kind<F, Int>> = FT.traverse(fha, { TIC(f(it) toT g(it)) }, TIA).reify().ti
                val expected: TI<Kind<F, Int>> = TIC(FT.traverse(fha, f, Id.applicative()) toT FT.traverse(fha, g, Id.applicative())).ti

                seen.equalUnderTheLaw(expected, TIEQ)
            })

    inline fun <reified F> foldMapDerived(FT: Traverse<F>, crossinline cf: (Int) -> Kind<F, Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: Kind<F, Int> ->
                val traversed = fa.traverse(FT, Const.applicative(IntMonoid), { a -> f(a).const() }).value()
                val mapped = fa.foldMap(FT, IntMonoid, f)
                mapped.equalUnderTheLaw(traversed, Eq.any())
            })
}