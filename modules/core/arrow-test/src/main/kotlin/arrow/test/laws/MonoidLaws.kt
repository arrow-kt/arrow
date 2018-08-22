package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid

object MonoidLaws {

  inline fun <F> laws(M: Monoid<F>, A: F, EQ: Eq<F>): List<Law> =
    listOf(
      Law("Monoid Laws: Left identity") { M.monoidLeftIdentity(A, EQ) },
      Law("Monoid Laws: Right identity") { M.monoidRightIdentity(A, EQ) }
    )

  fun <F> Monoid<F>.monoidLeftIdentity(A: F, EQ: Eq<F>): Boolean =
    (empty().combine(A)).equalUnderTheLaw(A, EQ)

  fun <F> Monoid<F>.monoidRightIdentity(A: F, EQ: Eq<F>): Boolean =
    A.combine(empty()).equalUnderTheLaw(A, EQ)

}
