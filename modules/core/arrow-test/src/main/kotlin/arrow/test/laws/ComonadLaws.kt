package arrow.test.laws

import arrow.Kind
import arrow.mtl.Cokleisli
import arrow.test.generators.functionAToB
import arrow.typeclasses.Comonad
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ComonadLaws {

  fun <F> laws(CM: Comonad<F>, G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): List<Law> =
    FunctorLaws.laws(CM, G, EQ) + listOf(
      Law("Comonad Laws: duplicate then extract is identity") { CM.duplicateThenExtractIsId(G, EQ) },
      Law("Comonad Laws: duplicate then map into extract is identity") { CM.duplicateThenMapExtractIsId(G, EQ) },
      Law("Comonad Laws: map and coflatMap are coherent") { CM.mapAndCoflatmapCoherence(G, EQ) },
      Law("Comonad Laws: left identity") { CM.comonadLeftIdentity(G, EQ) },
      Law("Comonad Laws: right identity") { CM.comonadRightIdentity(G, EQ) },
      Law("Comonad Laws: cokleisli left identity") { CM.cokleisliLeftIdentity(G, EQ) },
      Law("Comonad Laws: cokleisli right identity") { CM.cokleisliRightIdentity(G, EQ) },
      Law("Comonad Laws: cobinding") { CM.cobinding(G, EQ) }
    )

  fun <F> Comonad<F>.duplicateThenExtractIsId(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      fa.duplicate().extract().equalUnderTheLaw(fa, EQ)
    }

  fun <F> Comonad<F>.duplicateThenMapExtractIsId(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      fa.duplicate().map { it.extract() }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Comonad<F>.mapAndCoflatmapCoherence(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G, Gen.functionAToB<Int, Int>(Gen.int())) { fa: Kind<F, Int>, f: (Int) -> Int ->
      fa.map(f).equalUnderTheLaw(fa.coflatMap { f(it.extract()) }, EQ)
    }

  fun <F> Comonad<F>.comonadLeftIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      fa.coflatMap { it.extract() }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Comonad<F>.comonadRightIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G, Gen.functionAToB<Kind<F, Int>, Kind<F, Int>>(G)) { fa: Kind<F, Int>, f: (Kind<F, Int>) -> Kind<F, Int> ->
      fa.coflatMap(f).extract().equalUnderTheLaw(f(fa), EQ)
    }

  fun <F> Comonad<F>.cokleisliLeftIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>) {
    val MM = this
    forAll(G, Gen.functionAToB<Kind<F, Int>, Kind<F, Int>>(G)) { fa: Kind<F, Int>, f: (Kind<F, Int>) -> Kind<F, Int> ->
      Cokleisli(MM) { hk: Kind<F, Int> -> hk.extract() }.andThen(Cokleisli(MM, f)).run(fa).equalUnderTheLaw(f(fa), EQ)
    }
  }

  fun <F> Comonad<F>.cokleisliRightIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>) {
    val MM = this
    forAll(G, Gen.functionAToB<Kind<F, Int>, Kind<F, Int>>(G)) { fa: Kind<F, Int>, f: (Kind<F, Int>) -> Kind<F, Int> ->
      Cokleisli(MM, f).andThen(Cokleisli(MM) { hk: Kind<F, Kind<F, Int>> -> hk.extract() }).run(fa).equalUnderTheLaw(f(fa), EQ)
    }
  }

  // TODO: better understand this test as it fails when Nel has length > 1
  fun <F> Comonad<F>.cobinding(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      val comonad = fx.comonad {
        val x = fa.extract()
        val y = extract { fa.map { it + x } }
        fa.map { x + y }
      }
      val b = fa.map { it * 3 }
      comonad.equalUnderTheLaw(b, EQ)
    }
}
