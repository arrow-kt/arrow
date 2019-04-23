package arrow.test.laws

import arrow.typeclasses.Semiring
import io.kotlintest.shouldBe

object SemiringLaws {

    fun <F> laws(SG: Semiring<F>, A: F, B: F, C: F): List<Law> =
            listOf(
                    Law("Semiring: Additive commutativity") { SG.semiringAdditiveCommutativity(A, B) },
                    Law("Semiring: Multiplicative commutativity") { SG.semiringMultiplicativeCommutativity(A, B) },
                    Law("Semiring: Right distributivity") { SG.semiringRightDistributivity(A, B, C) },
                    Law("Semiring: Left distributivity") { SG.semiringLeftDistributivity(A, B, C) },
                    Law("Semiring: Multiplicative left identity") { SG.semiringMultiplicativeLeftIdentity(A, C) },
                    Law("Semiring: Multiplicative right identity") { SG.semiringMultiplicativeRightIdentity(A) },
                    Law("Semiring: Multiplicative left absorption") { SG.semiringMultiplicativeLeftAbsorption(A, C) },
                    Law("Semiring: Multiplicative right absorption") { SG.semiringMultiplicativeRightAbsorption(A, C) }
            )

    fun <F> Semiring<F>.semiringAdditiveCommutativity(A: F, B: F) =
            A.combine(B).shouldBe(B.combine(A))

    fun <F> Semiring<F>.semiringMultiplicativeCommutativity(A: F, B: F) =
            A.combineMultiplicate(B).shouldBe(B.combineMultiplicate(A))

    fun <F> Semiring<F>.semiringRightDistributivity(A: F, B: F, C: F) =
            (A.combine(B)).combineMultiplicate(C).shouldBe((A.combineMultiplicate(C)).combine(B.combineMultiplicate(C)))

    fun <F> Semiring<F>.semiringLeftDistributivity(A: F, B: F, C: F) =
            A.combine(B.combineMultiplicate(C)).shouldBe((A.combineMultiplicate(B)).combine(A.combineMultiplicate(C)))

    fun <F> Semiring<F>.semiringMultiplicativeLeftIdentity(A: F, C: F) =
            (one().combineMultiplicate(A)).shouldBe(A)

    fun <F> Semiring<F>.semiringMultiplicativeRightIdentity(A: F) =
            A.combineMultiplicate(one()).shouldBe(A)

    fun <F> Semiring<F>.semiringMultiplicativeLeftAbsorption(A: F, C: F) =
            (zero().combineMultiplicate(A)).shouldBe(zero())

    fun <F> Semiring<F>.semiringMultiplicativeRightAbsorption(A: F, C: F) =
            A.combineMultiplicate(zero()).shouldBe(zero())
}
