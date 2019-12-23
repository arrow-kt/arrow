package arrow.test.laws

import arrow.Kind
import arrow.core.Left
import arrow.core.Right
import arrow.core.extensions.eq
import arrow.core.identity
import arrow.mtl.Kleisli
import arrow.test.generators.GenK
import arrow.test.generators.applicative
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

    return SelectiveLaws.laws(M, GENK, EQK) +
      listOf(
        Law("Monad Laws: left identity") { M.leftIdentity(EQ) },
        Law("Monad Laws: right identity") { M.rightIdentity(EQ) },
        Law("Monad Laws: kleisli left identity") { M.kleisliLeftIdentity(EQ) },
        Law("Monad Laws: kleisli right identity") { M.kleisliRightIdentity(EQ) },
        Law("Monad Laws: map / flatMap coherence") { M.mapFlatMapCoherence(EQ) },
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

      return laws(M, GENK, EQK) + listOf(
        Law("Monad Laws: monad instance should preserve behavior of Functor") { M.preservesFunctor(FF, EQ) },
        Law("Monad Laws: monad instance should preserve behavior of Apply") { M.preservesApply(AP, EQ) },
        Law("Monad Laws: monad instance should preserve behavior of Selective") { M.preservesSelective(SL, EQ) }
      )
    }

  fun <F> Monad<F>.leftIdentity(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.functionAToB<Int, Kind<F, Int>>(Gen.int().applicative(this)), Gen.int()) { f: (Int) -> Kind<F, Int>, a: Int ->
      just(a).flatMap(f).equalUnderTheLaw(f(a), EQ)
    }

  fun <F> Monad<F>.rightIdentity(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int().applicative(this)) { fa: Kind<F, Int> ->
      fa.flatMap { just(it) }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Monad<F>.kleisliLeftIdentity(EQ: Eq<Kind<F, Int>>) {
    val M = this
    forAll(Gen.functionAToB<Int, Kind<F, Int>>(Gen.int().applicative(this)), Gen.int()) { f: (Int) -> Kind<F, Int>, a: Int ->
      (Kleisli { n: Int -> just(n) }.andThen(M, Kleisli(f)).run(a).equalUnderTheLaw(f(a), EQ))
    }
  }

  fun <F> Monad<F>.kleisliRightIdentity(EQ: Eq<Kind<F, Int>>) {
    val M = this
    forAll(Gen.functionAToB<Int, Kind<F, Int>>(Gen.int().applicative(this)), Gen.int()) { f: (Int) -> Kind<F, Int>, a: Int ->
      (Kleisli(f).andThen(M, Kleisli { n: Int -> just(n) }).run(a).equalUnderTheLaw(f(a), EQ))
    }
  }

  fun <F> Monad<F>.mapFlatMapCoherence(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.functionAToB<Int, Int>(Gen.int()), Gen.int().applicative(this)) { f: (Int) -> Int, fa: Kind<F, Int> ->
      fa.flatMap { just(f(it)) }.equalUnderTheLaw(fa.map(f), EQ)
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

  fun <F> Monad<F>.preservesSelective(SL: Selective<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.either(Gen.int(), Gen.int())) { either ->
      val f = just<(Int) -> Int>(::identity)
      SL.run { just(either).select(f) }.equalUnderTheLaw(just(either).select(f), EQ)
    }

  fun <F> Monad<F>.preservesFunctor(FF: Functor<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int(), Gen.functionAToB<Int, Int>(Gen.int())) { num, f ->
      FF.run { just(num).map(f) }.equalUnderTheLaw(just(num).map(f), EQ)
    }

  fun <F> Monad<F>.preservesApply(AP: Apply<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int(), Gen.functionAToB<Int, Int>(Gen.int())) { num, f ->
      val ff = just(f)
      AP.run { just(num).ap(ff) }.equalUnderTheLaw(just(num).ap(ff), EQ)

      val fg = just(1)
      AP.run { just(num).followedBy(fg) }.equalUnderTheLaw(just(num).followedBy(fg), EQ)

      AP.run { just(num).apTap(fg) }.equalUnderTheLaw(just(num).apTap(fg), EQ)
    }
}
