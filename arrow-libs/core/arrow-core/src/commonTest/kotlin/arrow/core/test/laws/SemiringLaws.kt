package arrow.core.test.laws

import arrow.core.test.Law
import arrow.core.test.LawSet
import arrow.core.test.equalUnderTheLaw
import io.kotest.property.Arb
import io.kotest.property.checkAll

data class SemiringLaws<F>(
  val zero: F,
  val combine: (F, F) -> F,
  val one: F,
  val combineMultiplicate: (F, F) -> F,
  val GEN: Arb<F>,
  val eq: (F, F) -> Boolean = { a, b -> a == b }
): LawSet {

  override val laws: List<Law> =
    listOf(
      Law("Semiring: Additive commutativity") { semiringAdditiveCommutativity() },
      Law("Semiring: Additive left identity") { semiringAdditiveLeftIdentity() },
      Law("Semiring: Additive right identity") { semiringAdditiveRightIdentity() },
      Law("Semiring: Additive associativity") { semiringAdditiveAssociativity() },
      Law("Semiring: Multiplicative commutativity") { semiringMultiplicativeCommutativity() },
      Law("Semiring: Multiplicative left identity") { semiringMultiplicativeLeftIdentity() },
      Law("Semiring: Multiplicative right identity") { semiringMultiplicativeRightIdentity() },
      Law("Semiring: Multiplicative associativity") { semiringMultiplicativeAssociativity() },
      Law("Semiring: Right distributivity") { semiringRightDistributivity() },
      Law("Semiring: Left distributivity") { semiringLeftDistributivity() },
      Law("Semiring: Multiplicative left absorption") { semiringMultiplicativeLeftAbsorption() },
      Law("Semiring: Multiplicative right absorption") { semiringMultiplicativeRightAbsorption() },
    )

  // a + b = b + a
  private suspend fun semiringAdditiveCommutativity() =
    checkAll(GEN, GEN) { a, b ->
      combine(a, b).equalUnderTheLaw(combine(b, a), eq)
    }

  // 0 + a = a
  private suspend fun semiringAdditiveLeftIdentity() =
    checkAll(GEN) { A ->
      combine(zero, A).equalUnderTheLaw(A, eq)
    }

  // a + 0 = a
  private suspend fun semiringAdditiveRightIdentity() =
    checkAll(GEN) { A ->
      combine(A, zero).equalUnderTheLaw(A, eq)
    }

  // a + (b + c) = (a + b) + c
  private suspend fun semiringAdditiveAssociativity() =
    checkAll(GEN, GEN, GEN) { A, B, C ->
      combine(A, combine(B, C)).equalUnderTheLaw(combine(combine(A, B), C), eq)
    }

  // a · b = b · a
  private suspend fun semiringMultiplicativeCommutativity() =
    checkAll(GEN, GEN) { a, b ->
      combineMultiplicate(a, b).equalUnderTheLaw(combineMultiplicate(b, a), eq)
    }

  // 1 · a = a
  private suspend fun semiringMultiplicativeLeftIdentity() =
    checkAll(GEN) { A ->
      combineMultiplicate(one, A).equalUnderTheLaw(A, eq)
    }

  // a · 1 = a
  private suspend fun semiringMultiplicativeRightIdentity() =
    checkAll(GEN) { A ->
      combineMultiplicate(A, one).equalUnderTheLaw(A, eq)
    }

  // a · (b · c) = (a · b) · c
  private suspend fun semiringMultiplicativeAssociativity() =
    checkAll(GEN, GEN, GEN) { A, B, C ->
      combineMultiplicate(A, combineMultiplicate(B, C)).equalUnderTheLaw(combineMultiplicate(combineMultiplicate(A, B), C), eq)
    }

  // (a + b) · c = a · c + b · c
  private suspend fun semiringRightDistributivity() =
    checkAll(GEN, GEN, GEN) { A, B, C ->
      combineMultiplicate(combine(A, B), C).equalUnderTheLaw(combine(combineMultiplicate(A, C), combineMultiplicate(B, C)), eq)
    }

  // a · (b + c) = a · b + a · c
  private suspend fun semiringLeftDistributivity() =
    checkAll(GEN, GEN, GEN) { A, B, C ->
      combineMultiplicate(A, combine(B, C)).equalUnderTheLaw(combine(combineMultiplicate(A, B), combineMultiplicate(A, C)), eq)
    }

  // 0 · a = 0
  private suspend fun semiringMultiplicativeLeftAbsorption() =
    checkAll(GEN) { A ->
      combineMultiplicate(zero, A).equalUnderTheLaw(zero, eq)
    }

  // a · 0 = 0
  private suspend fun semiringMultiplicativeRightAbsorption() =
    checkAll(GEN) { A ->
      combineMultiplicate(A, zero).equalUnderTheLaw(zero, eq)
    }
}
