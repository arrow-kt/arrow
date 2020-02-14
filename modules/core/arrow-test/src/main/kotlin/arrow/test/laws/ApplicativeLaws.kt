package arrow.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.extensions.eq
import arrow.core.extensions.monoid
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.extensions.tuple3.eq.eq
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

    val EQ: Eq<Kind<F, Int>> = EQK.liftEq(Int.eq())
    val G = GENK.genK(Gen.int())
    val EQTuple2: Eq<Kind<F, Tuple2<Int, Int>>> = EQK.liftEq(Tuple2.eq(Int.eq(), Int.eq()))
    val EQTuple3: Eq<Kind<F, Tuple3<Int, Int, Int>>> = EQK.liftEq(Tuple3.eq(Int.eq(), Int.eq(), Int.eq()))
    val EQBoolean: Eq<Kind<F, Boolean>> = EQK.liftEq(Boolean.eq())

    return FunctorLaws.laws(A, GENK, EQK) + listOf(
      Law("Applicative Laws: ap identity") { A.apIdentity(G, EQ) },
      Law("Applicative Laws: homomorphism") { A.homomorphism(EQ) },
      Law("Applicative Laws: interchange") { A.interchange(GENK, EQ) },
      Law("Applicative Laws: map derived") { A.mapDerived(G, FF, EQ) },
      Law("Applicative Laws: cartesian builder map") { A.cartesianBuilderMap(EQTuple3) },
      Law("Applicative Laws: cartesian builder tupled2") { A.cartesianBuilderTupled2(EQTuple2) },
      Law("Applicative Laws: cartesian builder tupled3") { A.cartesianBuilderTupled3(EQTuple3) },
      Law("Applicative Laws: replicate check size") { A.replicateSize(EQ) },
      Law("Applicative Laws: replicate check list == 1") { A.replicateListOf1(EQBoolean) },
      Law("Applicative Laws: replicate monoid") { A.replicateMonoid(EQ) }
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

  fun <F> Applicative<F>.cartesianBuilderMap(EQ: Eq<Kind<F, Tuple3<Int, Int, Int>>>): Unit =
    forAll(Gen.intSmall(), Gen.intSmall(), Gen.intSmall()) { a: Int, b: Int, c: Int ->
      mapN(just(a), just(b), just(c)) { it }.equalUnderTheLaw(just(Tuple3(a, b, c)), EQ)
    }

  fun <F> Applicative<F>.cartesianBuilderTupled2(EQ: Eq<Kind<F, Tuple2<Int, Int>>>): Unit =
    forAll(Gen.intSmall(), Gen.intSmall()) { a: Int, b: Int ->
      tupledN(just(a), just(b)).equalUnderTheLaw(just(Tuple2(a, b)), EQ)
    }

  fun <F> Applicative<F>.cartesianBuilderTupled3(EQ: Eq<Kind<F, Tuple3<Int, Int, Int>>>): Unit =
    forAll(Gen.intSmall(), Gen.intSmall(), Gen.intSmall()) { a: Int, b: Int, c: Int ->
      tupledN(just(a), just(b), just(c)).equalUnderTheLaw(just(Tuple3(a, b, c)), EQ)
    }

  fun <F> Applicative<F>.replicateSize(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.choose(0, 100)) { n ->
      just(1).replicate(n).map { it.size }.equalUnderTheLaw(just(n), EQ)
    }

  fun <F> Applicative<F>.replicateListOf1(EQ: Eq<Kind<F, Boolean>>): Unit =
    forAll(Gen.choose(0, 100)) { n ->
      just(1).replicate(n).map { list -> list.all { it == 1 } }.equalUnderTheLaw(just(true), EQ)
    }

  fun <F> Applicative<F>.replicateMonoid(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.choose(0, 100)) { n ->
      just(1).replicate(n, Int.monoid()).equalUnderTheLaw(just(n), EQ)
    }
}
