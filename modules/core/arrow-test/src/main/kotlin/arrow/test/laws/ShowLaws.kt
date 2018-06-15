package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Show
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ShowLaws {

  inline fun <F> laws(S: Show<F>, EQ: Eq<F>, noinline cf: (Int) -> F): List<Law> =
    EqLaws.laws(EQ, cf) + listOf(
      Law("Show Laws: equality", { S.equalShow(EQ, cf) })
    )

  fun <F> Show<F>.equalShow(EQ: Eq<F>, cf: (Int) -> F): Unit =
    forAll(Gen.int(), { int: Int ->
      val a = cf(int)
      val b = cf(int)
      EQ.run { a.eqv(b) } && a.show() == b.show()
    })

}