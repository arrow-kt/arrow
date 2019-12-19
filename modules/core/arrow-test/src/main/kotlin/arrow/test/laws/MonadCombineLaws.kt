package arrow.test.laws

import arrow.Kind
import arrow.typeclasses.Apply
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.MonadCombine
import arrow.typeclasses.Selective

object MonadCombineLaws {

  fun <F> laws(
    MCF: MonadCombine<F>,
    cf: (Int) -> Kind<F, Int>,
    cff: (Int) -> Kind<F, (Int) -> Int>,
    EQK: EqK<F>
  ): List<Law> =
    MonadFilterLaws.laws(MCF, cf, EQK) + AlternativeLaws.laws(MCF, cf, cff, EQK)

  fun <F> laws(
    MCF: MonadCombine<F>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    cf: (Int) -> Kind<F, Int>,
    cff: (Int) -> Kind<F, (Int) -> Int>,
    EQK: EqK<F>
  ): List<Law> =
    MonadFilterLaws.laws(MCF, FF, AP, SL, cf, EQK) + AlternativeLaws.laws(MCF, cf, cff, EQK)
}
