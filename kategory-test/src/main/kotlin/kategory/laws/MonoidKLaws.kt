package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonoidKLaws {

    inline fun <reified F> laws(SGK: MonoidK<F>, AP: Applicative<F>, EQ: Eq<HK<F, Int>>): List<Law> =
            SemigroupKLaws.laws(SGK, AP, EQ) + listOf(
                    Law("MonoidK Laws: Left identity", { monoidKLeftIdentity(SGK, AP, EQ) }),
                    Law("MonoidK Laws: Right identity", { monoidKRightIdentity(SGK, AP, EQ) }))

    inline fun <reified F> monoidKLeftIdentity(SGK: MonoidK<F>, AP: Applicative<F>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genApplicative(Gen.int(), AP), { fa: HK<F, Int> ->
                SGK.combineK(SGK.empty<Int>(), fa).equalUnderTheLaw(fa, EQ)
            })

    inline fun <reified F> monoidKRightIdentity(SGK: MonoidK<F>, AP: Applicative<F>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genApplicative(Gen.int(), AP), { fa: HK<F, Int> ->
                SGK.combineK(fa, SGK.empty<Int>()).equalUnderTheLaw(fa, EQ)
            })
}
