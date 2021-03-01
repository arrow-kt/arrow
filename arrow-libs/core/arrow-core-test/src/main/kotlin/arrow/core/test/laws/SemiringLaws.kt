package arrow.core.test.laws

import arrow.typeclasses.Semiring
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

object SemiringLaws {

  fun <F> laws(SG: Semiring<F>, GEN: Gen<F>, f: (F, F) -> Boolean = { a, b -> a?.equals(b) == true }): List<Law> =
    listOf(
      Law("Semiring: Additive commutativity") { SG.semiringAdditiveCommutativity(GEN, f) },
      Law("Semiring: Additive left identity") { SG.semiringAdditiveLeftIdentity(GEN, f) },
      Law("Semiring: Additive right identity") { SG.semiringAdditiveRightIdentity(GEN, f) },
      Law("Semiring: Additive associativity") { SG.semiringAdditiveAssociativity(GEN, f) },
      Law("Semiring: Multiplicative commutativity") { SG.semiringMultiplicativeCommutativity(GEN, f) },
      Law("Semiring: Multiplicative left identity") { SG.semiringMultiplicativeLeftIdentity(GEN, f) },
      Law("Semiring: Multiplicative right identity") { SG.semiringMultiplicativeRightIdentity(GEN, f) },
      Law("Semiring: Multiplicative associativity") { SG.semiringMultiplicativeAssociativity(GEN, f) },
      Law("Semiring: Right distributivity") { SG.semiringRightDistributivity(GEN, f) },
      Law("Semiring: Left distributivity") { SG.semiringLeftDistributivity(GEN, f) },
      Law("Semiring: Multiplicative left absorption") { SG.semiringMultiplicativeLeftAbsorption(GEN, f) },
      Law("Semiring: Multiplicative right absorption") { SG.semiringMultiplicativeRightAbsorption(GEN, f) },
      Law("Semiring: times is derived") { SG.timesIsDerived(GEN, f) },
      Law("Semiring: plus is derived") { SG.plusIsDerived(GEN, f) },
      Law("Semiring: maybeCombineAddition is derived") { SG.maybeCombineAdditionIsDerived(GEN, f) },
      Law("Semiring: maybeCombineAddition left null") { SG.maybeCombineAdditionLeftNull(GEN, f) },
      Law("Semiring: maybeCombineAddition right null") { SG.maybeCombineAdditionRightNull(GEN, f) },
      Law("Semiring: maybeCombineAddition both null") { SG.maybeCombineAdditionBothNull(f) },
      Law("Semiring: maybeCombineMultiplicate is derived") { SG.maybeCombineMultiplicateIsDerived(GEN, f) },
      Law("Semiring: maybeCombineMultiplicate left null") { SG.maybeCombineMultiplicateLeftNull(GEN, f) },
      Law("Semiring: maybeCombineMultiplicate right null") { SG.maybeCombineMultiplicateRightNull(GEN, f) },
      Law("Semiring: maybeCombineMultiplicate both null") { SG.maybeCombineMultiplicateBothNull(f) }
    )

  // a + b = b + a
  fun <F> Semiring<F>.semiringAdditiveCommutativity(GEN: Gen<F>, f: (F, F) -> Boolean) =
    forAll(GEN, GEN) { a, b ->
      a.combine(b).equalUnderTheLaw(b.combine(a), f)
    }

  // 0 + a = a
  fun <F> Semiring<F>.semiringAdditiveLeftIdentity(GEN: Gen<F>, f: (F, F) -> Boolean) =
    forAll(GEN) { A ->
      (zero().combine(A)).equalUnderTheLaw(A, f)
    }

  // a + 0 = a
  fun <F> Semiring<F>.semiringAdditiveRightIdentity(GEN: Gen<F>, f: (F, F) -> Boolean) =
    forAll(GEN) { A ->
      A.combine(zero()).equalUnderTheLaw(A, f)
    }

  // a + (b + c) = (a + b) + c
  fun <F> Semiring<F>.semiringAdditiveAssociativity(GEN: Gen<F>, f: (F, F) -> Boolean) =
    forAll(GEN, GEN, GEN) { A, B, C ->
      A.combine(B.combine(C)).equalUnderTheLaw((A.combine(B)).combine(C), f)
    }

  // a · b = b · a
  fun <F> Semiring<F>.semiringMultiplicativeCommutativity(GEN: Gen<F>, f: (F, F) -> Boolean) =
    forAll(GEN, GEN) { a, b ->
      a.combineMultiplicate(b).equalUnderTheLaw(b.combineMultiplicate(a), f)
    }

  // 1 · a = a
  fun <F> Semiring<F>.semiringMultiplicativeLeftIdentity(GEN: Gen<F>, f: (F, F) -> Boolean) =
    forAll(GEN) { A ->
      (one().combineMultiplicate(A)).equalUnderTheLaw(A, f)
    }

