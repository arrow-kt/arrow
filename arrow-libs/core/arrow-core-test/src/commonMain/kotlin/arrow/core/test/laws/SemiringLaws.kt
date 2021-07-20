package arrow.core.test.laws

import arrow.typeclasses.Semiring
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotest.property.PropertyContext

public object SemiringLaws {

  public fun <F> laws(SG: Semiring<F>, GEN: Arb<F>, eq: (F, F) -> Boolean = { a, b -> a == b }): List<Law> =
    listOf(
      Law("Semiring: Additive commutativity") { SG.semiringAdditiveCommutativity(GEN, eq) },
      Law("Semiring: Additive left identity") { SG.semiringAdditiveLeftIdentity(GEN, eq) },
      Law("Semiring: Additive right identity") { SG.semiringAdditiveRightIdentity(GEN, eq) },
      Law("Semiring: Additive associativity") { SG.semiringAdditiveAssociativity(GEN, eq) },
      Law("Semiring: Multiplicative commutativity") { SG.semiringMultiplicativeCommutativity(GEN, eq) },
      Law("Semiring: Multiplicative left identity") { SG.semiringMultiplicativeLeftIdentity(GEN, eq) },
      Law("Semiring: Multiplicative right identity") { SG.semiringMultiplicativeRightIdentity(GEN, eq) },
      Law("Semiring: Multiplicative associativity") { SG.semiringMultiplicativeAssociativity(GEN, eq) },
      Law("Semiring: Right distributivity") { SG.semiringRightDistributivity(GEN, eq) },
      Law("Semiring: Left distributivity") { SG.semiringLeftDistributivity(GEN, eq) },
      Law("Semiring: Multiplicative left absorption") { SG.semiringMultiplicativeLeftAbsorption(GEN, eq) },
      Law("Semiring: Multiplicative right absorption") { SG.semiringMultiplicativeRightAbsorption(GEN, eq) },
      Law("Semiring: times is derived") { SG.timesIsDerived(GEN, eq) },
      Law("Semiring: plus is derived") { SG.plusIsDerived(GEN, eq) },
      Law("Semiring: maybeCombineAddition is derived") { SG.maybeCombineAdditionIsDerived(GEN, eq) },
      Law("Semiring: maybeCombineAddition left null") { SG.maybeCombineAdditionLeftNull(GEN, eq) },
      Law("Semiring: maybeCombineAddition right null") { SG.maybeCombineAdditionRightNull(GEN, eq) },
      Law("Semiring: maybeCombineAddition both null") { SG.maybeCombineAdditionBothNull(eq) },
      Law("Semiring: maybeCombineMultiplicate is derived") { SG.maybeCombineMultiplicateIsDerived(GEN, eq) },
      Law("Semiring: maybeCombineMultiplicate left null") { SG.maybeCombineMultiplicateLeftNull(GEN, eq) },
      Law("Semiring: maybeCombineMultiplicate right null") { SG.maybeCombineMultiplicateRightNull(GEN, eq) },
      Law("Semiring: maybeCombineMultiplicate both null") { SG.maybeCombineMultiplicateBothNull(eq) }
    )

  // a + b = b + a
  private suspend fun <F> Semiring<F>.semiringAdditiveCommutativity(GEN: Arb<F>, eq: (F, F) -> Boolean) =
    checkAll(GEN, GEN) { a, b ->
      a.combine(b).equalUnderTheLaw(b.combine(a), eq)
    }

  // 0 + a = a
  private suspend fun <F> Semiring<F>.semiringAdditiveLeftIdentity(GEN: Arb<F>, eq: (F, F) -> Boolean) =
    checkAll(GEN) { A ->
      (zero().combine(A)).equalUnderTheLaw(A, eq)
    }

  // a + 0 = a
  private suspend fun <F> Semiring<F>.semiringAdditiveRightIdentity(GEN: Arb<F>, eq: (F, F) -> Boolean) =
    checkAll(GEN) { A ->
      A.combine(zero()).equalUnderTheLaw(A, eq)
    }

  // a + (b + c) = (a + b) + c
  private suspend fun <F> Semiring<F>.semiringAdditiveAssociativity(GEN: Arb<F>, eq: (F, F) -> Boolean) =
    checkAll(GEN, GEN, GEN) { A, B, C ->
      A.combine(B.combine(C)).equalUnderTheLaw((A.combine(B)).combine(C), eq)
    }

  // a · b = b · a
  private suspend fun <F> Semiring<F>.semiringMultiplicativeCommutativity(GEN: Arb<F>, eq: (F, F) -> Boolean) =
    checkAll(GEN, GEN) { a, b ->
      a.combineMultiplicate(b).equalUnderTheLaw(b.combineMultiplicate(a), eq)
    }

  // 1 · a = a
  private suspend fun <F> Semiring<F>.semiringMultiplicativeLeftIdentity(GEN: Arb<F>, eq: (F, F) -> Boolean) =
    checkAll(GEN) { A ->
      (one().combineMultiplicate(A)).equalUnderTheLaw(A, eq)
    }

