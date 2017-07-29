package kategory

import io.kotlintest.properties.forAll

object MonoidKLaws {

    inline fun <reified F> laws(SGK: MonoidK<F>, EQ: Eq<HK<F, Int?>>): List<Law> =
            SemigroupKLaws.laws(SGK, EQ) + listOf(
                    Law("Left identity", { monoidKLeftIdentity(SGK, EQ) }),
                    Law("Right identity", { monoidKRightIdentity(SGK, EQ) }))

    inline fun <reified F> monoidKLeftIdentity(SGK: MonoidK<F>, EQ: Eq<HK<F, Int?>>): Unit =
            forAll(genEmpty<F, Int>(SGK), { a ->
                SGK.combineK(SGK.empty<Int>(), a).equalUnderTheLaw(a, EQ)
            })

    inline fun <reified F> monoidKRightIdentity(SGK: MonoidK<F>, EQ: Eq<HK<F, Int?>>): Unit =
            forAll(genEmpty<F, Int>(SGK), { a ->
                SGK.combineK(a, SGK.empty<Int>()).equalUnderTheLaw(a, EQ)
            })
}
