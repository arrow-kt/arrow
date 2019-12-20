package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonoidLaws {

  fun <F> laws(M: Monoid<F>, GEN: Gen<F>, EQ: Eq<F>): List<Law> =
    listOf(
      Law("Monoid Laws: Left identity") { M.monoidLeftIdentity(GEN, EQ) },
      Law("Monoid Laws: Right identity") { M.monoidRightIdentity(GEN, EQ) }
    )

  fun <F> Monoid<F>.monoidLeftIdentity(GEN: Gen<F>, EQ: Eq<F>): Unit =
    forAll(GEN) { a ->
      (empty().combine(a)).equalUnderTheLaw(a, EQ)
    }

  fun <F> Monoid<F>.monoidRightIdentity(GEN: Gen<F>, EQ: Eq<F>): Unit =
    forAll(GEN) { a ->
      a.combine(empty()).equalUnderTheLaw(a, EQ)
    }
}
