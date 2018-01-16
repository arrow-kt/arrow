package arrow.test.laws

import arrow.*
import arrow.core.Option
import arrow.core.Some
import arrow.mtl.FunctorFilter
import arrow.mtl.functorFilter
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall
import arrow.test.generators.genOption
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FunctorFilterLaws {

    inline fun <reified F> laws(FFF: FunctorFilter<F> = functorFilter(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): List<Law> =
            FunctorLaws.laws(FFF, cf, EQ) + listOf(
                    Law("Functor Filter: mapFilter composition", { mapFilterComposition(FFF, cf, EQ) }),
                    Law("Functor Filter: mapFilter map consistency", { mapFilterMapConsistency(FFF, cf, EQ) })
            )

    inline fun <reified F> mapFilterComposition(FFF: FunctorFilter<F> = functorFilter(), crossinline ff: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(
                    genConstructor(Gen.int(), ff),
                    genFunctionAToB<Int, Option<Int>>(genOption(genIntSmall())),
                    genFunctionAToB<Int, Option<Int>>(genOption(genIntSmall())),
                    { fa: HK<F, Int>, f, g ->
                        FFF.mapFilter(FFF.mapFilter(fa, f), g).equalUnderTheLaw(FFF.mapFilter(fa, { a -> f(a).flatMap(g) }), EQ)
                    })

    inline fun <reified F> mapFilterMapConsistency(FFF: FunctorFilter<F> = functorFilter(), crossinline ff: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(
                    genConstructor(Gen.int(), ff),
                    genFunctionAToB<Int, Int>(Gen.int()),
                    { fa: HK<F, Int>, f ->
                        FFF.mapFilter(fa, { Some(f(it)) }).equalUnderTheLaw(FFF.map(fa, f), EQ)
                    })
}
