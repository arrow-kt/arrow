package arrow.core.test.laws

import arrow.core.test.Law
import arrow.core.test.equalUnderTheLaw
import arrow.typeclasses.Monoid
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.list

object MonoidLaws {

  fun <F> laws(M: Monoid<F>, GEN: Arb<F>, eq: (F, F) -> Boolean = { a, b -> a == b }): List<Law> =
    SemigroupLaws.laws(M, GEN, eq) +
      listOf(
        Law("Monoid Laws: Left identity") { M.monoidLeftIdentity(GEN, eq) },
        Law("Monoid Laws: Right identity") { M.monoidRightIdentity(GEN, eq) },
        Law("Monoid Laws: combineAll should be derived") { M.combineAllIsDerived(GEN, eq) },
        Law("Monoid Laws: combineAll of empty list is empty") { M.combineAllOfEmptyIsEmpty(eq) }
      )

  private suspend fun <F> Monoid<F>.monoidLeftIdentity(GEN: Arb<F>, eq: (F, F) -> Boolean): PropertyContext =
    checkAll(GEN) { a ->
      (empty().combine(a)).equalUnderTheLaw(a, eq)
    }

  private suspend fun <F> Monoid<F>.monoidRightIdentity(GEN: Arb<F>, eq: (F, F) -> Boolean): PropertyContext =
    checkAll(GEN) { a ->
      a.combine(empty()).equalUnderTheLaw(a, eq)
    }

  private suspend fun <F> Monoid<F>.combineAllIsDerived(GEN: Arb<F>, eq: (F, F) -> Boolean): PropertyContext =
    checkAll(5, Arb.list(GEN)) { list ->
      list.fold().equalUnderTheLaw(if (list.isEmpty()) empty() else list.reduce { acc, f -> acc.combine(f) }, eq)
    }

  private fun <F> Monoid<F>.combineAllOfEmptyIsEmpty(eq: (F, F) -> Boolean): Unit =
    emptyList<F>().fold().equalUnderTheLaw(empty(), eq) shouldBe true
}
