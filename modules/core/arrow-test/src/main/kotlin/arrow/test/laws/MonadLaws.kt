package arrow.test.laws

import arrow.Kind
import arrow.core.Left
import arrow.core.Right
import arrow.data.Kleisli
import arrow.free.Free
import arrow.free.bindingStackSafe
import arrow.free.run
import arrow.test.generators.*
import arrow.typeclasses.Eq
import arrow.typeclasses.Monad
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlinx.coroutines.newSingleThreadContext

object MonadLaws {

  fun <F> laws(M: Monad<F>, EQ: Eq<Kind<F, Int>>): List<Law> =
    ApplicativeLaws.laws(M, EQ) + listOf(
      Law("Monad Laws: left identity") { M.leftIdentity(EQ) },
      Law("Monad Laws: right identity") { M.rightIdentity(EQ) },
      Law("Monad Laws: kleisli left identity") { M.kleisliLeftIdentity(EQ) },
      Law("Monad Laws: kleisli right identity") { M.kleisliRightIdentity(EQ) },
      Law("Monad Laws: map / flatMap coherence") { M.mapFlatMapCoherence(EQ) },
      Law("Monad Laws: monad comprehensions") { M.monadComprehensions(EQ) },
      Law("Monad Laws: monad comprehensions binding in other threads") { M.monadComprehensionsBindInContext(EQ) },
      Law("Monad Laws: stack-safe//unsafe monad comprehensions equivalence") { M.equivalentComprehensions(EQ) },
      Law("Monad Laws: stack safe") { M.stackSafety(5000, EQ) },
      Law("Monad Laws: stack safe comprehensions") { M.stackSafetyComprehensions(5000, EQ) }
    )

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

  fun <F> Monad<F>.stackSafety(iterations: Int = 5000, EQ: Eq<Kind<F, Int>>): Unit =
    forFew(1, Gen.from(listOf(iterations))) { iter ->
      val res = tailRecM(0) { i -> just(if (i < iter) Left(i + 1) else Right(i)) }
      res.equalUnderTheLaw(just(iter), EQ)
    }

  fun <F> Monad<F>.stackSafetyComprehensions(iterations: Int = 5000, EQ: Eq<Kind<F, Int>>): Unit =
    forFew(1, Gen.from(listOf(iterations))) { iter ->
      val res = stackSafeTestProgram(0, iter)
      res.run(this).equalUnderTheLaw(just(iter), EQ)
    }

  fun <F> Monad<F>.equivalentComprehensions(EQ: Eq<Kind<F, Int>>) {
    val M = this
    forAll(Gen.int()) { num: Int ->
      val aa = fx {
        val (a) = just(num)
        val (b) = just(a + 1)
        val (c) = just(b + 1)
        c
      }
      val bb = bindingStackSafe {
        val (a) = just(num)
        val (b) = just(a + 1)
        val (c) = just(b + 1)
        c
      }.run(M)
      aa.equalUnderTheLaw(bb, EQ) &&
          aa.equalUnderTheLaw(just(num + 2), EQ)
    }
  }

  fun <F> Monad<F>.monadComprehensions(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int()) { num: Int ->
      fx {
        val (a) = just(num)
        val (b) = just(a + 1)
        val (c) = just(b + 1)
        c
      }.equalUnderTheLaw(just(num + 2), EQ)
    }

  fun <F> Monad<F>.monadComprehensionsBindInContext(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(5, Gen.intSmall()) { num: Int ->
      fx {
        val a = bindIn(newSingleThreadContext("$num")) { num + 1 }
        val b = bindIn(newSingleThreadContext("$a")) { a + 1 }
        b
      }.equalUnderTheLaw(just(num + 2), EQ)
    }

  fun <F> Monad<F>.stackSafeTestProgram(n: Int, stopAt: Int): Free<F, Int> = bindingStackSafe {
    val (v) = this.just(n + 1)
    val r = if (v < stopAt) stackSafeTestProgram(v, stopAt).bind() else this.just(v).bind()
    r
  }
}
