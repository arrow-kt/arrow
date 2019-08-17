package arrow.test.laws

import arrow.Kind
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.test.generators.functionAAToA
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
    SemigroupalLaws.laws(SF, cf, bijection, EQ2) +
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
      fa.ap(ff).ap(fg).equalUnderTheLaw(fa.ap(fg compose ff), EQ)
    }


  fun <F> Apply<F>.productConsistency(cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>) =
    forAll(
      Gen.int().map(cf),
      Gen.int().map(cf),
      Gen.functionAAToA(Gen.int())
    ) { fa, fb, f ->
      fa.map2(fb) { x -> f(x.a, x.b) }.equalUnderTheLaw(fa.product(fb).map { x -> f(x.a, x.b) }, EQ)
    }

  fun <F> Apply<F>.evalConsistency(cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>) =
    forAll(
      Gen.int().map(cf),
      Gen.int().map(cf),
      Gen.functionAAToA(Gen.int())
    ) { fa, fb, f ->
      fa.map2(fb) { x -> f(x.a, x.b) }.equalUnderTheLaw(fa.map2Eval(Eval.now(fb)) { x -> f(x.a, x.b) }.value(), EQ)
    }
}
