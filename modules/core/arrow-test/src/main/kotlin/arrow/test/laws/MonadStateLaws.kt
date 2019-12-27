package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.mtl.typeclasses.MonadState
import arrow.test.generators.GenK
import arrow.test.generators.intSmall
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.Selective
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadStateLaws {

  private fun <F> monadStateLaws(M: MonadState<F, Int>, EQK: EqK<F>): List<Law> {

    val EQ = EQK.liftEq(Int.eq())
    val EQUnit = EQK.liftEq(Eq.any())

    return listOf(
      Law("Monad State Laws: idempotence") { M.monadStateGetIdempotent(EQ) },
      Law("Monad State Laws: set twice eq to set once the last element") { M.monadStateSetTwice(EQUnit) },
      Law("Monad State Laws: set get") { M.monadStateSetGet(EQ) },
      Law("Monad State Laws: get set") { M.monadStateGetSet(EQUnit) }
    )
  }

  fun <F> laws(
    M: MonadState<F, Int>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    MonadLaws.laws(M, GENK, EQK) + monadStateLaws(M, EQK)

  fun <F> laws(
    M: MonadState<F, Int>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    MonadLaws.laws(M, FF, AP, SL, GENK, EQK) + monadStateLaws(M, EQK)

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
