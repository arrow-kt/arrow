package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Show
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ShowLaws {

  fun <F> laws(S: Show<F>, EQ: Eq<F>, G: Gen<F>): List<Law> =
    EqLaws.laws(EQ, G) + listOf(
      Law("Show Laws: equality") { S.equalShow(EQ, G) }
    )

  fun <F> Show<F>.equalShow(EQ: Eq<F>, G: Gen<F>): Unit =
    forAll(G, G) { a, b ->
      if (EQ.run { a.eqv(b) })
        a.show() == b.show()
      else
        true
    }
}
