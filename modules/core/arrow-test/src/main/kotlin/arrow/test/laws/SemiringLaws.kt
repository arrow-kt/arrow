package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Semiring

object SemiringLaws {

  fun <F> laws(SG: Semiring<F>, A: F, B: F, C: F, EQ: Eq<F>): List<Law> =
    listOf(
      Law("Semiring: Additive commutativity") { SG.semiringAdditiveCommutativity(A, B, EQ) },
      Law("Semiring: Multiplicative commutativity") { SG.semiringMultiplicativeCommutativity(A, B, EQ) },
      Law("Semiring: Right distributivity") { SG.semiringRightDistributivity(A, B, C, EQ) },
      Law("Semiring: Left distributivity") { SG.semiringLeftDistributivity(A, B, C, EQ) },
      Law("Semiring: Multiplicative left identity") { SG.semiringMultiplicativeLeftIdentity(A, C, EQ) },
      Law("Semiring: Multiplicative right identity") { SG.semiringMultiplicativeRightIdentity(A, EQ) },
      Law("Semiring: Multiplicative left absorption") { SG.semiringMultiplicativeLeftAbsorption(A, C, EQ) },
      Law("Semiring: Multiplicative right absorption") { SG.semiringMultiplicativeRightAbsorption(A, C, EQ) }
    )

  fun <F> Semiring<F>.semiringAdditiveCommutativity(A: F, B: F, EQ: Eq<F>) =
    A.combine(B).equalUnderTheLaw(B.combine(A), EQ)

  fun <F> Semiring<F>.semiringMultiplicativeCommutativity(A: F, B: F, EQ: Eq<F>) =
    A.combineMultiplicate(B).equalUnderTheLaw(B.combineMultiplicate(A), EQ)

  fun <F> Semiring<F>.semiringRightDistributivity(A: F, B: F, C: F, EQ: Eq<F>) =
    (A.combine(B)).combineMultiplicate(C).equalUnderTheLaw((A.combineMultiplicate(C)).combine(B.combineMultiplicate(C)), EQ)

  fun <F> Semiring<F>.semiringLeftDistributivity(A: F, B: F, C: F, EQ: Eq<F>) =
    A.combine(B.combineMultiplicate(C)).equalUnderTheLaw((A.combineMultiplicate(B)).combine(A.combineMultiplicate(C)), EQ)

  fun <F> Semiring<F>.semiringMultiplicativeLeftIdentity(A: F, C: F, EQ: Eq<F>) =
    (one().combineMultiplicate(A)).equalUnderTheLaw(A, EQ)

  fun <F> Semiring<F>.semiringMultiplicativeRightIdentity(A: F, EQ: Eq<F>) =
    A.combineMultiplicate(one()).equalUnderTheLaw(A, EQ)

  fun <F> Semiring<F>.semiringMultiplicativeLeftAbsorption(A: F, C: F, EQ: Eq<F>) =
    (zero().combineMultiplicate(A)).equalUnderTheLaw(zero(), EQ)

  fun <F> Semiring<F>.semiringMultiplicativeRightAbsorption(A: F, C: F, EQ: Eq<F>) =
    A.combineMultiplicate(zero()).equalUnderTheLaw(zero(), EQ)
}
