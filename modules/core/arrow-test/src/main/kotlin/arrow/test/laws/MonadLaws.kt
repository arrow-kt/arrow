package arrow.test.laws

import arrow.Kind
import arrow.core.Left
import arrow.core.Right
import arrow.core.extensions.eq
import arrow.mtl.Kleisli
import arrow.test.generators.GenK
import arrow.test.generators.either
import arrow.test.generators.functionAToB
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Selective
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadLaws {

  fun <F> laws(M: Monad<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val EQ = EQK.liftEq(Int.eq())
    val G = GENK.genK(Gen.int())

    return SelectiveLaws.laws(M, GENK, EQK) +
      listOf(
        Law("Monad Laws: left identity") { M.leftIdentity(G, EQ) },
        Law("Monad Laws: right identity") { M.rightIdentity(G, EQ) },
        Law("Monad Laws: kleisli left identity") { M.kleisliLeftIdentity(G, EQ) },
        Law("Monad Laws: kleisli right identity") { M.kleisliRightIdentity(G, EQ) },
        Law("Monad Laws: monad comprehensions") { M.monadComprehensions(EQ) },
        Law("Monad Laws: stack safe") { M.stackSafety(5000, EQ) }
      )
  }

    fun <F> laws(
      M: Monad<F>,
      FF: Functor<F>,
      AP: Apply<F>,
      SL: Selective<F>,
      GENK: GenK<F>,
      EQK: EqK<F>
    ): List<Law> {
      val EQ = EQK.liftEq(Int.eq())
      val G = GENK.genK(Gen.int())

      return laws(M, GENK, EQK) + listOf(
        Law("Monad Laws: monad map should be consistent with functor map") { M.derivedMapConsistent(G, FF, EQ) },
        Law("Monad Laws: monad ap should be consistent with applicative ap") { M.derivedApConsistent(GENK, AP, EQ) },
        Law("Monad Laws: monad apTap should be consistent with applicative apTap") { M.derivedApTapConsistent(GENK, AP, EQ) },
        Law("Monad Laws: monad followedBy should be consistent with applicative followedBy") { M.derivedFollowedByConsistent(GENK, AP, EQ) },
        Law("Monad Laws: monad selective should be consistent with selective selective") { M.derivedSelectiveConsistent(GENK, SL, EQ) }
      )
    }

  fun <F> Monad<F>.leftIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.functionAToB<Int, Kind<F, Int>>(G), Gen.int()) { f: (Int) -> Kind<F, Int>, a: Int ->
      just(a).flatMap(f).equalUnderTheLaw(f(a), EQ)
    }

  fun <F> Monad<F>.rightIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      fa.flatMap { just(it) }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Monad<F>.kleisliLeftIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>) {
    val M = this
    forAll(Gen.functionAToB<Int, Kind<F, Int>>(G), Gen.int()) { f: (Int) -> Kind<F, Int>, a: Int ->
      (Kleisli { n: Int -> just(n) }.andThen(M, Kleisli(f)).run(a).equalUnderTheLaw(f(a), EQ))
    }
  }

  fun <F> Monad<F>.kleisliRightIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>) {
    val M = this
    forAll(Gen.functionAToB<Int, Kind<F, Int>>(G), Gen.int()) { f: (Int) -> Kind<F, Int>, a: Int ->
      (Kleisli(f).andThen(M, Kleisli { n: Int -> just(n) }).run(a).equalUnderTheLaw(f(a), EQ))
    }
  }

  fun <F> Monad<F>.stackSafety(iter: Int = 5000, EQ: Eq<Kind<F, Int>>) {
    val res = tailRecM(0) { i -> just(if (i < iter) Left(i + 1) else Right(i)) }
    res.equalUnderTheLaw(just(iter), EQ)
  }

  fun <F> Monad<F>.monadComprehensions(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int()) { num: Int ->
      fx.monad {
        val (a) = just(num)
        val (b) = just(a + 1)
        val (c) = just(b + 1)
        c
      }.equalUnderTheLaw(just(num + 2), EQ)
    }

  fun <F> Monad<F>.derivedSelectiveConsistent(GK: GenK<F>, SL: Selective<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Gen.either(Gen.int(), Gen.int())), GK.genK(Gen.functionAToB<Int, Int>(Gen.int()))) { x, f ->
      SL.run { x.select(f) }.equalUnderTheLaw(x.select(f), EQ)
    }

  fun <F> Monad<F>.derivedMapConsistent(G: Gen<Kind<F, Int>>, FF: Functor<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G, Gen.functionAToB<Int, Int>(Gen.int())) { fa, f ->
      FF.run { fa.map(f) }.equalUnderTheLaw(fa.map(f), EQ)
    }

  fun <F> Monad<F>.derivedApConsistent(GK: GenK<F>, AP: Apply<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Gen.int()), GK.genK(Gen.functionAToB<Int, Int>(Gen.int()))) { fa, ff ->
      AP.run { fa.ap(ff) }.equalUnderTheLaw(fa.ap(ff), EQ)
    }

  fun <F> Monad<F>.derivedFollowedByConsistent(GK: GenK<F>, AP: Apply<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Gen.int()), GK.genK(Gen.int())) { fa, fb ->
      AP.run { fa.followedBy(fb) }.equalUnderTheLaw(fa.followedBy(fb), EQ)
    }

  fun <F> Monad<F>.derivedApTapConsistent(GK: GenK<F>, AP: Apply<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Gen.int()), GK.genK(Gen.int())) { fa, fb ->
      AP.run { fa.apTap(fb) }.equalUnderTheLaw(fa.apTap(fb), EQ)
    }
}
