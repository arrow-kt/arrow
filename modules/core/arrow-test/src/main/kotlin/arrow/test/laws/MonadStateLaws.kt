package arrow.test.laws

import arrow.Kind
import arrow.mtl.typeclasses.MonadState
import arrow.test.generators.intSmall
import arrow.test.laws.MonadStateLaws.monadStateGetIdempotent
import arrow.test.laws.MonadStateLaws.monadStateGetSet
import arrow.test.laws.MonadStateLaws.monadStateSetGet
import arrow.test.laws.MonadStateLaws.monadStateSetTwice
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import arrow.typeclasses.Selective
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadStateLaws {

  private fun <F> monadStateLaws(M: MonadState<F, Int>, EQ: Eq<Kind<F, Int>>, EQUnit: Eq<Kind<F, Unit>>): List<Law> = listOf(
    Law("Monad State Laws: idempotence") { M.monadStateGetIdempotent(EQ) },
    Law("Monad State Laws: set twice eq to set once the last element") { M.monadStateSetTwice(EQUnit) },
    Law("Monad State Laws: set get") { M.monadStateSetGet(EQ) },
    Law("Monad State Laws: get set") { M.monadStateGetSet(EQUnit) }
  )

  fun <F> laws(
    M: MonadState<F, Int>,
    EQ: Eq<Kind<F, Int>>,
    EQUnit: Eq<Kind<F, Unit>>
  ): List<Law> =
    MonadLaws.laws(M, EQ) + monadStateLaws(M, EQ, EQUnit)

  fun <F> laws(
    M: MonadState<F, Int>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    EQ: Eq<Kind<F, Int>>,
    EQUnit: Eq<Kind<F, Unit>>
  ): List<Law> =
    MonadLaws.laws(M, FF, AP, SL, EQ) + monadStateLaws(M, EQ, EQUnit)

  fun <F> MonadState<F, Int>.monadStateGetIdempotent(EQ: Eq<Kind<F, Int>>) {
    get().flatMap { get() }.equalUnderTheLaw(get(), EQ)
  }

  fun <F> MonadState<F, Int>.monadStateSetTwice(EQ: Eq<Kind<F, Unit>>) {
    forAll(Gen.intSmall(), Gen.intSmall()) { s: Int, t: Int ->
      set(s).flatMap { set(t) }.equalUnderTheLaw(set(t), EQ)
    }
  }

  fun <F> MonadState<F, Int>.monadStateSetGet(EQ: Eq<Kind<F, Int>>) {
    forAll(Gen.intSmall()) { s: Int ->
      set(s).flatMap { get() }.equalUnderTheLaw(set(s).flatMap { just(s) }, EQ)
    }
  }

  fun <F> MonadState<F, Int>.monadStateGetSet(EQ: Eq<Kind<F, Unit>>) {
    get().flatMap { set(it) }.equalUnderTheLaw(just(Unit), EQ)
  }
}
