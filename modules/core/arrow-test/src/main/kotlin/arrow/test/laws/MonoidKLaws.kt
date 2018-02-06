package arrow.test.laws

import arrow.*
import arrow.test.generators.genConstructor
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.MonoidK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonoidKLaws {

    inline fun <reified F> laws(SGK: MonoidK<F>, AP: Applicative<F>, EQ: Eq<HK<F, Int>>): List<Law> =
            SemigroupKLaws.laws(SGK, AP, EQ) + listOf(
                    Law("MonoidK Laws: Left identity", { monoidKLeftIdentity(SGK, AP::pure, EQ) }),
                    Law("MonoidK Laws: Right identity", { monoidKRightIdentity(SGK, AP::pure, EQ) }))

    inline fun <reified F> laws(SGK: MonoidK<F>, crossinline f: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): List<Law> =
            SemigroupKLaws.laws(SGK, f, EQ) + listOf(
                    Law("MonoidK Laws: Left identity", { monoidKLeftIdentity(SGK, f, EQ) }),
                    Law("MonoidK Laws: Right identity", { monoidKRightIdentity(SGK, f, EQ) }))

    inline fun <reified F> monoidKLeftIdentity(SGK: MonoidK<F>, crossinline f: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), f), { fa: HK<F, Int> ->
                SGK.combineK(SGK.empty<Int>(), fa).equalUnderTheLaw(fa, EQ)
            })

    inline fun <reified F> monoidKRightIdentity(SGK: MonoidK<F>, crossinline f: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), f), { fa: HK<F, Int> ->
                SGK.combineK(fa, SGK.empty<Int>()).equalUnderTheLaw(fa, EQ)
            })
}
