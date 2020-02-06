package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.test.generators.intSmall
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ApplicativeLaws {

  fun <F> laws(A: Applicative<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> = laws(A, A, GENK, EQK)

  fun <F> laws(A: Applicative<F>, FF: Functor<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val EQ = EQK.liftEq(Int.eq())
    val G = GENK.genK(Gen.int())
    return FunctorLaws.laws(A, GENK, EQK) + listOf(
      Law("Applicative Laws: ap identity") { A.apIdentity(G, EQ) },
      Law("Applicative Laws: homomorphism") { A.homomorphism(EQ) },
      Law("Applicative Laws: interchange") { A.interchange(GENK, EQ) },
      Law("Applicative Laws: map derived") { A.mapDerived(G, FF, EQ) },
      Law("Applicative Laws: cartesian builder map") { A.cartesianBuilderMap(EQ) },
      Law("Applicative Laws: cartesian builder tupled2") { A.cartesianBuilderTupled2(EQ) },
      Law("Applicative Laws: cartesian builder tupled3") { A.cartesianBuilderTupled3(EQ) }
    )
  }

  fun <F> Applicative<F>.apIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      fa.ap(just { n: Int -> n }).equalUnderTheLaw(fa, EQ)
    }

  fun <F> Applicative<F>.homomorphism(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.functionAToB<Int, Int>(Gen.int()), Gen.int()) { ab: (Int) -> Int, a: Int ->
      just(a).ap(just(ab)).equalUnderTheLaw(just(ab(a)), EQ)
    }

  fun <F> Applicative<F>.interchange(GK: GenK<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Gen.functionAToB<Int, Int>(Gen.int())), Gen.int()) { fa: Kind<F, (Int) -> Int>, a: Int ->
      just(a).ap(fa).equalUnderTheLaw(fa.ap(just { x: (Int) -> Int -> x(a) }), EQ)
    }

  fun <F> Applicative<F>.mapDerived(G: Gen<Kind<F, Int>>, FF: Functor<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G, Gen.functionAToB<Int, Int>(Gen.int())) { fa: Kind<F, Int>, f: (Int) -> Int ->
      FF.run { fa.map(f) }.equalUnderTheLaw(fa.ap(just(f)), EQ)
    }

  fun <F> Applicative<F>.cartesianBuilderMap(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.intSmall(), Gen.intSmall(), Gen.intSmall()) { a: Int, b: Int, c: Int ->
      mapN(just(a), just(b), just(c)) { (x, y, z) -> x + y + z }.equalUnderTheLaw(just(a + b + c), EQ)
    }

  fun <F> Applicative<F>.cartesianBuilderTupled2(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.intSmall(), Gen.intSmall()) { a: Int, b: Int ->
      tupledN(just(a), just(b)).map { (x, y) -> x + y }.equalUnderTheLaw(just(a + b), EQ)
    }

  fun <F> Applicative<F>.cartesianBuilderTupled3(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.intSmall(), Gen.intSmall(), Gen.intSmall()) { a: Int, b: Int, c: Int ->
      tupledN(just(a), just(b), just(c)).map { (x, y, z) -> x + y + z }.equalUnderTheLaw(just(a + b + c), EQ)
    }
}