  // a · 1 = a
  private suspend fun <F> Semiring<F>.semiringMultiplicativeRightIdentity(GEN: Arb<F>, eq: (F, F) -> Boolean) =
    checkAll(GEN) { A ->
      A.combineMultiplicate(one()).equalUnderTheLaw(A, eq)
    }

  // a · (b · c) = (a · b) · c
  private suspend fun <F> Semiring<F>.semiringMultiplicativeAssociativity(GEN: Arb<F>, eq: (F, F) -> Boolean) =
    checkAll(GEN, GEN, GEN) { A, B, C ->
      A.combineMultiplicate(B.combineMultiplicate(C)).equalUnderTheLaw((B.combineMultiplicate(A)).combineMultiplicate(C), eq)
    }

  // (a + b) · c = a · c + b · c
  private suspend fun <F> Semiring<F>.semiringRightDistributivity(GEN: Arb<F>, eq: (F, F) -> Boolean) =
    checkAll(GEN, GEN, GEN) { A, B, C ->
      (A.combine(B)).combineMultiplicate(C).equalUnderTheLaw((A.combineMultiplicate(C)).combine(B.combineMultiplicate(C)), eq)
    }

  // a · (b + c) = a · b + a · c
  private suspend fun <F> Semiring<F>.semiringLeftDistributivity(GEN: Arb<F>, eq: (F, F) -> Boolean) =
    checkAll(GEN, GEN, GEN) { A, B, C ->
      A.combineMultiplicate(B.combine(C)).equalUnderTheLaw((A.combineMultiplicate(B)).combine(A.combineMultiplicate(C)), eq)
    }

  // 0 · a = 0
  private suspend fun <F> Semiring<F>.semiringMultiplicativeLeftAbsorption(GEN: Arb<F>, eq: (F, F) -> Boolean) =
    checkAll(GEN) { A ->
      (zero().combineMultiplicate(A)).equalUnderTheLaw(zero(), eq)
    }

  // a · 0 = 0
  private suspend fun <F> Semiring<F>.semiringMultiplicativeRightAbsorption(GEN: Arb<F>, eq: (F, F) -> Boolean) =
    checkAll(GEN) { A ->
      A.combineMultiplicate(zero()).equalUnderTheLaw(zero(), eq)
    }

  private suspend fun <F> Semiring<F>.timesIsDerived(GEN: Arb<F>, eq: (F, F) -> Boolean): PropertyContext =
    checkAll(GEN, GEN) { A, B ->
      A.times(B).equalUnderTheLaw(A.combineMultiplicate(B), eq)
    }

  private suspend fun <F> Semiring<F>.plusIsDerived(GEN: Arb<F>, eq: (F, F) -> Boolean): PropertyContext =
    checkAll(GEN, GEN) { A, B ->
      A.plus(B).equalUnderTheLaw(A.combine(B), eq)
    }

  private suspend fun <F> Semiring<F>.maybeCombineAdditionIsDerived(GEN: Arb<F>, eq: (F, F) -> Boolean): PropertyContext =
    checkAll(GEN, GEN) { A, B ->
      A.maybeCombineAddition(B).equalUnderTheLaw(A.combine(B), eq)
    }

  private suspend fun <F> Semiring<F>.maybeCombineAdditionLeftNull(GEN: Arb<F>, eq: (F, F) -> Boolean): PropertyContext =
    checkAll(GEN) { A ->
      null.maybeCombineAddition(A).equalUnderTheLaw(zero(), eq)
    }

  private suspend fun <F> Semiring<F>.maybeCombineAdditionRightNull(GEN: Arb<F>, eq: (F, F) -> Boolean): PropertyContext =
    checkAll(GEN) { A ->
      A.maybeCombineAddition(null).equalUnderTheLaw(A, eq)
    }

  private fun <F> Semiring<F>.maybeCombineAdditionBothNull(eq: (F, F) -> Boolean): Unit =
    null.maybeCombineAddition(null).equalUnderTheLaw(zero(), eq) shouldBe true

  private suspend fun <F> Semiring<F>.maybeCombineMultiplicateIsDerived(GEN: Arb<F>, eq: (F, F) -> Boolean): PropertyContext =
    checkAll(GEN, GEN) { A, B ->
      A.maybeCombineMultiplicate(B).equalUnderTheLaw(A.combineMultiplicate(B), eq)
    }

  private suspend fun <F> Semiring<F>.maybeCombineMultiplicateLeftNull(GEN: Arb<F>, eq: (F, F) -> Boolean): PropertyContext =
    checkAll(GEN) { A ->
      null.maybeCombineMultiplicate(A).equalUnderTheLaw(one(), eq)
    }

  private suspend fun <F> Semiring<F>.maybeCombineMultiplicateRightNull(GEN: Arb<F>, eq: (F, F) -> Boolean): PropertyContext =
    checkAll(GEN) { A ->
      A.maybeCombineMultiplicate(null).equalUnderTheLaw(A, eq)
    }

  private fun <F> Semiring<F>.maybeCombineMultiplicateBothNull(eq: (F, F) -> Boolean): Unit =
    null.maybeCombineMultiplicate(null).equalUnderTheLaw(one(), eq) shouldBe true
}
