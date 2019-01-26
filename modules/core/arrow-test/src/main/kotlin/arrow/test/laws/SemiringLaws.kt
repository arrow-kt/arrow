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
            A.product(B).equalUnderTheLaw(B.product(A), EQ)

    fun <F> Semiring<F>.semiringRightDistributivity(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            (A.combine(B)).product(C).equalUnderTheLaw((A.product(C)).combine(B.product(C)), EQ)

    fun <F> Semiring<F>.semiringLeftDistributivity(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            A.combine(B.product(C)).equalUnderTheLaw((A.product(B)).combine(A.product(C)), EQ)

    fun <F> Semiring<F>.semiringMultiplicativeLeftIdentity(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            (one().product(A)).equalUnderTheLaw(A, EQ)

    fun <F> Semiring<F>.semiringMultiplicativeRightIdentity(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            A.product(one()).equalUnderTheLaw(A, EQ)

    fun <F> Semiring<F>.semiringMultiplicativeLeftAbsorption(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            (zero().product(A)).equalUnderTheLaw(zero(), EQ)

    fun <F> Semiring<F>.semiringMultiplicativeRightAbsorption(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            A.product(zero()).equalUnderTheLaw(zero(), EQ)

}
