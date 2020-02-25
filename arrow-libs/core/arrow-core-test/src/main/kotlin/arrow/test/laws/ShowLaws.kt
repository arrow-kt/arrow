package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Show
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ShowLaws {

  fun <F> laws(S: Show<F>, EQ: Eq<F>, GEN: Gen<F>): List<Law> =
    EqLaws.laws(EQ, GEN) + listOf(
      Law("Show Laws: equality") { S.equalShow(EQ, GEN) }
    )

  fun <F> Show<F>.equalShow(EQ: Eq<F>, GEN: Gen<F>): Unit =
    forAll(GEN, GEN) { a, b ->
      if (EQ.run { a.eqv(b) })
        a.show() == b.show()
      else
        true
    }
}
