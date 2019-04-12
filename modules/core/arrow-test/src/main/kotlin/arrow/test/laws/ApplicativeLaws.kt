package arrow.test.laws

import arrow.Kind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

object ApplicativeLaws {

  fun <F> laws(A: Applicative<F>, EQ: Eq<Kind<F, Int>>): List<Law> =
    ApplyLaws.laws(A, Gen.int(), EQ) + ApplyCartesianLaws.laws(A, EQ)
}
