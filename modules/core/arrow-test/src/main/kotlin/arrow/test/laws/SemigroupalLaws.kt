package arrow.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.typeclasses.Eq
import arrow.typeclasses.Semigroupal
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object SemigroupalLaws {

  fun <F> laws(
    SGAL: Semigroupal<F>,
    GEN: Gen<Kind<F, Int>>,
    bijection: (Kind<F, Tuple2<Tuple2<Int, Int>, Int>>) -> (Kind<F, Tuple2<Int, Tuple2<Int, Int>>>),
    EQ: Eq<Kind<F, Tuple2<Int, Tuple2<Int, Int>>>>
  ): List<Law> = listOf(Law("Semigroupal: Bijective associativity") { SGAL.semigroupalAssociative(GEN, bijection, EQ) })

  private fun <F> Semigroupal<F>.semigroupalAssociative(
    GEN: Gen<Kind<F, Int>>,
    bijection: (Kind<F, Tuple2<Tuple2<Int, Int>, Int>>) -> (Kind<F, Tuple2<Int, Tuple2<Int, Int>>>),
    EQ: Eq<Kind<F, Tuple2<Int, Tuple2<Int, Int>>>>
  ) = forAll(GEN, GEN, GEN) { a, b, c ->
    a.product(b.product(c)).equalUnderTheLaw(bijection(a.product(b).product(c)), EQ)
  }
}
