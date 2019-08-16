package arrow.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.Semigroupal
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ApplyLaws {
  fun <F> laws(
    AF: Apply<F>,
    SF: Semigroupal<F>,
    EQ1: Eq<Kind<F, Int>>,
    cf: (Int) -> Kind<F, Int>,
    cff: (Int) -> Kind<F, (Int) -> Int>,
    bijection: (Kind<F, Tuple2<Tuple2<Int, Int>, Int>>) -> (Kind<F, Tuple2<Int, Tuple2<Int, Int>>>),
    EQ2: Eq<Kind<F, Tuple2<Int, Tuple2<Int, Int>>>>
  ): List<Law> =
    FunctorLaws.laws(AF, cf, EQ1) + SemigroupalLaws.laws(SF, cf, bijection, EQ2) +
      listOf(
        Law("Apply Laws: Composition") { AF.composition(cf, cff, EQ1) },
        Law("Apply Laws: Product Consistency") { AF.productConsistency(cf, EQ1) },
        Law("Apply Laws: Eval Consistency") { AF.evalConsistency(cf, EQ1) }
      )

  fun <F> Apply<F>.composition(cf: (Int) -> Kind<F, Int>, cff: (Int) -> Kind<F, (Int) -> Int>, EQ: Eq<Kind<F, Int>>) =
    forAll(
      Gen.int().map(cf),
      Gen.int().map(cff),
      Gen.int().map(cff)
    ) { fa: Kind<F, Int>, ff: Kind<F, (Int) -> Int>, fg: Kind<F, (Int) -> Int> ->
      // val compose = { g: (Int) -> Int -> { f: (Int) -> Int -> f andThen g }}//({it})({it})(3)
      // fa.ap(ff).ap(fg).equalUnderTheLaw()
      true
    }


  /*forAll(
    Gen.int().map(f),
    Gen.functionAToB<Int, Int>(Gen.int()),
    Gen.functionAToB<Int, Int>(Gen.int()),
    Gen.functionAToB<Int, Int>(Gen.int()),
    Gen.functionAToB<Int, Int>(Gen.int())
  ) { fa: Kind2<F, (Int) -> Int>, ff, g, x, y ->
    fa.bimap(ff, g).bimap(x, y).equalUnderTheLaw(fa.bimap(ff andThen x, g andThen y), EQ)
  }*/


  fun <F> Apply<F>.productConsistency(cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit = TODO()

  fun <F> Apply<F>.evalConsistency(cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit = TODO()
}
