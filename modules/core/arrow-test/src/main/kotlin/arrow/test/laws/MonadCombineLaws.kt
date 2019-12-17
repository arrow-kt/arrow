package arrow.test.laws

import arrow.Kind
import arrow.typeclasses.Apply
import arrow.typeclasses.MonadCombine
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import arrow.typeclasses.Selective

object MonadCombineLaws {

  fun <F> laws(
    MCF: MonadCombine<F>,
    cf: (Int) -> Kind<F, Int>,
    cff: (Int) -> Kind<F, (Int) -> Int>,
    EQ: Eq<Kind<F, Int>>
  ): List<Law> =
    MonadFilterLaws.laws(MCF, cf, EQ) + AlternativeLaws.laws(MCF, cf, cff, EQ)

  fun <F> laws(
    MCF: MonadCombine<F>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    cf: (Int) -> Kind<F, Int>,
    cff: (Int) -> Kind<F, (Int) -> Int>,
    EQ: Eq<Kind<F, Int>>
  ): List<Law> =
    MonadFilterLaws.laws(MCF, FF, AP, SL, cf, EQ) + AlternativeLaws.laws(MCF, cf, cff, EQ)
}
