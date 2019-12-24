package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.typeclasses.Alternative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object AlternativeLaws {

  fun <F> laws(
    AF: Alternative<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> {
    val EQ = EQK.liftEq(Int.eq())
    val cf = GENK.genK(Gen.int())
    val cff = GENK.genK(Gen.functionAToB<Int, Int>(Gen.int()))

    return ApplicativeLaws.laws(AF, GENK, EQK) + MonoidKLaws.laws(AF, GENK, EQK) + listOf(
      Law("Alternative Laws: Right Absorption") { AF.alternativeRightAbsorption(cff, EQ) },
      Law("Alternative Laws: Left Distributivity") { AF.alternativeLeftDistributivity(cf, EQ) },
      /*
      right distributivity is not implemented correctly
        https://github.com/arrow-kt/arrow/issues/1880
       Law("Alternative Laws: Right Distributivity") { AF.alternativeRightDistributivity(cf, cff, EQ) },
       */
      Law("Alternative Laws: alt is associative") { AF.alternativeAssociativity(cf, EQ) }
    )
  }

  fun <F> Alternative<F>.alternativeRightAbsorption(G: Gen<Kind<F, (Int) -> Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G) { fa: Kind<F, (Int) -> Int> ->
      empty<Int>().ap(fa).equalUnderTheLaw(empty(), EQ)
    }

  fun <F> Alternative<F>.alternativeLeftDistributivity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G, G, Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind<F, Int>, fa2: Kind<F, Int>, f: (Int) -> Int ->
      fa.combineK(fa2).map(f).equalUnderTheLaw(fa.map(f).combineK(fa2.map(f)), EQ)
    }

  fun <F> Alternative<F>.alternativeRightDistributivity(
    G: Gen<Kind<F, Int>>,
    GF: Gen<Kind<F, (Int) -> Int>>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(G, GF, GF) { fa: Kind<F, Int>, ff: Kind<F, (Int) -> Int>, fg: Kind<F, (Int) -> Int> ->
      fa.ap(ff.combineK(fg)).equalUnderTheLaw(fa.ap(ff).combineK(fa.ap(fg)), EQ)
    }

  fun <F> Alternative<F>.alternativeAssociativity(
    G: Gen<Kind<F, Int>>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(G, G, G) { fa: Kind<F, Int>, fa2: Kind<F, Int>, fa3: Kind<F, Int> ->
      (fa alt (fa2 alt fa3)).equalUnderTheLaw((fa alt fa2) alt fa3, EQ)
    }
}
