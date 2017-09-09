package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object AlternativeLaws {

    inline fun <reified F> laws(AF: Alternative<F> = alternative(), SGK: MonoidK<F> = monoidK(),
                                crossinline cf: (Int) -> HK<F, Int>,
                                crossinline cff: (Int) -> HK<F, (Int) -> Int>,
                                EQ: Eq<HK<F, Int>>,
                                EQF: Eq<HK<F, (Int) -> Int>>): List<Law> =
            ApplicativeLaws.laws(AF, EQ) + MonoidKLaws.laws(SGK, AF, EQ) + listOf(
                    Law("Alternative Laws: Right Absorption", { alternativeRightAbsorption(AF, cff, EQ) }),
                    Law("Alternative Laws: Left Distributivity", { alternativeLeftDistributivity(AF, cf, EQF) }),
                    Law("Alternative Laws: Right Distributivity", { alternativeRightDistributivity(AF, cf, cff, EQ) }))

    inline fun <reified F> alternativeRightAbsorption(AF: Alternative<F>, crossinline cff: (Int) -> HK<F, (Int) -> Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor2(Gen.int(), cff), { fa: HK<F, (Int) -> Int> ->
                AF.ap(AF.empty(), fa).equalUnderTheLaw(AF.empty(), EQ)
            })

    inline fun <reified F> alternativeLeftDistributivity(AF: Alternative<F>, crossinline cf: (Int) -> HK<F, Int>, EQF: Eq<HK<F, (Int) -> Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genConstructor(Gen.int(), cf), genFunctionAToB<Int, Int>(Gen.int()),
                    { fa: HK<F, Int>, fa2: HK<F, Int>, f: (Int) -> Int ->
                        AF.combineK(fa, fa2).map { f }.equalUnderTheLaw(AF.combineK(fa.map { f }, fa2.map { f }), EQF)
                    })

    inline fun <reified F> alternativeRightDistributivity(AF: Alternative<F>,
                                                          crossinline cf: (Int) -> HK<F, Int>,
                                                          crossinline cff: (Int) -> HK<F, (Int) -> Int>,
                                                          EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genConstructor2(Gen.int(), cff), genConstructor2(Gen.int(), cff),
                    { fa: HK<F, Int>, ff: HK<F, (Int) -> Int>, fg: HK<F, (Int) -> Int> ->
                        AF.ap(fa, AF.combineK(ff, fg)).equalUnderTheLaw(AF.combineK(AF.ap(fa, ff), AF.ap(fa, fg)), EQ)
                    })
}
