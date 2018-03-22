package arrow.test.laws

import arrow.Kind
import arrow.test.generators.genConstructor
import arrow.test.generators.genConstructor2
import arrow.test.generators.genFunctionAToB
import arrow.typeclasses.Alternative
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object AlternativeLaws {

    inline fun <F> laws(AF: Alternative<F>,
                        noinline cf: (Int) -> Kind<F, Int>,
                        noinline cff: (Int) -> Kind<F, (Int) -> Int>,
                        EQ: Eq<Kind<F, Int>>): List<Law> =
            ApplicativeLaws.laws(AF, EQ) + MonoidKLaws.laws(AF, AF, EQ) + listOf(
                    Law("Alternative Laws: Right Absorption", { AF.alternativeRightAbsorption(cff, EQ) }),
                    Law("Alternative Laws: Left Distributivity", { AF.alternativeLeftDistributivity(cf, EQ) }),
                    Law("Alternative Laws: Right Distributivity", { AF.alternativeRightDistributivity(cf, cff, EQ) }))

    fun <F> Alternative<F>.alternativeRightAbsorption(cff: (Int) -> Kind<F, (Int) -> Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor2(Gen.int(), cff), { fa: Kind<F, (Int) -> Int> ->
                ap(empty(), fa).equalUnderTheLaw(empty(), EQ)
            })

    fun <F> Alternative<F>.alternativeLeftDistributivity(cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genConstructor(Gen.int(), cf), genFunctionAToB<Int, Int>(Gen.int()),
                    { fa: Kind<F, Int>, fa2: Kind<F, Int>, f: (Int) -> Int ->
                        map(combineK(fa, fa2), f).equalUnderTheLaw(combineK(map(fa, f), map(fa2, f)), EQ)
                    })

    fun <F> Alternative<F>.alternativeRightDistributivity(cf: (Int) -> Kind<F, Int>,
                                                          cff: (Int) -> Kind<F, (Int) -> Int>,
                                                          EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genConstructor2(Gen.int(), cff), genConstructor2(Gen.int(), cff),
                    { fa: Kind<F, Int>, ff: Kind<F, (Int) -> Int>, fg: Kind<F, (Int) -> Int> ->
                        ap(fa, combineK(ff, fg)).equalUnderTheLaw(combineK(ap(fa, ff), ap(fa, fg)), EQ)
                    })
}
