package arrow.core.test.laws

import arrow.core.test.Law
import arrow.core.test.LawSet
import arrow.core.test.equalUnderTheLaw
import arrow.typeclasses.Monoid
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.list

data class MonoidLaws<F>(
  val empty: F,
  val combine: (F, F) -> F,
  val GEN: Arb<F>,
  val eq: (F, F) -> Boolean = { a, b -> a == b }
): LawSet {

  override val laws: List<Law> =
    SemigroupLaws(combine, GEN, eq).laws +
      listOf(
        Law("Monoid Laws: Left identity") { monoidLeftIdentity() },
        Law("Monoid Laws: Right identity") { monoidRightIdentity() },
        Law("Monoid Laws: combineAll should be derived") { combineAllIsDerived() },
        Law("Monoid Laws: combineAll of empty list is empty") { combineAllOfEmptyIsEmpty() }
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

  private fun combineAllOfEmptyIsEmpty(): Unit =
    emptyList<F>().fold(empty, combine).equalUnderTheLaw(empty, eq) shouldBe true
}
