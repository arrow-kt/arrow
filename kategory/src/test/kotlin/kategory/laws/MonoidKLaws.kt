package kategory

object MonoidKLaws {

    inline fun <reified F> laws(SGK: MonoidK<F>, AP: Applicative<F>, a: HK<F, Int>, EQ: Eq<HK<F, Int>>, EQSemigroupK: Eq<HK<F, Int>>): List<Law> =
            SemigroupKLaws.laws(SGK, AP, EQSemigroupK) + listOf(
                    Law("MonoidK Laws: Left identity", { monoidKLeftIdentity(SGK, a, EQ) }),
                    Law("MonoidK Laws: Right identity", { monoidKRightIdentity(SGK, a, EQ) }))

    inline fun <reified F> monoidKLeftIdentity(SGK: MonoidK<F>, a: HK<F, Int>, EQ: Eq<HK<F, Int>>): Boolean =
            SGK.combineK(SGK.empty<Int>(), a).equalUnderTheLaw(a, EQ)

    inline fun <reified F> monoidKRightIdentity(SGK: MonoidK<F>, a: HK<F, Int>, EQ: Eq<HK<F, Int>>): Boolean =
            SGK.combineK(a, SGK.empty<Int>()).equalUnderTheLaw(a, EQ)
}
