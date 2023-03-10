package arrow.core.test.laws

import arrow.core.test.Law
import arrow.core.test.LawSet
import arrow.core.test.equalUnderTheLaw
import arrow.typeclasses.Semigroup
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.checkAll

data class SemigroupLaws<F>(
  val combine: (F, F) -> F,
  val G: Arb<F>,
  val eq: (F, F) -> Boolean = { a, b -> a == b }
): LawSet {

  override val laws: List<Law> =
    listOf(Law("Semigroup: associativity") { semigroupAssociative() })

  private suspend fun semigroupAssociative(): PropertyContext =
    checkAll(G, G, G) { A, B, C ->
      combine(combine(A, B), C).equalUnderTheLaw(combine(A, combine(B, C)), eq)
    }
}
