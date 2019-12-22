package arrow.test.laws

import arrow.test.generators.GenK
import arrow.typeclasses.Apply
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.MonadCombine
import arrow.typeclasses.Selective

object MonadCombineLaws {

  fun <F> laws(
    MCF: MonadCombine<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    MonadFilterLaws.laws(MCF, GENK, EQK) + AlternativeLaws.laws(MCF, GENK, EQK)

  fun <F> laws(
    MCF: MonadCombine<F>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    MonadFilterLaws.laws(MCF, FF, AP, SL, GENK, EQK) + AlternativeLaws.laws(MCF, GENK, EQK)
}
