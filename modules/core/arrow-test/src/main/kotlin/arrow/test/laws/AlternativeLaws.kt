package arrow.test.laws

import arrow.*
import arrow.test.generators.genConstructor
import arrow.test.generators.genConstructor2
import arrow.test.generators.genFunctionAToB
import arrow.typeclasses.Alternative
import arrow.typeclasses.Eq
import arrow.typeclasses.alternative
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object AlternativeLaws {

    inline fun <reified F> laws(AF: Alternative<F> = alternative(),
                                crossinline cf: (Int) -> Kind<F, Int>,
                                crossinline cff: (Int) -> Kind<F, (Int) -> Int>,
                                EQ: Eq<Kind<F, Int>>): List<Law> =
            ApplicativeLaws.laws(AF, EQ) + MonoidKLaws.laws(AF, AF, EQ) + listOf(
                    Law("Alternative Laws: Right Absorption", { alternativeRightAbsorption(AF, cff, EQ) }),
                    Law("Alternative Laws: Left Distributivity", { alternativeLeftDistributivity(AF, cf, EQ) }),
                    Law("Alternative Laws: Right Distributivity", { alternativeRightDistributivity(AF, cf, cff, EQ) }))

    inline fun <reified F> alternativeRightAbsorption(AF: Alternative<F>, crossinline cff: (Int) -> Kind<F, (Int) -> Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor2(Gen.int(), cff), { fa: Kind<F, (Int) -> Int> ->
                AF.ap(AF.empty(), fa).equalUnderTheLaw(AF.empty(), EQ)
            })

    inline fun <reified F> alternativeLeftDistributivity(AF: Alternative<F>, crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genConstructor(Gen.int(), cf), genFunctionAToB<Int, Int>(Gen.int()),
                    { fa: Kind<F, Int>, fa2: Kind<F, Int>, f: (Int) -> Int ->
                        AF.map(AF.combineK(fa, fa2), f).equalUnderTheLaw(AF.combineK(AF.map(fa, f), AF.map(fa2, f)), EQ)
                    })

    inline fun <reified F> alternativeRightDistributivity(AF: Alternative<F>,
                                                          crossinline cf: (Int) -> Kind<F, Int>,
                                                          crossinline cff: (Int) -> Kind<F, (Int) -> Int>,
                                                          EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genConstructor2(Gen.int(), cff), genConstructor2(Gen.int(), cff),
                    { fa: Kind<F, Int>, ff: Kind<F, (Int) -> Int>, fg: Kind<F, (Int) -> Int> ->
                        AF.ap(fa, AF.combineK(ff, fg)).equalUnderTheLaw(AF.combineK(AF.ap(fa, ff), AF.ap(fa, fg)), EQ)
                    })
}
