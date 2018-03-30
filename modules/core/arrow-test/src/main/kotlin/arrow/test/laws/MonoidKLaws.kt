package arrow.test.laws

import arrow.Kind
import arrow.test.generators.genConstructor
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.MonoidK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonoidKLaws {

    inline fun <F> laws(SGK: MonoidK<F>, AP: Applicative<F>, EQ: Eq<Kind<F, Int>>): List<Law> =
            SemigroupKLaws.laws(SGK, AP, EQ) + listOf(
                    Law("MonoidK Laws: Left identity", { SGK.monoidKLeftIdentity(AP::just, EQ) }),
                    Law("MonoidK Laws: Right identity", { SGK.monoidKRightIdentity(AP::just, EQ) }))

    inline fun <F> laws(SGK: MonoidK<F>, noinline f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
            SemigroupKLaws.laws(SGK, f, EQ) + listOf(
                    Law("MonoidK Laws: Left identity", { SGK.monoidKLeftIdentity(f, EQ) }),
                    Law("MonoidK Laws: Right identity", { SGK.monoidKRightIdentity(f, EQ) }))

    fun <F> MonoidK<F>.monoidKLeftIdentity(f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), f), { fa: Kind<F, Int> ->
                empty<Int>().combineK(fa).equalUnderTheLaw(fa, EQ)
            })

    fun <F> MonoidK<F>.monoidKRightIdentity(f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), f), { fa: Kind<F, Int> ->
                fa.combineK(empty<Int>()).equalUnderTheLaw(fa, EQ)
            })
}
