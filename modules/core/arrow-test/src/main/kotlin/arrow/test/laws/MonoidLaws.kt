package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonoidLaws {

  fun <F> laws(M: Monoid<F>, A: Gen<F>, EQ: Eq<F>): List<Law> =
    listOf(
      Law("Monoid Laws: Left identity") { M.monoidLeftIdentity(A, EQ) },
      Law("Monoid Laws: Right identity") { M.monoidRightIdentity(A, EQ) }
    )

  fun <F> Monoid<F>.monoidLeftIdentity(A: Gen<F>, EQ: Eq<F>): Unit =
    forAll(A) { a ->
      (empty().combine(a)).equalUnderTheLaw(a, EQ)
    }

  fun <F> Monoid<F>.monoidRightIdentity(A: Gen<F>, EQ: Eq<F>): Unit =
    forAll(A) { a ->
    a.combine(empty()).equalUnderTheLaw(a, EQ)
  }
}
