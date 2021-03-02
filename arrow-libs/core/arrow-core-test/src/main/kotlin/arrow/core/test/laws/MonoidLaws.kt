package arrow.core.test.laws

import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

object MonoidLaws {

  fun <F> laws(M: Monoid<F>, GEN: Gen<F>, eq: (F, F) -> Boolean = { a, b -> a == b }): List<Law> =
    SemigroupLaws.laws(M, GEN, eq) +
      listOf(
        Law("Monoid Laws: Left identity") { M.monoidLeftIdentity(GEN, eq) },
        Law("Monoid Laws: Right identity") { M.monoidRightIdentity(GEN, eq) },
        Law("Monoid Laws: combineAll should be derived") { M.combineAllIsDerived(GEN, eq) },
        Law("Monoid Laws: combineAll of empty list is empty") { M.combineAllOfEmptyIsEmpty(eq) }
      )

  fun <F> Monoid<F>.monoidLeftIdentity(GEN: Gen<F>, eq: (F, F) -> Boolean): Unit =
    forAll(GEN) { a ->
      (empty().combine(a)).equalUnderTheLaw(a, eq)
    }

  fun <F> Monoid<F>.monoidRightIdentity(GEN: Gen<F>, eq: (F, F) -> Boolean): Unit =
    forAll(GEN) { a ->
      a.combine(empty()).equalUnderTheLaw(a, eq)
    }

  fun <F> Monoid<F>.combineAllIsDerived(GEN: Gen<F>, eq: (F, F) -> Boolean): Unit =
    forAll(5, Gen.list(GEN)) { list ->
      list.combineAll().equalUnderTheLaw(if (list.isEmpty()) empty() else list.reduce { acc, f -> acc.combine(f) }, eq)
    }

  fun <F> Monoid<F>.combineAllOfEmptyIsEmpty(eq: (F, F) -> Boolean): Unit =
    emptyList<F>().combineAll().equalUnderTheLaw(empty(), eq) shouldBe true
}
