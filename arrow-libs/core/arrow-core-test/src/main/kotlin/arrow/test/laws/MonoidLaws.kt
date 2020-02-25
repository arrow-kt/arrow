package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

object MonoidLaws {

  fun <F> laws(M: Monoid<F>, GEN: Gen<F>, EQ: Eq<F>): List<Law> =
    SemigroupLaws.laws(M, GEN, EQ) +
      listOf(
        Law("Monoid Laws: Left identity") { M.monoidLeftIdentity(GEN, EQ) },
        Law("Monoid Laws: Right identity") { M.monoidRightIdentity(GEN, EQ) },
        Law("Monoid Laws: combineAll should be derived") { M.combineAllIsDerived(GEN, EQ) },
        Law("Monoid Laws: combineAll of empty list is empty") { M.combineAllOfEmptyIsEmpty(EQ) }
      )

  fun <F> Monoid<F>.monoidLeftIdentity(GEN: Gen<F>, EQ: Eq<F>): Unit =
    forAll(GEN) { a ->
      (empty().combine(a)).equalUnderTheLaw(a, EQ)
    }

  fun <F> Monoid<F>.monoidRightIdentity(GEN: Gen<F>, EQ: Eq<F>): Unit =
    forAll(GEN) { a ->
      a.combine(empty()).equalUnderTheLaw(a, EQ)
    }

  fun <F> Monoid<F>.combineAllIsDerived(GEN: Gen<F>, EQ: Eq<F>): Unit =
    forAll(5, Gen.list(GEN)) { list ->
      list.combineAll().equalUnderTheLaw(if (list.isEmpty()) empty() else list.reduce { acc, f -> acc.combine(f) }, EQ)
    }

  fun <F> Monoid<F>.combineAllOfEmptyIsEmpty(EQ: Eq<F>): Unit =
    emptyList<F>().combineAll().equalUnderTheLaw(empty(), EQ) shouldBe true
}
