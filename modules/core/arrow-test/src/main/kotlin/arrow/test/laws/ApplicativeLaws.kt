package arrow.test.laws

import arrow.Kind
import arrow.test.generators.genApplicative
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ApplicativeLaws {

  inline fun <F> laws(A: Applicative<F>, EQ: Eq<Kind<F, Int>>): List<Law> =
    FunctorLaws.laws(A, EQ) + listOf(
      Law("Applicative Laws: ap identity") { A.apIdentity(EQ) },
      Law("Applicative Laws: homomorphism") { A.homomorphism(EQ) },
      Law("Applicative Laws: interchange") { A.interchange(EQ) },
      Law("Applicative Laws: map derived") { A.mapDerived(EQ) },
      Law("Applicative Laws: cartesian builder map") { A.cartesianBuilderMap(EQ) },
      Law("Applicative Laws: cartesian builder tupled") { A.cartesianBuilderTupled(EQ) }
    )

  fun <F> Applicative<F>.apIdentity(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genApplicative(Gen.int(), this)) { fa: Kind<F, Int> ->
      fa.ap(just({ n: Int -> n })).equalUnderTheLaw(fa, EQ)
    }

  fun <F> Applicative<F>.homomorphism(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genFunctionAToB<Int, Int>(Gen.int()), Gen.int()) { ab: (Int) -> Int, a: Int ->
      just(a).ap(just(ab)).equalUnderTheLaw(just(ab(a)), EQ)
    }

  fun <F> Applicative<F>.interchange(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genApplicative(genFunctionAToB<Int, Int>(Gen.int()), this), Gen.int()) { fa: Kind<F, (Int) -> Int>, a: Int ->
      just(a).ap(fa).equalUnderTheLaw(fa.ap(just({ x: (Int) -> Int -> x(a) })), EQ)
    }

  fun <F> Applicative<F>.mapDerived(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genApplicative(Gen.int(), this), genFunctionAToB<Int, Int>(Gen.int())) { fa: Kind<F, Int>, f: (Int) -> Int ->
      fa.map(f).equalUnderTheLaw(fa.ap(just(f)), EQ)
    }

  fun <F> Applicative<F>.cartesianBuilderMap(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genIntSmall(), genIntSmall(), genIntSmall(), genIntSmall(), genIntSmall(), genIntSmall()) { a: Int, b: Int, c: Int, d: Int, e: Int, f: Int ->
      map(just(a), just(b), just(c), just(d), just(e), just(f)) { (x, y, z, u, v, w) -> x + y + z - u - v - w }.equalUnderTheLaw(just(a + b + c - d - e - f), EQ)
    }

  fun <F> Applicative<F>.cartesianBuilderTupled(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genIntSmall(), genIntSmall(), genIntSmall(), genIntSmall(), genIntSmall(), genIntSmall()) { a: Int, b: Int, c: Int, d: Int, e: Int, f: Int ->
      tupled(just(a), just(b), just(c), just(d), just(e), just(f)).map { (x, y, z, u, v, w) -> x + y + z - u - v - w }.equalUnderTheLaw(just(a + b + c - d - e - f), EQ)
    }
}
