package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Semiring

object SemiringLaws {

    fun <F> laws(SG: Semiring<F>, A: F, B: F, C: F, EQ: Eq<F>): List<Law> =
            listOf(
                    Law("Semiring: Additive commutativity") {
                        SG.semiringAdditiveCommutativity(A, B, C, EQ)
                    },
                    Law("Semiring: Multiplicative commutativity") {
                        SG.semiringMultiplicativeCommutativity(A, B, C, EQ)
                    },
                    Law("Semiring: Right distributivity") {
                        SG.semiringRightDistributivity(A, B, C, EQ)
                    },
                    Law("Semiring: Left distributivity") {
                        SG.semiringLeftDistributivity(A, B, C, EQ)
                    },
                    Law("Semiring: Multiplicative left identity") {
                        SG.semiringMultiplicativeLeftIdentity(A, B, C, EQ)
                    },
                    Law("Semiring: Multiplicative right identity") {
                        SG.semiringMultiplicativeRightIdentity(A, B, C, EQ)
                    },
                    Law("Semiring: Multiplicative left absorption") {
                        SG.semiringMultiplicativeLeftAbsorption(A, B, C, EQ)
                    },
                    Law("Semiring: Multiplicative right absorption") {
                        SG.semiringMultiplicativeRightAbsorption(A, B, C, EQ)
                    }
            )

    fun <F> Semiring<F>.semiringAdditiveCommutativity(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            A.combine(B).equalUnderTheLaw(B.combine(A), EQ)

    fun <F> Semiring<F>.semiringMultiplicativeCommutativity(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            A.combineMultiplicate(B).equalUnderTheLaw(B.combineMultiplicate(A), EQ)

    fun <F> Semiring<F>.semiringRightDistributivity(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            (A.combine(B)).combineMultiplicate(C)
                    .equalUnderTheLaw((A.combineMultiplicate(C)).combine(B.combineMultiplicate(C)), EQ)

    fun <F> Semiring<F>.semiringLeftDistributivity(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            A.combine(B.combineMultiplicate(C))
                    .equalUnderTheLaw((A.combineMultiplicate(B)).combine(A.combineMultiplicate(C)), EQ)

    fun <F> Semiring<F>.semiringMultiplicativeLeftIdentity(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            (one().combineMultiplicate(A)).equalUnderTheLaw(A, EQ)

    fun <F> Semiring<F>.semiringMultiplicativeRightIdentity(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            A.combineMultiplicate(one()).equalUnderTheLaw(A, EQ)

    fun <F> Semiring<F>.semiringMultiplicativeLeftAbsorption(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            (zero().combineMultiplicate(A)).equalUnderTheLaw(zero(), EQ)

    fun <F> Semiring<F>.semiringMultiplicativeRightAbsorption(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            A.combineMultiplicate(zero()).equalUnderTheLaw(zero(), EQ)

}
