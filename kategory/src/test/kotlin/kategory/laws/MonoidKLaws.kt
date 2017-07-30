package kategory

import io.kotlintest.properties.forAll

object MonoidKLaws {

    inline fun <reified F> laws(SGK: MonoidK<F>, AP: Applicative<F>, EQ: Eq<HK<F, Id.F>>, EQSemigroupK: Eq<HK<F, Int>>): List<Law> =
            SemigroupKLaws.laws(SGK, AP, EQSemigroupK) + listOf(
                    Law("Left identity", { monoidKLeftIdentity(SGK, EQ) }),
                    Law("Right identity", { monoidKRightIdentity(SGK, EQ) }))

    inline fun <reified F> monoidKLeftIdentity(SGK: MonoidK<F>, EQ: Eq<HK<F, Id.F>>): Unit =
            forAll(genEmpty<F, Id.F>(SGK), { a ->
                SGK.combineK(SGK.empty<Id.F>(), a).equalUnderTheLaw(a, EQ)
            })

    inline fun <reified F> monoidKRightIdentity(SGK: MonoidK<F>, EQ: Eq<HK<F, Id.F>>): Unit =
            forAll(genEmpty<F, Id.F>(SGK), { a ->
                SGK.combineK(a, SGK.empty<Id.F>()).equalUnderTheLaw(a, EQ)
            })
}
