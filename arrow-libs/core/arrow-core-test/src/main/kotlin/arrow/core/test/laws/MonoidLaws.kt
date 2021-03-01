package arrow.core.test.laws

import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

object MonoidLaws {

  fun <F> laws(M: Monoid<F>, GEN: Gen<F>, f: (F, F) -> Boolean = { a, b -> a?.equals(b) == true }): List<Law> =
    SemigroupLaws.laws(M, GEN, f) +
      listOf(
        Law("Monoid Laws: Left identity") { M.monoidLeftIdentity(GEN, f) },
        Law("Monoid Laws: Right identity") { M.monoidRightIdentity(GEN, f) },
        Law("Monoid Laws: combineAll should be derived") { M.combineAllIsDerived(GEN, f) },
        Law("Monoid Laws: combineAll of empty list is empty") { M.combineAllOfEmptyIsEmpty(f) }
      )

  fun <F> Monoid<F>.monoidLeftIdentity(GEN: Gen<F>, f: (F, F) -> Boolean): Unit =
    forAll(GEN) { a ->
      (empty().combine(a)).equalUnderTheLaw(a, f)
    }

  fun <F> Monoid<F>.monoidRightIdentity(GEN: Gen<F>, f: (F, F) -> Boolean): Unit =
    forAll(GEN) { a ->
      a.combine(empty()).equalUnderTheLaw(a, f)
    }

  fun <F> Monoid<F>.combineAllIsDerived(GEN: Gen<F>, f: (F, F) -> Boolean): Unit =
    forAll(5, Gen.list(GEN)) { list ->
      list.combineAll().equalUnderTheLaw(if (list.isEmpty()) empty() else list.reduce { acc, f -> acc.combine(f) }, f)
    }

  fun <F> Monoid<F>.combineAllOfEmptyIsEmpty(f: (F, F) -> Boolean): Unit =
    emptyList<F>().combineAll().equalUnderTheLaw(empty(), f) shouldBe true
}
