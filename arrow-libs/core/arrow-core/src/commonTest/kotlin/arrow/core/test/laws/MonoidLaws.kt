package arrow.core.test.laws

import arrow.core.test.Law
import arrow.core.test.LawSet
import arrow.core.test.equalUnderTheLaw
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

data class MonoidLaws<F>(
  val name: String,
  val empty: F,
  val combine: (F, F) -> F,
  val GEN: Arb<F>,
  val eq: (F, F) -> Boolean = { a, b -> a == b }
): LawSet {

  override val laws: List<Law> =
    SemigroupLaws(name, combine, GEN, eq).laws +
      listOf(
        Law("Monoid Laws ($name): Left identity") { monoidLeftIdentity() },
        Law("Monoid Laws ($name): Right identity") { monoidRightIdentity() },
        Law("Monoid Laws ($name): combineAll should be derived") { combineAllIsDerived() },
        Law("Monoid Laws ($name): combineAll of empty list is empty") { combineAllOfEmptyIsEmpty() }
      )

  private suspend fun monoidLeftIdentity(): PropertyContext =
    checkAll(GEN) { a ->
      combine(empty, a).equalUnderTheLaw(a, eq)
    }

  private suspend fun monoidRightIdentity(): PropertyContext =
    checkAll(GEN) { a ->
      combine(a, empty).equalUnderTheLaw(a, eq)
    }

  private suspend fun combineAllIsDerived(): PropertyContext =
    checkAll(5, Arb.list(GEN)) { list ->
      list.fold(empty, combine).equalUnderTheLaw(if (list.isEmpty()) empty else list.reduce(combine), eq)
    }

  private fun combineAllOfEmptyIsEmpty() {
    emptyList<F>().fold(empty, combine).equalUnderTheLaw(empty, eq) shouldBe true
  }
}
