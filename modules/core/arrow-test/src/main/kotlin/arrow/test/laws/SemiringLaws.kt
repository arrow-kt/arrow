package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Semiring
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

object SemiringLaws {

  fun <F> laws(SG: Semiring<F>, GEN: Gen<F>, EQ: Eq<F>): List<Law> =
    MonoidLaws.laws(SG, GEN, EQ) +
      listOf(
        Law("Semiring: Multiplicative commutativity") { SG.semiringMultiplicativeCommutativity(GEN, EQ) },
        Law("Semiring: Right distributivity") { SG.semiringRightDistributivity(GEN, EQ) },
        Law("Semiring: Left distributivity") { SG.semiringLeftDistributivity(GEN, EQ) },
        Law("Semiring: Multiplicative left identity") { SG.semiringMultiplicativeLeftIdentity(GEN, EQ) },
        Law("Semiring: Multiplicative right identity") { SG.semiringMultiplicativeRightIdentity(GEN, EQ) },
        Law("Semiring: Multiplicative left absorption") { SG.semiringMultiplicativeLeftAbsorption(GEN, EQ) },
        Law("Semiring: Multiplicative right absorption") { SG.semiringMultiplicativeRightAbsorption(GEN, EQ) },
        Law("Semiring: times is derived") { SG.timesIsDerived(GEN, EQ) },
        Law("Semiring: zero is derived") { SG.zeroIsDerived(EQ) },
        Law("Semiring: maybeCombineAddition is derived") { SG.maybeCombineAdditionIsDerived(GEN, EQ) },
        Law("Semiring: maybeCombineAddition left null") { SG.maybeCombineAdditionLeftNull(GEN, EQ) },
        Law("Semiring: maybeCombineAddition right null") { SG.maybeCombineAdditionRightNull(GEN, EQ) },
        Law("Semiring: maybeCombineAddition both null") { SG.maybeCombineAdditionBothNull(EQ) },
        Law("Semiring: maybeCombineMultiplicate is derived") { SG.maybeCombineMultiplicateIsDerived(GEN, EQ) },
        Law("Semiring: maybeCombineMultiplicate left null") { SG.maybeCombineMultiplicateLeftNull(GEN, EQ) },
        Law("Semiring: maybeCombineMultiplicate right null") { SG.maybeCombineMultiplicateRightNull(GEN, EQ) },
        Law("Semiring: maybeCombineMultiplicate Both null") { SG.maybeCombineMultiplicateBothNull(EQ) }
      )

  fun <F> Semiring<F>.semiringMultiplicativeCommutativity(GEN: Gen<F>, EQ: Eq<F>) =
    forAll(GEN, GEN) { A, B ->
      A.combineMultiplicate(B).equalUnderTheLaw(B.combineMultiplicate(A), EQ)
    }

  fun <F> Semiring<F>.semiringRightDistributivity(GEN: Gen<F>, EQ: Eq<F>) =
    forAll(GEN, GEN, GEN) { A, B, C ->
      (A.combine(B)).combineMultiplicate(C).equalUnderTheLaw((A.combineMultiplicate(C)).combine(B.combineMultiplicate(C)), EQ)
    }

  fun <F> Semiring<F>.semiringLeftDistributivity(GEN: Gen<F>, EQ: Eq<F>) =
    forAll(GEN, GEN, GEN) { A, B, C ->
      A.combine(B.combineMultiplicate(C)).equalUnderTheLaw((A.combineMultiplicate(B)).combine(A.combineMultiplicate(C)), EQ)
    }

  fun <F> Semiring<F>.semiringMultiplicativeLeftIdentity(GEN: Gen<F>, EQ: Eq<F>) =
    forAll(GEN) { A ->
      (one().combineMultiplicate(A)).equalUnderTheLaw(A, EQ)
    }

  fun <F> Semiring<F>.semiringMultiplicativeRightIdentity(GEN: Gen<F>, EQ: Eq<F>) =
    forAll(GEN) { A ->
      A.combineMultiplicate(one()).equalUnderTheLaw(A, EQ)
    }

  fun <F> Semiring<F>.semiringMultiplicativeLeftAbsorption(GEN: Gen<F>, EQ: Eq<F>) =
    forAll(GEN) { A ->
      (zero().combineMultiplicate(A)).equalUnderTheLaw(zero(), EQ)
    }

  fun <F> Semiring<F>.semiringMultiplicativeRightAbsorption(GEN: Gen<F>, EQ: Eq<F>) =
    forAll(GEN) { A ->
      A.combineMultiplicate(zero()).equalUnderTheLaw(zero(), EQ)
    }

  fun <F> Semiring<F>.timesIsDerived(GEN: Gen<F>, EQ: Eq<F>): Unit =
    forAll(GEN, GEN) { A, B ->
      A.times(B).equalUnderTheLaw(A.combineMultiplicate(B), EQ)
    }

  fun <F> Semiring<F>.zeroIsDerived(EQ: Eq<F>): Unit =
    zero().equalUnderTheLaw(empty(), EQ) shouldBe true

  fun <F> Semiring<F>.maybeCombineAdditionIsDerived(GEN: Gen<F>, EQ: Eq<F>): Unit =
    forAll(GEN, GEN) { A, B ->
      A.maybeCombineAddition(B).equalUnderTheLaw(A.combine(B), EQ)
    }

  fun <F> Semiring<F>.maybeCombineAdditionLeftNull(GEN: Gen<F>, EQ: Eq<F>): Unit =
    forAll(GEN) { A ->
      null.maybeCombineAddition(A).equalUnderTheLaw(zero(), EQ)
    }

  fun <F> Semiring<F>.maybeCombineAdditionRightNull(GEN: Gen<F>, EQ: Eq<F>): Unit =
    forAll(GEN) { A ->
      A.maybeCombineAddition(null).equalUnderTheLaw(A, EQ)
    }

  fun <F> Semiring<F>.maybeCombineAdditionBothNull(EQ: Eq<F>): Unit =
    null.maybeCombineAddition(null).equalUnderTheLaw(zero(), EQ) shouldBe true

  fun <F> Semiring<F>.maybeCombineMultiplicateIsDerived(GEN: Gen<F>, EQ: Eq<F>): Unit =
    forAll(GEN, GEN) { A, B ->
      A.maybeCombineMultiplicate(B).equalUnderTheLaw(A.combineMultiplicate(B), EQ)
    }

  fun <F> Semiring<F>.maybeCombineMultiplicateLeftNull(GEN: Gen<F>, EQ: Eq<F>): Unit =
    forAll(GEN) { A ->
      null.maybeCombineMultiplicate(A).equalUnderTheLaw(one(), EQ)
    }

  fun <F> Semiring<F>.maybeCombineMultiplicateRightNull(GEN: Gen<F>, EQ: Eq<F>): Unit =
    forAll(GEN) { A ->
      A.maybeCombineMultiplicate(null).equalUnderTheLaw(A, EQ)
    }

  fun <F> Semiring<F>.maybeCombineMultiplicateBothNull(EQ: Eq<F>): Unit =
    null.maybeCombineMultiplicate(null).equalUnderTheLaw(one(), EQ) shouldBe true
}