  // a · 1 = a
  fun <F> Semiring<F>.semiringMultiplicativeRightIdentity(GEN: Gen<F>, f: (F, F) -> Boolean) =
    forAll(GEN) { A ->
      A.combineMultiplicate(one()).equalUnderTheLaw(A, f)
    }

  // a · (b · c) = (a · b) · c
  fun <F> Semiring<F>.semiringMultiplicativeAssociativity(GEN: Gen<F>, f: (F, F) -> Boolean) =
    forAll(GEN, GEN, GEN) { A, B, C ->
      A.combineMultiplicate(B.combineMultiplicate(C)).equalUnderTheLaw((B.combineMultiplicate(A)).combineMultiplicate(C), f)
    }

  // (a + b) · c = a · c + b · c
  fun <F> Semiring<F>.semiringRightDistributivity(GEN: Gen<F>, f: (F, F) -> Boolean) =
    forAll(GEN, GEN, GEN) { A, B, C ->
      (A.combine(B)).combineMultiplicate(C).equalUnderTheLaw((A.combineMultiplicate(C)).combine(B.combineMultiplicate(C)), f)
    }

  // a · (b + c) = a · b + a · c
  fun <F> Semiring<F>.semiringLeftDistributivity(GEN: Gen<F>, f: (F, F) -> Boolean) =
    forAll(GEN, GEN, GEN) { A, B, C ->
      A.combineMultiplicate(B.combine(C)).equalUnderTheLaw((A.combineMultiplicate(B)).combine(A.combineMultiplicate(C)), f)
    }

  // 0 · a = 0
  fun <F> Semiring<F>.semiringMultiplicativeLeftAbsorption(GEN: Gen<F>, f: (F, F) -> Boolean) =
    forAll(GEN) { A ->
      (zero().combineMultiplicate(A)).equalUnderTheLaw(zero(), f)
    }

  // a · 0 = 0
  fun <F> Semiring<F>.semiringMultiplicativeRightAbsorption(GEN: Gen<F>, f: (F, F) -> Boolean) =
    forAll(GEN) { A ->
      A.combineMultiplicate(zero()).equalUnderTheLaw(zero(), f)
    }

  fun <F> Semiring<F>.timesIsDerived(GEN: Gen<F>, f: (F, F) -> Boolean): Unit =
    forAll(GEN, GEN) { A, B ->
      A.times(B).equalUnderTheLaw(A.combineMultiplicate(B), f)
    }

  fun <F> Semiring<F>.plusIsDerived(GEN: Gen<F>, f: (F, F) -> Boolean): Unit =
    forAll(GEN, GEN) { A, B ->
      A.plus(B).equalUnderTheLaw(A.combine(B), f)
    }

  fun <F> Semiring<F>.maybeCombineAdditionIsDerived(GEN: Gen<F>, f: (F, F) -> Boolean): Unit =
    forAll(GEN, GEN) { A, B ->
      A.maybeCombineAddition(B).equalUnderTheLaw(A.combine(B), f)
    }

  fun <F> Semiring<F>.maybeCombineAdditionLeftNull(GEN: Gen<F>, f: (F, F) -> Boolean): Unit =
    forAll(GEN) { A ->
      null.maybeCombineAddition(A).equalUnderTheLaw(zero(), f)
    }

  fun <F> Semiring<F>.maybeCombineAdditionRightNull(GEN: Gen<F>, f: (F, F) -> Boolean): Unit =
    forAll(GEN) { A ->
      A.maybeCombineAddition(null).equalUnderTheLaw(A, f)
    }

  fun <F> Semiring<F>.maybeCombineAdditionBothNull(f: (F, F) -> Boolean): Unit =
    null.maybeCombineAddition(null).equalUnderTheLaw(zero(), f) shouldBe true

  fun <F> Semiring<F>.maybeCombineMultiplicateIsDerived(GEN: Gen<F>, f: (F, F) -> Boolean): Unit =
    forAll(GEN, GEN) { A, B ->
      A.maybeCombineMultiplicate(B).equalUnderTheLaw(A.combineMultiplicate(B), f)
    }

  fun <F> Semiring<F>.maybeCombineMultiplicateLeftNull(GEN: Gen<F>, f: (F, F) -> Boolean): Unit =
    forAll(GEN) { A ->
      null.maybeCombineMultiplicate(A).equalUnderTheLaw(one(), f)
    }

  fun <F> Semiring<F>.maybeCombineMultiplicateRightNull(GEN: Gen<F>, f: (F, F) -> Boolean): Unit =
    forAll(GEN) { A ->
      A.maybeCombineMultiplicate(null).equalUnderTheLaw(A, f)
    }

  fun <F> Semiring<F>.maybeCombineMultiplicateBothNull(f: (F, F) -> Boolean): Unit =
    null.maybeCombineMultiplicate(null).equalUnderTheLaw(one(), f) shouldBe true
}
