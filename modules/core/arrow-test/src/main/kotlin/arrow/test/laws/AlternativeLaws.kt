package arrow.test.laws

import arrow.Kind
import arrow.test.generators.functionAToB
import arrow.typeclasses.Alternative
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object AlternativeLaws {

  fun <F> laws(
    AF: Alternative<F>,
    cf: (Int) -> Kind<F, Int>,
    cff: (Int) -> Kind<F, (Int) -> Int>,
    EQ: Eq<Kind<F, Int>>
  ): List<Law> =
    ApplicativeLaws.laws(AF, EQ) + MonoidKLaws.laws(AF, AF, EQ) + listOf(
      Law("Alternative Laws: Right Absorption") { AF.alternativeRightAbsorption(cff, EQ) },
      Law("Alternative Laws: Left Distributivity") { AF.alternativeLeftDistributivity(cf, EQ) },
      Law("Alternative Laws: Right Distributivity") { AF.alternativeRightDistributivity(cf, cff, EQ) },
      Law("Alternative Laws: alt is associative") { AF.alternativeAssociativity(cf, EQ) }
    )

  fun <F> Alternative<F>.alternativeRightAbsorption(cff: (Int) -> Kind<F, (Int) -> Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int().map(cff)) { fa: Kind<F, (Int) -> Int> ->
      empty<Int>().ap(fa).equalUnderTheLaw(empty(), EQ)
    }

  fun <F> Alternative<F>.alternativeLeftDistributivity(cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int().map(cf), Gen.int().map(cf), Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind<F, Int>, fa2: Kind<F, Int>, f: (Int) -> Int ->
      fa.combineK(fa2).map(f).equalUnderTheLaw(fa.map(f).combineK(fa2.map(f)), EQ)
    }

  fun <F> Alternative<F>.alternativeRightDistributivity(
    cf: (Int) -> Kind<F, Int>,
    cff: (Int) -> Kind<F, (Int) -> Int>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(Gen.int().map(cf), Gen.int().map(cff), Gen.int().map(cff)
    ) { fa: Kind<F, Int>, ff: Kind<F, (Int) -> Int>, fg: Kind<F, (Int) -> Int> ->
      fa.ap(ff.combineK(fg)).equalUnderTheLaw(fa.ap(ff).combineK(fa.ap(fg)), EQ)
    }

  fun <F> Alternative<F>.alternativeAssociativity(
    cf: (Int) -> Kind<F, Int>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(Gen.int().map(cf), Gen.int().map(cf), Gen.int().map(cf)) {
      fa: Kind<F, Int>, fa2: Kind<F, Int>, fa3: Kind<F, Int> ->
        (fa alt (fa2 alt fa3)).equalUnderTheLaw((fa alt fa2) alt fa3, EQ)
    }
}
