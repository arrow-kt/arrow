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
      f: (Int) -> Kind<F, Int>,
      bijection: (Kind<F, Tuple2<Tuple2<Int, Int>, Int>>) -> (Kind<F, Tuple2<Int, Tuple2<Int, Int>>>),
      EQ: Eq<Kind<F, Tuple2<Int, Tuple2<Int, Int>>>>
    ): List<Law> = listOf(Law("Semigroupal: Bijective associativity") { SGAL.semigroupalAssociative(f, bijection, EQ) })

    private fun <F> Semigroupal<F>.semigroupalAssociative(
      f: (Int) -> Kind<F, Int>,
      bijection: (Kind<F, Tuple2<Tuple2<Int, Int>, Int>>) -> (Kind<F, Tuple2<Int, Tuple2<Int, Int>>>),
      EQ: Eq<Kind<F, Tuple2<Int, Tuple2<Int, Int>>>>
    ) = forAll(Gen.int().map(f), Gen.int().map(f), Gen.int().map(f)) { a, b, c ->
        a.product(b.product(c)).equalUnderTheLaw(bijection(a.product(b).product(c)), EQ)
    }
}
