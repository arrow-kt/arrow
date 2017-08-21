package kategory.laws

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.*

object FunctorFilterLaws {

    inline fun <reified F> laws(AP: Applicative<F> = applicative(), FFF: FunctorFilter<F> = functorFilter(), EQ: Eq<HK<F, Int>>): List<Law> =
            FunctorLaws.laws(AP, EQ) + listOf(
                    Law("Functor Filter: mapFilter composition", { mapFilterComposition(FFF, AP::pure, EQ) }),
                    Law("Functor Filter: mapFilter map consistency", { mapFilterMapConsistency(FFF, AP::pure, EQ) })
            )

    inline fun <reified F> mapFilterComposition(FFF: FunctorFilter<F> = functorFilter(), crossinline ff: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>> = Eq.any()): Unit =
            forAll(
                    genConstructor(Gen.int(), ff),
                    genFunctionAToB<Int, Option<Int>>(genOption(genIntSmall())),
                    genFunctionAToB<Int, Option<Int>>(genOption(genIntSmall())),
                    { fa: HK<F, Int>, f, g ->
                        FFF.mapFilter(FFF.mapFilter(fa, f), g).equalUnderTheLaw(FFF.mapFilter(fa, { a -> f(a).flatMap(g) }), EQ)
                    })

    inline fun <reified F> mapFilterMapConsistency(FFF: FunctorFilter<F> = functorFilter(), crossinline ff: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>> = Eq.any()): Unit =
            forAll(
                    genConstructor(Gen.int(), ff),
                    genFunctionAToB<Int, Int>(Gen.int()),
                    { fa: HK<F, Int>, f ->
                        FFF.mapFilter(fa, { Option.Some(f(it)) }).equalUnderTheLaw(FFF.map(fa, f), EQ)
                    })
}
